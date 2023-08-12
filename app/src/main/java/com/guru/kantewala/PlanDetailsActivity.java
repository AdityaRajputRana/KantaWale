package com.guru.kantewala;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.guru.kantewala.Adapters.PlanDetailsAdapter;
import com.guru.kantewala.Tools.Methods;
import com.guru.kantewala.databinding.ActivityPlanDetailsBinding;
import com.guru.kantewala.rest.api.APIMethods;
import com.guru.kantewala.rest.api.interfaces.APIResponseListener;
import com.guru.kantewala.rest.response.PackHistoryRP;

public class PlanDetailsActivity extends AppCompatActivity {

    ActivityPlanDetailsBinding binding;
    PackHistoryRP historyRP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlanDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListeners();
        fetchData();
    }

    private void fetchData() {
        if (historyRP != null){
            loadData();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.GONE);

        APIMethods.getUserPurchaseHistory(this, new APIResponseListener<PackHistoryRP>() {
            @Override
            public void success(PackHistoryRP response) {
                historyRP = response;
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

    private Activity getActivity() {
        return this;
    }

    private void loadData() {
        binding.progressBar.setVisibility(View.GONE);
        binding.infoTxt.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.GONE);

        if (historyRP.getHistory() == null
        || historyRP.getHistory().size() == 0){
            binding.infoTxt.setText("No order history found!");
            binding.infoTxt.setVisibility(View.VISIBLE);
            return;
        }

        binding.recyclerView.setAdapter(new PlanDetailsAdapter(historyRP, this));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setVisibility(View.VISIBLE);
    }

    private void setListeners() {
        binding.backBtn.setOnClickListener(view->onBackPressed());
    }
}