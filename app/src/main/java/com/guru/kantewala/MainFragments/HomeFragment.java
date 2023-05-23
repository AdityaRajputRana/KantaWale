package com.guru.kantewala.MainFragments;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.guru.kantewala.Adapters.RecommendationRVAdapter;
import com.guru.kantewala.R;
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
        setUpToggle();
        loadDashboard();
        return binding.getRoot();
    }

    private void setUpToggle() {
        binding.recommendationToggle.setOnClickListener(view->{
            binding.recommendationToggle.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.color_cta)));
            binding.recommendationToggle.setTextColor(getActivity().getResources().getColor(R.color.color_fg));

            binding.categoryToggle.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.color_fg)));
            binding.categoryToggle.setTextColor(getActivity().getResources().getColor(R.color.color_cta));

            binding.dashboardLayout.setVisibility(View.VISIBLE);
            binding.categoryLayout.setVisibility(View.GONE);
        });

        binding.categoryToggle.setOnClickListener(view->{
            binding.categoryToggle.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.color_cta)));
            binding.categoryToggle.setTextColor(getActivity().getResources().getColor(R.color.color_fg));

            binding.recommendationToggle.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.color_fg)));
            binding.recommendationToggle.setTextColor(getActivity().getResources().getColor(R.color.color_cta));

            binding.categoryLayout.setVisibility(View.VISIBLE);
            binding.dashboardLayout.setVisibility(View.GONE);
        });
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