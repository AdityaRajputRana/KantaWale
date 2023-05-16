package com.guru.kantewala;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.guru.kantewala.Helpers.PhoneAuthHelper;
import com.guru.kantewala.Tools.ProfileUtils;
import com.guru.kantewala.databinding.ActivityLoginBinding;
import com.guru.kantewala.databinding.DialogLoadingBinding;
import com.guru.kantewala.databinding.DialogOtpBinding;

public class LoginActivity extends AppCompatActivity implements PhoneAuthHelper.PhoneAuthListener {

    //Todo: Make the activity render after pause using a full state class - less priority

    //Todo: Implement country code picker
    int requiredPhoneNumberDigits = 10;
    String countryCode = "+91";
    
    ActivityLoginBinding binding;
    PhoneAuthHelper helper;
    AlertDialog dialog;
    DialogLoadingBinding dialogBinding;
    



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        helper = new PhoneAuthHelper(this, this);
        setListeners();
    }



    private void setListeners() {
        binding.continueBtn.setOnClickListener(view->{
            if (validPhoneInput()){
                String phoneNumber = countryCode + binding.phoneNumberEt.getText().toString();
                startProgress("We are sending OTP to your mobile number");
                helper.sendOTP(phoneNumber);
            }

        });

        binding.registerLayout.setOnClickListener(view->{
            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
            LoginActivity.this.startActivity(i);
        });
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

    private boolean validPhoneInput() {
        boolean isValidInput = true;
        String error = "";
        
        if (binding.phoneNumberEt.getText().toString().length() != requiredPhoneNumberDigits){
            error = "Please enter a valid " + requiredPhoneNumberDigits + " digit mobile number";
            isValidInput = false;
        }
        
        if (error.isEmpty())
            error = null;
        binding.phoneNumberEt.setError(error);
        return isValidInput;
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


    @Override
    public void authMessage(boolean success, String message, int code) {
        switch (code){
            case 5:
                showOTPLayout();
                break;
            case 2:
                invalidPhoneNumber();
                break;
            case 6:
                invalidOTP();
                break;
            case 1:
                handlePhoneVerification();
            case 10:
                signIn();
            case 11:
                registerNewUser();
            default:
                showError(message);
                break;
        }
    }

    private void signIn() {
        dismissProgressDialog();
        if (FirebaseAuth.getInstance().getCurrentUser() == null
        || FirebaseAuth.getInstance().getCurrentUser().getDisplayName() == null
        || FirebaseAuth.getInstance().getCurrentUser().getDisplayName().isEmpty()){
            registerNewUser();
        } else {
            String firstName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName().split(" ")[0];
            Toast.makeText(this, "Welcome back " + firstName + "!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void registerNewUser() {
        dismissProgressDialog();
        ProfileUtils.saveProfileEditRequired(this, true);
        Intent i = new Intent(this, RegisterActivity.class);
        i.putExtra("signUpPending", true);
        i.putExtra("sentFromLogin", true);
        startActivity(i);
        finish();
    }

    private void handlePhoneVerification() {
        if (otpDialog != null) {
            otpDialog.dismiss();
            otpDialog = null;
            otpBinding = null;
        }

        startProgress("Phone number is verified!\nLogging you in.");
    }

    //Todo: Handle Back Presses - save stage in helper
    //Todo: Handle changing phone number request

    private void invalidOTP() {
        if (otpBinding != null){
            otpBinding.continueBtn.setEnabled(true);
            otpBinding.progressBar.setVisibility(View.GONE);
            otpBinding.imageView.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Wrong otp!", Toast.LENGTH_SHORT).show();
            otpBinding.pinview.setLineColor(Color.RED);
        }
    }

    private void dismissProgressDialog(){
        if (dialog != null){
            dialog.dismiss();
            dialog = null;
            dialogBinding = null;
        }
    }

    private void invalidPhoneNumber() {
        dismissProgressDialog();
        binding.phoneNumberEt.setError("This phone number is not valid");
    }


    DialogOtpBinding otpBinding;
    AlertDialog otpDialog;

    private void showOTPLayout() {
        dismissProgressDialog();
        if (otpBinding == null){
            otpBinding = DialogOtpBinding.inflate(getLayoutInflater());
            otpBinding.pinview.setAnimationEnable(true);

            otpBinding.continueBtn.setOnClickListener(view->{
                if (validateOTPInput()) {
                    otpBinding.progressBar.setVisibility(View.VISIBLE);
                    otpBinding.continueBtn.setEnabled(false);
                    otpBinding.imageView.setVisibility(View.GONE);
                    helper.verifyOTP(otpBinding.pinview.getText().toString());
                }
            });

            //Todo: Auto Submit on OTP Filled
            //Todo: Add timer before enabling this option
            otpBinding.resendLayout.setOnClickListener(view-> {
                otpBinding.progressBar.setVisibility(View.VISIBLE);
                otpBinding.bodyTxt.setText("Resending OTP");
                otpBinding.imageView.setVisibility(View.GONE);
                helper.resendOTP();
            });
        }
        otpBinding.progressBar.setVisibility(View.GONE);
        otpBinding.continueBtn.setEnabled(true);
        otpBinding.imageView.setVisibility(View.VISIBLE);
        otpBinding.bodyTxt.setText("Enter OTP code sent to " + countryCode + " " + binding.phoneNumberEt.getText().toString());

        if (otpDialog == null){
            otpDialog = new AlertDialog.Builder(this)
                    .setView(otpBinding.getRoot())
                    .setOnDismissListener(d->{
                        otpDialog = null;
                        otpBinding = null;
                    })
                    .setCancelable(false)
                    .show();
            otpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private boolean validateOTPInput() {
        if (otpBinding.pinview.getText().toString().length() == 0){
            otpBinding.pinview.setLineColor(Color.RED);
            return false;
        }
        otpBinding.pinview.setLineColor(getResources().getColor(R.color.color_cta));
        return true;
    }
}