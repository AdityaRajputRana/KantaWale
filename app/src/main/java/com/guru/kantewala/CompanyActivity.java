package com.guru.kantewala;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.guru.kantewala.databinding.ActivityCompanyBinding;

public class CompanyActivity extends AppCompatActivity {

    ActivityCompanyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCompanyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListeners();
    }

    private void setListeners() {
        binding.backBtn.setOnClickListener(view->onBackPressed());
    }
}