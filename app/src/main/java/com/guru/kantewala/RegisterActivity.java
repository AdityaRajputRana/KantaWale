package com.guru.kantewala;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.guru.kantewala.Tools.Constants;
import com.guru.kantewala.Tools.Utils;
import com.guru.kantewala.databinding.ActivityRegisterBinding;
import com.guru.kantewala.databinding.DialogLoadingBinding;
import com.guru.kantewala.rest.api.APIMethods;
import com.guru.kantewala.rest.api.interfaces.APIResponseListener;
import com.guru.kantewala.rest.requests.RegisterProfileReq;
import com.guru.kantewala.rest.response.MessageRP;

public class RegisterActivity extends AppCompatActivity{

    ActivityRegisterBinding binding;
    private int selectedStateIndex = 0;

    //Todo: Implement country code picker
    int requiredPhoneNumberDigits = 10;
    String countryCode = "+91";

    AlertDialog dialog;
    DialogLoadingBinding dialogBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initialiseUI();
        initialise();
        setUpListeners();
    }

    private void setUpListeners() {
        binding.loginLayout.setOnClickListener(view->{
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            RegisterActivity.this.finish();
        });

        binding.continueBtn.setOnClickListener(view->{
            Utils.hideSoftKeyboard(RegisterActivity.this);
            if (isInputValid()){
                saveProfileInformation();
            }
        });
    }

    private boolean isInputValid(){
        boolean isValid = true;

        if (binding.nameEt.getText().toString().isEmpty()){
            binding.nameEt.setError("Required");
            isValid = false;
        } else {
            binding.nameEt.setError(null);
        }

        if (binding.emailEt.getText().toString().isEmpty()){
            binding.emailEt.setError("Required");
            isValid = false;
        }else if(!binding.emailEt.getText().toString().contains("@")){
            binding.emailEt.setError("Enter a valid email");
            isValid = false;
        }else {
            binding.emailEt.setError(null);
        }


        if (binding.phoneNumberEt.getText().toString().length() != requiredPhoneNumberDigits){
            String error = "Please enter a valid " + requiredPhoneNumberDigits + " digit mobile number";
            binding.phoneNumberEt.setError(error);
            isValid = false;
        } else {
            binding.phoneNumberEt.setError(null);
        }


        if (binding.companyEt.getText().toString().isEmpty()){
            binding.companyEt.setError("Required");
            isValid = false;
        } else {
            binding.companyEt.setError(null);
        }

        if (binding.gstET.getText().toString().isEmpty()){
            binding.gstET.setError("Required");
            isValid = false;
        } else {
            binding.gstET.setError(null);
        }

        if (binding.cityEt.getText().toString().isEmpty()){
            binding.cityEt.setError("Required");
            isValid = false;
        } else {
            binding.cityEt.setError(null);
        }

        if (selectedStateIndex == 0){
            binding.stateSpinner.setBackgroundColor(Color.YELLOW);
            isValid = false;
        } else {
            binding.stateSpinner.setBackground(getResources().getDrawable(R.drawable.bg_et_main));
        }


        return isValid;
    }

    private void saveProfileInformation() {
        startProgress("Please wait while we update your profile");
        //Todo: Save to server here
        RegisterProfileReq req = new RegisterProfileReq(
                binding.nameEt.getText().toString(),
                countryCode + binding.phoneNumberEt.getText().toString(),
                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                binding.emailEt.getText().toString(),
                binding.companyEt.getText().toString(),
                binding.gstET.getText().toString(),
                binding.cityEt.getText().toString(),
                Constants.getIndianStates().get(selectedStateIndex),
                selectedStateIndex
        );
        APIMethods.registerProfile(req, new APIResponseListener<MessageRP>() {
            @Override
            public void success(MessageRP response) {
                saveToFirebase(response.getMessage());
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                message = message + " (EC"+code+")";
                showError(message);
            }
        });
    }

    private void saveToFirebase(String message) {
        if (message == null || message.isEmpty())
            message = "Almost there! setting up your new account";
        startProgress(message);

        //Todo: Save changes here

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(binding.nameEt.getText().toString())
                //todo: update photo here also
                .build();
        FirebaseAuth.getInstance().getCurrentUser()
                .updateProfile(request)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            showSuccess("Account is set up. Click anywhere outside dialog to continue!",
                                    new OnDismissListener() {
                                        @Override
                                        public void onCancel() {
                                            startMainActivity();
                                        }
                                    });
                        } else {
                            showError("Error updating your name (FA-401)");
                        }
                    }
                });
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
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
            loadPendingSignUpUI();
        } else {
            //Todo: Editing the profile
            loadEditingProfileUI();
        }
    }

    private void loadPendingSignUpUI() {
        binding.phoneNumberEt.setEnabled(false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.getDisplayName() != null && !user.getDisplayName().isEmpty())
            binding.nameEt.setText(user.getDisplayName());

        if (user.getEmail() != null && !user.getEmail().isEmpty())
            binding.emailEt.setText(user.getEmail());

        //Todo: handle country codes
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty())
            binding.phoneNumberEt.setText(user.getPhoneNumber().replace("+91", ""));
    }

    private void loadEditingProfileUI() {
        binding.phoneNumberEt.setEnabled(false);
        binding.loginLayout.setVisibility(View.GONE);
    }


    private void startProgress(String message) {
        if (dialogBinding == null){
            dialogBinding = DialogLoadingBinding.inflate(getLayoutInflater(), null, false);
        }

        dialogBinding.imageView.setVisibility(View.GONE);
        dialogBinding.progressBar.setVisibility(View.VISIBLE);
        dialogBinding.titleTxt.setText("Please wait");
        dialogBinding.bodyTxt.setText(message);

        if (dialog == null){
            dialog = new android.app.AlertDialog.Builder(this)
                    .setView(dialogBinding.getRoot())
                    .setOnDismissListener(d->{
                        dialogBinding = null;
                        dialog = null;
                    })
                    .show();

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCancelable(false);
    }
    private void showError(String message){
        if (dialogBinding == null){
            dialogBinding = DialogLoadingBinding.inflate(getLayoutInflater(), null, false);
        }

        dialogBinding.imageView.setVisibility(View.VISIBLE);
        dialogBinding.imageView.setImageDrawable(getDrawable(R.drawable.ic_error_bg));
        dialogBinding.progressBar.setVisibility(View.GONE);
        dialogBinding.titleTxt.setText("Some Error Occurred");
        dialogBinding.bodyTxt.setText(message);

        if (dialog == null) {
            dialog = new AlertDialog.Builder(this)
                    .setView(dialogBinding.getRoot())
                    .setOnDismissListener(d->{
                        dialogBinding = null;
                        dialog = null;
                    })
                    .show();

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.setCancelable(true);
    }
    interface OnDismissListener{
        void onCancel();
    }
    private void showSuccess(String message, OnDismissListener listener){
        if (dialogBinding == null){
            dialogBinding = DialogLoadingBinding.inflate(getLayoutInflater(), null, false);
        }

        dialogBinding.imageView.setVisibility(View.VISIBLE);
        dialogBinding.imageView.setImageDrawable(getDrawable(R.drawable.ic_done_bg));
        dialogBinding.progressBar.setVisibility(View.GONE);
        dialogBinding.titleTxt.setText("Completed");
        dialogBinding.bodyTxt.setText(message);

        if (dialog == null) {
            dialog = new AlertDialog.Builder(this)
                    .setView(dialogBinding.getRoot())
                    .setOnDismissListener(d->{
                        dialogBinding = null;
                        dialog = null;
                    })
                    .show();

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.setCancelable(true);
        dialog.setOnCancelListener(dialog1 -> {
            dialog.setOnCancelListener(null);
            if (listener != null)
                listener.onCancel();
        });
    }
}