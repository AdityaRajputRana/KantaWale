package com.guru.kantewala;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.guru.kantewala.Adapters.ChecklistRVAdapter;
import com.guru.kantewala.Models.State;
import com.guru.kantewala.Models.SubscriptionPack;
import com.guru.kantewala.Tools.Constants;
import com.guru.kantewala.databinding.ActivitySubscriptionsOptionsBinding;

import java.util.ArrayList;

public class SubscriptionsOptionsActivity extends AppCompatActivity  {

    ActivitySubscriptionsOptionsBinding binding;
    ChecklistRVAdapter adapter;
    ChecklistRVAdapter.ChecklistListener checklistListener;
    SubscriptionPack pack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubscriptionsOptionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadLocalData();
        setListeners();
        loadData();
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
        binding.recyclerView.setVisibility(View.GONE);
        binding.searchEt.setVisibility(View.GONE);
        binding.titleTxt.setText("Initiating Payment");
        binding.continueBtn.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);
        ArrayList<State> selectedStateOptions = adapter.getSelectedStates();
        //Todo: Launch Payment Helper from here (FETCH ORDER ID, EXECUTE PAYMENT, SEND CONFIRMATION TO SERVER)
        //Todo: Server: Save SubOptions, generate order id, verify payment and activate subscription)
    }
}