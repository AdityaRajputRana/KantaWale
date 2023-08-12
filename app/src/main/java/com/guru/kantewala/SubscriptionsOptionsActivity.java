package com.guru.kantewala;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.style.SubscriptSpan;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.guru.kantewala.Adapters.ChecklistRVAdapter;
import com.guru.kantewala.Helpers.PhonePePaymentsHelper;
import com.guru.kantewala.Helpers.SubscriptionPaymentHelper;
import com.guru.kantewala.Models.State;
import com.guru.kantewala.Models.SubscriptionPack;
import com.guru.kantewala.Tools.Constants;
import com.guru.kantewala.databinding.ActivitySubscriptionsOptionsBinding;
import com.guru.kantewala.databinding.DialogStateVerficationBinding;
import com.guru.kantewala.databinding.ItemUpiAppBinding;
import com.guru.kantewala.rest.requests.VerifyLessonPaymentReq;
import com.guru.kantewala.rest.response.UnlockedStatesRP;
import com.phonepe.intent.sdk.api.UPIApplicationInfo;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import java.util.ArrayList;

public class SubscriptionsOptionsActivity extends AppCompatActivity  implements SubscriptionPaymentHelper.Listener,PhonePePaymentsHelper.Listener, PaymentResultWithDataListener {

    ActivitySubscriptionsOptionsBinding binding;
    ChecklistRVAdapter adapter;
    ChecklistRVAdapter.ChecklistListener checklistListener;
    SubscriptionPack pack;
    SubscriptionPaymentHelper helper;
    PhonePePaymentsHelper phonePeHelper;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 108){
            phonePeHelper.verifySuccess(data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubscriptionsOptionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        helper = SubscriptionPaymentHelper.newInstance(getApplicationContext(), this, this);
        phonePeHelper = PhonePePaymentsHelper.newInstance(getApplicationContext(), this, this);
        loadLocalData();
        setListeners();
        loadData();
    }



    private void loadLocalData() {
        pack = new Gson().fromJson(getIntent().getStringExtra("pack"), SubscriptionPack.class);
    }

    private void setListeners() {
        checklistListener = this::showCheckout;
        binding.continueBtn.setOnClickListener(view-> verifyCheckoutRequirements());
    }

    private void loadData() {
        if (pack.getNoOfStates() >= Constants.getIndianStatesArrayList().size()){
            forceCheckout();
            return;
        } else {
            binding.progressBar.setVisibility(View.VISIBLE);
            UnlockedStatesRP.getUnlockedStates(new UnlockedStatesRP.OnStatesListener() {
                @Override
                public void onGotUnlockedStates(ArrayList<Integer> states) {
                    if (states == null){
                       binding.progressBar.setVisibility(View.GONE);
                    } else {
                        loadStatesList(states);
                    }
                }
            }, this);
        }
    }

    private void loadStatesList(ArrayList<Integer> unlockedStates) {
        adapter = new ChecklistRVAdapter(Constants.getIndianStatesArrayList(), binding.searchEt, checklistListener, pack.getNoOfStates(),
                binding.recyclerView, this, unlockedStates);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.progressBar.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.VISIBLE);
        binding.continueBtn.setVisibility(View.GONE);
    }

    private void showCheckout(boolean toShow) {
        if (toShow)
            binding.continueBtn.setVisibility(View.VISIBLE);
        else
            binding.continueBtn.setVisibility(View.GONE);
    }


    private void forceCheckout(){
        ArrayList<State> selectedStateOptions = new ArrayList<State>();
        ArrayList<String> mStates = Constants.getIndianStatesArrayList();
        for (int i = 0; i < mStates.size(); i++){
            selectedStateOptions.add(new State(mStates.get(i), i+1));
        }
        binding.recyclerView.setVisibility(View.GONE);
        binding.searchEt.setVisibility(View.GONE);
        binding.titleTxt.setText("Initiating Payment");
        binding.continueBtn.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);
        showCustomPaymentOptions(selectedStateOptions);
        //Todo: Launch Payment Helper from here (FETCH ORDER ID, EXECUTE PAYMENT, SEND CONFIRMATION TO SERVER)
        //Todo: Server: Save SubOptions, generate order id, verify payment and activate subscription)
    }

    private void showCustomPaymentOptions(ArrayList<State> selectedStateOptions){
        boolean phonePay = true;
        if (phonePay){
            phonePeHelper.generateOrder(selectedStateOptions, pack);
        } else {
            helper.generateOrder(selectedStateOptions, pack);
        }
    }


//    private void showPhonePePaymentsPage(ArrayList<State> selectedStateOptions) {
//        binding.paymentsLayout.setVisibility(View.VISIBLE);
//        for (UPIApplicationInfo app: phonePeHelper.getUpiApps()){
//            ItemUpiAppBinding binding = ItemUpiAppBinding.inflate(getLayoutInflater());
//            binding.appName.setText(app.getApplicationName());
//            try {
//                Drawable icon = this.getPackageManager().getApplicationIcon(app.getPackageName());
//                binding.imageView.setImageDrawable(icon);
//                SubscriptionsOptionsActivity.this.binding.paymentsLayout.addView(binding.getRoot());
//            } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
//            }
//
//            binding.getRoot().setOnClickListener(view->launchPhonePeUPICheckoutFlow(app, selectedStateOptions));
//        }
//    }
//
//    private void launchPhonePeUPICheckoutFlow(UPIApplicationInfo app, ArrayList<State> selectedStateOptions) {
//        binding.paymentsLayout.setVisibility(View.GONE);
//        phonePeHelper.generateOrder(selectedStateOptions, pack, app);
//    }

    private void confirmCheckout(){
        ArrayList<State> selectedStateOptions = adapter.getSelectedStates();
        String message = "";
        for (State state: selectedStateOptions){
            message += ", " + state.getName();
        }
        if (!message.isEmpty()){
            message = message.substring(2);
        }
//        message = "You have selected following states for premium access. Click CONFIRM to proceed.\n\n" + message;
        DialogStateVerficationBinding stateBinding = DialogStateVerficationBinding.inflate(getLayoutInflater());
        stateBinding.statesTxt.setText(message);
        stateBinding.priceTxt.setText(stateBinding.priceTxt.getText().toString() + pack.getPrice());
        AlertDialog sDialog = new AlertDialog.Builder(this)
                .setView(stateBinding.getRoot())
                .setCancelable(true)
                .show();

        stateBinding.continueBtn.setOnClickListener(view->{
            sDialog.dismiss();
            checkout();
        });
        stateBinding.cancelButton.setOnClickListener(view->sDialog.dismiss());
        sDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void checkout(){
        ArrayList<State> selectedStateOptions = adapter.getSelectedStates();
        binding.recyclerView.setVisibility(View.GONE);
        binding.searchEt.setVisibility(View.GONE);
        binding.titleTxt.setText("Initiating Payment");
        binding.continueBtn.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);
        showCustomPaymentOptions(selectedStateOptions);
    }
    private void verifyCheckoutRequirements(){
        ArrayList<State> selectedStateOptions = adapter.getSelectedStates();
        if (selectedStateOptions.size() != pack.getNoOfStates()){
            return;
        }
        confirmCheckout();
        }

    @Override
    public void success(String message) {
        Toast.makeText(this, "Thanks for becoming a member - " + message, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void failed(String error) {
        binding.progressBar.setVisibility(View.GONE);
        Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
//        Methods.showError(SubscriptionsOptionsActivity.this, s);
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        helper.success(new Gson().toJson(
                new VerifyLessonPaymentReq(paymentData)
        ));
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        helper.fail(s);
    }
}