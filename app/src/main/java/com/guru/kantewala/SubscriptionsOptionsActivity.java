package com.guru.kantewala;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.guru.kantewala.Adapters.ChecklistRVAdapter;
import com.guru.kantewala.Helpers.SubscriptionPaymentHelper;
import com.guru.kantewala.Models.State;
import com.guru.kantewala.Models.SubscriptionPack;
import com.guru.kantewala.Tools.Constants;
import com.guru.kantewala.Tools.Methods;
import com.guru.kantewala.databinding.ActivitySubscriptionsOptionsBinding;
import com.guru.kantewala.rest.requests.VerifyLessonPaymentReq;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import java.util.ArrayList;

public class SubscriptionsOptionsActivity extends AppCompatActivity  implements SubscriptionPaymentHelper.Listener, PaymentResultWithDataListener {

    ActivitySubscriptionsOptionsBinding binding;
    ChecklistRVAdapter adapter;
    ChecklistRVAdapter.ChecklistListener checklistListener;
    SubscriptionPack pack;
    SubscriptionPaymentHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubscriptionsOptionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadLocalData();
        setListeners();
        loadData();
        helper = SubscriptionPaymentHelper.newInstance(getApplicationContext(), this, this);
    }

    private void loadLocalData() {
        pack = new Gson().fromJson(getIntent().getStringExtra("pack"), SubscriptionPack.class);
    }

    private void setListeners() {
        checklistListener = this::showCheckout;
        binding.continueBtn.setOnClickListener(view->checkOut());
    }

    private void loadData() {
        adapter = new ChecklistRVAdapter(Constants.getIndianStatesArrayList(), binding.searchEt, checklistListener, pack.getNoOfStates(),
                binding.recyclerView, this);
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

    private void checkOut(){
        ArrayList<State> selectedStateOptions = adapter.getSelectedStates();
        if (selectedStateOptions.size() != pack.getNoOfStates()){
            return;
        }
        binding.recyclerView.setVisibility(View.GONE);
        binding.searchEt.setVisibility(View.GONE);
        binding.titleTxt.setText("Initiating Payment");
        binding.continueBtn.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);
        helper.generateOrder(selectedStateOptions, pack);
        //Todo: Launch Payment Helper from here (FETCH ORDER ID, EXECUTE PAYMENT, SEND CONFIRMATION TO SERVER)
        //Todo: Server: Save SubOptions, generate order id, verify payment and activate subscription)
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