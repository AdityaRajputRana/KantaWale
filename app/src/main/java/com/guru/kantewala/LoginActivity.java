package com.guru.kantewala;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.guru.kantewala.Helpers.PhoneAuthHelper;
import com.guru.kantewala.Tools.Constants;
import com.guru.kantewala.Tools.ProfileUtils;
import com.guru.kantewala.Tools.Utils;
import com.guru.kantewala.databinding.ActivityLoginBinding;
import com.guru.kantewala.databinding.DialogLoadingBinding;
import com.guru.kantewala.databinding.DialogOtpBinding;
import com.guru.kantewala.rest.api.APIMethods;
import com.guru.kantewala.rest.api.interfaces.APIResponseListener;
import com.guru.kantewala.rest.response.UserRP;

public class LoginActivity extends AppCompatActivity implements PhoneAuthHelper.PhoneAuthListener {

    private static final int MY_REQUEST_CODE = 100;

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
        checkAppUpdate();
    }

    private void checkAppUpdate() {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);

// Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Request the update.

                try {
                    appUpdateManager.startUpdateFlowForResult(
                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                            appUpdateInfo,
                            // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                            AppUpdateType.IMMEDIATE,
                            // The current activity making the update request.
                            this,
                            // Include a request code to later monitor this update request.
                            MY_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {

            }
        }
    }




    private void setListeners() {
        binding.termsBtn.setOnClickListener(view-> Utils.openBrowser(Constants.termsUrl, this));

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

        if (!binding.termsCheck.isChecked()){
            binding.termsLayout.animate()
                    .translationX(100)
                    .setDuration(100)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            binding.termsLayout.animate()
                                    .translationX(-150)
                                    .setDuration(100)
                                    .setInterpolator(new AccelerateDecelerateInterpolator())
                                    .withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            binding.termsLayout.animate()
                                                    .translationX(0)
                                                    .setDuration(75)
                                                    .setInterpolator(new AccelerateDecelerateInterpolator())
                                                    .start();
                                        }
                                    })
                                    .start();
                        }
                    })
                    .start();


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
                break;
            case 10:
                signIn();
                break;
            case 11:
                confirmNewUser();
                break;
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
            confirmNewUser();
        } else {
            String firstName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName().split(" ")[0];
            Toast.makeText(this, "Welcome back " + firstName + "!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void confirmNewUser() {

        APIMethods.getUserProfile(new APIResponseListener<UserRP>() {
            @Override
            public void success(UserRP response) {
                dismissProgressDialog();
                if (response.isRegisteredUser()){
                    userDetailsAlreadyExistFlow(response);
                } else {
                    startRegistrationFlow();
                }
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                showError(message+ " - While checking for user records");
            }
        });
    }

    private void userDetailsAlreadyExistFlow(UserRP userRP) {
        DialogLoadingBinding mBinding = DialogLoadingBinding.inflate(getLayoutInflater());
        mBinding.progressBar.setVisibility(View.GONE);
        mBinding.titleTxt.setText("Business Details Found");
        mBinding.bodyTxt.setText("Business details associated with this phone number are found. We will link the details to your account.\n" +
                "If you want to update the details you may do so by editing your profile");
        new AlertDialog.Builder(this)
                .setView(mBinding.getRoot())
                .setPositiveButton("Continue", (dialog1, which) -> saveDetailsToFirebase(userRP))
                .setCancelable(false)
                .show();
    }

    private void saveDetailsToFirebase(UserRP userRP) {
        startProgress("Linking your business to your profile");
        String uploadedImageUrl = userRP.getPhotoUrl();
        String name = userRP.getName();
        if (name == null || name.isEmpty()){
            name = "Default User";
        }

        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder()
                .setDisplayName(name);
        if (uploadedImageUrl != null && !uploadedImageUrl.isEmpty()){
            builder.setPhotoUri(Uri.parse(uploadedImageUrl));
        }
        UserProfileChangeRequest request = builder.build();
        FirebaseAuth.getInstance().getCurrentUser()
                .updateProfile(request)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            startMainActivity();
                        } else {
                            showError("Error updating your name (FA-401)");
                        }
                    }
                });
    }

    private void startMainActivity() {
        Toast.makeText(this, "Welcome to our platform", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void startRegistrationFlow(){
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