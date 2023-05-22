package com.guru.kantewala.MainFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.guru.kantewala.R;
import com.guru.kantewala.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {


    FragmentProfileBinding binding;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("ProfileFrag", "onCreate");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Log.i("ProfileFrag", "onCreateView");
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}