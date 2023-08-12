package com.guru.kantewala.Helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.guru.kantewala.Models.State;
import com.guru.kantewala.Models.SubscriptionPack;
import com.guru.kantewala.R;
import com.guru.kantewala.SubscriptionsOptionsActivity;
import com.guru.kantewala.Tools.Methods;
import com.guru.kantewala.rest.api.APIMethods;
import com.guru.kantewala.rest.api.interfaces.APIResponseListener;
import com.guru.kantewala.rest.response.LessonOrderIdRp;
import com.guru.kantewala.rest.response.MessageRP;
import com.guru.kantewala.rest.response.PhonePeOrderRP;
import com.guru.kantewala.rest.response.UserRP;
import com.phonepe.intent.sdk.api.B2BPGRequest;
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder;
import com.phonepe.intent.sdk.api.PhonePe;
import com.phonepe.intent.sdk.api.PhonePeInitException;
import com.phonepe.intent.sdk.api.UPIApplicationInfo;
import com.razorpay.Checkout;
import com.razorpay.PayloadHelper;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PhonePePaymentsHelper {
    FirebaseUser user;
    UserRP mUser;
    private List<UPIApplicationInfo> upiApps;
    private UPIApplicationInfo app = new UPIApplicationInfo("test", "app", 78);



    public List<UPIApplicationInfo> getUpiApps() {
        if (upiApps == null){
            return new ArrayList<>();
        }
        return upiApps;
    }

    public interface Listener{
        void success(String s);
        void failed(String s);
    }
    private Context applicationContext;
    private Checkout checkout;

    private PhonePePaymentsHelper.Listener listener;
    private PhonePeOrderRP phonePeOrderRP;

    private Activity activity;

    private PaymentResultListener paymentResultListener;


    private static PhonePePaymentsHelper phonePePaymentsHelperInstance;

    public static PhonePePaymentsHelper getInstance(){
        return phonePePaymentsHelperInstance;
    }

    private PhonePePaymentsHelper(Context context, Activity activity, PhonePePaymentsHelper.Listener listener){
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

        PhonePe.init(context);
        try {
            PhonePe.setFlowId(this.user.getUid());
            upiApps = PhonePe.getUpiApps();
        } catch (PhonePeInitException exception) {
            exception.printStackTrace();
        }
    }

    public static PhonePePaymentsHelper newInstance(Context context, Activity activity, PhonePePaymentsHelper.Listener listener){
        phonePePaymentsHelperInstance = new PhonePePaymentsHelper(context, activity, listener);

        return phonePePaymentsHelperInstance;
    }

    public void startPayments(PhonePeOrderRP orderIdRp){
        this.phonePeOrderRP = orderIdRp;
        createPaymentJSON();
    }

    public void generateOrder(ArrayList<State> states, SubscriptionPack pack){
        APIMethods.generatePhonePeOrder(activity, states, pack, new APIResponseListener<PhonePeOrderRP>() {
            @Override
            public void success(PhonePeOrderRP response) {
                startPayments(response);
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                listener.failed(message);
                Methods.showFailedAlert(activity, code, message, redirectLink, retry, cancellable);
            }
        }, app.getPackageName());
    }



    public void verifySuccess(Intent data) {
        APIMethods.verifyPhonePeOrder(activity, phonePeOrderRP.getOrderId(), new APIResponseListener<MessageRP>() {
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
        B2BPGRequest b2BPGRequest = new B2BPGRequestBuilder()
                .setData(phonePeOrderRP.getBase64Body())
                .setChecksum(phonePeOrderRP.getCheckSum())
                .setUrl(phonePeOrderRP.getPhonePeEndPoint())
                .build();

        int B2B_PG_REQUEST_CODE = 108;

        try {
            Log.i("eta pp", "Starting payment");
            activity.startActivityForResult(PhonePe.getImplicitIntent(
                    activity, b2BPGRequest, app.getPackageName()), B2B_PG_REQUEST_CODE
            );
        } catch (PhonePeInitException e){
            Log.i("eta pp ini failed", e.getMessage());
            fail("Initiation Failure: "+ e.getMessage());
        }
    }


}
