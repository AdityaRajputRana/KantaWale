package com.guru.kantewala.MainFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.guru.kantewala.Adapters.SubscriptionPackageAdapter;
import com.guru.kantewala.Helpers.SubscriptionUIHelper;
import com.guru.kantewala.Models.SubscriptionPackage;
import com.guru.kantewala.R;
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
        binding.infoTxt.setVisibility(View.GONE);
        binding.continueBtn.setVisibility(View.GONE);

        APIMethods.getSubscriptionPackages(new APIResponseListener<SubscriptionPackagesRP>() {
            @Override
            public void success(SubscriptionPackagesRP response) {
                SubscriptionUIHelper.subscriptionPackagesRP = response;
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
        if (subscriptionPackagesRP.isPremiumUser()){
            binding.infoTxt.setVisibility(View.VISIBLE);
            binding.infoTxt.setText("You are already a premium User");
            return;
        }

        if (adapter == null){
            adapter = new SubscriptionPackageAdapter(subscriptionPackagesRP);
        }

        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setVisibility(View.VISIBLE);

        binding. continueBtn.setVisibility(View.VISIBLE);
    }

    private void setListeners() {
        binding.continueBtn.setOnClickListener(view->{
            subscribe(adapter.getSelectedPack());
        });
    }

    private void subscribe(SubscriptionPackage selectedPack) {
        //Todo: Launch Subscribe Activity
    }
}