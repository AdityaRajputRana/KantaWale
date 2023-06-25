package com.guru.kantewala.MainFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.guru.kantewala.Adapters.SubscriptionPackageAdapter;
import com.guru.kantewala.Helpers.SubscriptionInterface;
import com.guru.kantewala.Models.SubscriptionPack;
import com.guru.kantewala.SubscriptionsOptionsActivity;
import com.guru.kantewala.Tools.Methods;
import com.guru.kantewala.databinding.FragmentSubscriptionBinding;
import com.guru.kantewala.rest.api.APIMethods;
import com.guru.kantewala.rest.api.interfaces.APIResponseListener;
import com.guru.kantewala.rest.response.SubscriptionPackagesRP;

public class SubscriptionFragment extends Fragment {

    FragmentSubscriptionBinding binding;
    private SubscriptionPackagesRP subscriptionPackagesRP;
    private SubscriptionPackageAdapter adapter;

    public SubscriptionFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSubscriptionBinding.inflate(inflater, container, false);
        setListeners();
        fetchData();
        return binding.getRoot();
    }

    private void fetchData() {
        if (subscriptionPackagesRP != null){
            loadData();
            return;
        }
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.GONE);
        binding.myPackDetailsLayout.setVisibility(View.GONE);
        binding.continueBtn.setVisibility(View.GONE);

        APIMethods.getSubscriptionPackages(new APIResponseListener<SubscriptionPackagesRP>() {
            @Override
            public void success(SubscriptionPackagesRP response) {
                SubscriptionInterface.subscriptionPackagesRP = response;
                subscriptionPackagesRP = response;
                loadData();
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                binding.progressBar.setVisibility(View.GONE);
                Methods.showFailedAlert(getActivity(), code,
                        message, redirectLink, retry, cancellable);
            }
        });
    }

    private void loadData() {
        binding.progressBar.setVisibility(View.GONE);


        binding.myPackDetailsLayout.setVisibility(View.GONE);

        if (adapter == null){
            adapter = new SubscriptionPackageAdapter(subscriptionPackagesRP, getActivity());
        }

        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setVisibility(View.VISIBLE);

        binding.continueBtn.setVisibility(View.VISIBLE);
    }

    private void setListeners() {
        binding.continueBtn.setOnClickListener(view->{
            subscribe(adapter.getSelectedPack());
        });
    }

    private void subscribe(SubscriptionPack selectedPack) {
        String pack = new Gson().toJson(selectedPack);
        Intent intent = new Intent(getActivity(), SubscriptionsOptionsActivity.class);
        intent.putExtra("pack", pack);
        startActivity(intent);
    }
}