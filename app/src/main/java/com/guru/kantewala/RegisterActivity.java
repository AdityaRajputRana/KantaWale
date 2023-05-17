package com.guru.kantewala;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.guru.kantewala.Tools.Constants;
import com.guru.kantewala.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity{

    ActivityRegisterBinding binding;
    private int selectedStateIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initialiseUI();
        initialise();
    }

    private void initialiseUI() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                R.layout.item_spinner, Constants.getIndianStates());
        spinnerAdapter.setDropDownViewResource(R.layout.item_dropdown);
        binding.stateSpinner.setAdapter(spinnerAdapter);
        binding.stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStateIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initialise() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null){
            //Todo: Fresh Registration
        } else if (user.getDisplayName() == null
        || user.getDisplayName().isEmpty()){
            //Todo: Pending Sign Up
        } else {
            //Todo: Editing the profile
        }
    }
}