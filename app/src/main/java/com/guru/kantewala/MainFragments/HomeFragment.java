package com.guru.kantewala.MainFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.guru.kantewala.Adapters.RecommendationRVAdapter;
import com.guru.kantewala.Tools.Methods;
import com.guru.kantewala.databinding.FragmentHomeBinding;
import com.guru.kantewala.rest.api.APIMethods;
import com.guru.kantewala.rest.api.interfaces.APIResponseListener;
import com.guru.kantewala.rest.response.DashboardRP;


public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    DashboardRP dashboardRP;



    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("HomeFrag", "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        loadDashboard();
        return binding.getRoot();
    }

    private void loadDashboard() {
        if (dashboardRP != null) {
            updateDashboardUI();
            return;
        }

        APIMethods.getDashboard(new APIResponseListener<DashboardRP>() {
            @Override
            public void success(DashboardRP response) {
                dashboardRP = response;
                updateDashboardUI();
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                binding.dashboardLayout.setVisibility(View.GONE);
                Methods.showFailedAlert(getActivity(), code, message, redirectLink, retry, cancellable);
            }
        });
    }

    private void updateDashboardUI() {
        binding.dashboardProgressBar.setVisibility(View.GONE);
        binding.dashBoardRecyclerView.setAdapter(
                new RecommendationRVAdapter(dashboardRP, getActivity())
        );
        binding.dashBoardRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.dashBoardRecyclerView.setVisibility(View.VISIBLE);
    }
}