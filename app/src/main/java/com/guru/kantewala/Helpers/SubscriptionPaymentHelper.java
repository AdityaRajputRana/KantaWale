package com.guru.kantewala.Helpers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.guru.kantewala.Models.Order;
import com.guru.kantewala.Models.State;
import com.guru.kantewala.Models.SubscriptionPack;
import com.guru.kantewala.R;
import com.guru.kantewala.Tools.Methods;
import com.guru.kantewala.rest.api.APIMethods;
import com.guru.kantewala.rest.api.interfaces.APIResponseListener;
import com.guru.kantewala.rest.response.LessonOrderIdRp;
import com.guru.kantewala.rest.response.MessageRP;
import com.guru.kantewala.rest.response.UserRP;
import com.razorpay.Checkout;
import com.razorpay.PayloadHelper;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;

public class SubscriptionPaymentHelper {
    FirebaseUser user;
    UserRP mUser;

    public interface Listener{
        void success(String s);
        void failed(String s);
    }
    private Context applicationContext;
    private Checkout checkout;

    private Listener listener;
    private LessonOrderIdRp orderIdRp;

    private Order order;
    private Activity activity;

    private PaymentResultListener paymentResultListener;

    private static SubscriptionPaymentHelper SubscriptionPaymentHelperInstance;

    public static SubscriptionPaymentHelper getInstance(){
        return SubscriptionPaymentHelperInstance;
    }

    private SubscriptionPaymentHelper(Context context, Activity activity, Listener listener){
        this.applicationContext = context;
        this.activity = activity;
        this.listener = listener;
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        APIMethods.getUserProfile(activity, new APIResponseListener<UserRP>() {
            @Override
            public void success(UserRP response) {
                mUser = response;

            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                mUser = null;
            }
        });
        Checkout.preload(context);
    }

    public static SubscriptionPaymentHelper newInstance(Context context, Activity activity, Listener listener){
        SubscriptionPaymentHelperInstance = new SubscriptionPaymentHelper(context, activity, listener);

        return SubscriptionPaymentHelperInstance;
    }

    public void startPayments(LessonOrderIdRp orderIdRp){
        this.orderIdRp = orderIdRp;
        this.order = orderIdRp.getOrder();
        createPaymentJSON();
    }

    public void generateOrder(ArrayList<State> states, SubscriptionPack pack){
        APIMethods.getOrderId(activity, states, pack, new APIResponseListener<LessonOrderIdRp>() {
            @Override
            public void success(LessonOrderIdRp response) {
                startPayments(response);
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                listener.failed(message);
                Methods.showFailedAlert(activity, code, message, redirectLink, retry, cancellable);
            }
        });
    }

    public void success(String s){
        Log.i("PHPayment", "success: "+  s);
        verifySuccess(s);
    }

    private void verifySuccess(String s) {
        APIMethods.verifyPayment(activity, s, orderIdRp.getOrder().getId(), new APIResponseListener<MessageRP>() {
            @Override
            public void success(MessageRP response) {
                listener.success(response.getMessage());
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                listener.failed("Order Verification Failed");
                Methods.showFailedAlert(activity, code, message, redirectLink, retry, cancellable);
            }
        });
    }

    public void fail(String s){
        Log.i("PHPayment", "fail: "+  s);
        String error = s;
        try{
            JSONObject object = new JSONObject(s);
            error = object.getJSONObject("error").getString("description");
        } catch (Exception e){
            e.printStackTrace();
        }
        Methods.showError(activity, error);
        listener.failed(error);
    }



    private void createPaymentJSON() {
        checkout = new Checkout();
        checkout.setKeyID(orderIdRp.getApi_key());
        checkout.setImage(R.mipmap.ic_launcher);

        PayloadHelper payloadHelper = new PayloadHelper(order.getCurrency(), order.getAmount(), order.getId());

        payloadHelper.setName(applicationContext.getString(R.string.app_name));
        payloadHelper.setDescription("Receipt: "+ order.getReceipt());
        payloadHelper.setColor("#" + Integer.toHexString(applicationContext.getResources().getColor(R.color.color_bg))
                .toUpperCase());
        payloadHelper.setRetryEnabled(true);
        payloadHelper.setRetryMaxCount(3);
        payloadHelper.setSendSmsHash(true);
        payloadHelper.setPrefillName(user.getDisplayName());
        payloadHelper.setPrefillContact(user.getPhoneNumber());
        if (mUser != null && mUser.getEmail() != null && !mUser.getEmail().isEmpty()){
            payloadHelper.setPrefillEmail(mUser.getEmail());
        }

        JSONObject options = payloadHelper.getJson();
        checkout.open(activity, options);
    }
}
