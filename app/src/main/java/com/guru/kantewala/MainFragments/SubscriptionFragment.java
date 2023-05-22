package com.guru.kantewala.MainFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.guru.kantewala.R;
import com.guru.kantewala.databinding.FragmentSubscriptionBinding;

public class SubscriptionFragment extends Fragment {

    FragmentSubscriptionBinding binding;
    public SubscriptionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSubscriptionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}