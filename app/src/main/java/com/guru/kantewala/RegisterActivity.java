package com.guru.kantewala;

import androidx.activity.result.ActivityResultLauncher;

import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import 	androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.guru.kantewala.Helpers.PhoneAuthHelper;
import com.guru.kantewala.Tools.Constants;
import com.guru.kantewala.Tools.Transformations.RoundedCornerTransformation;
import com.guru.kantewala.Tools.Utils;
import com.guru.kantewala.databinding.ActivityRegisterBinding;
import com.guru.kantewala.databinding.DialogLoadingBinding;
import com.guru.kantewala.databinding.DialogOtpBinding;
import com.guru.kantewala.rest.api.APIMethods;
import com.guru.kantewala.rest.api.interfaces.APIResponseListener;
import com.guru.kantewala.rest.api.interfaces.FileTransferResponseListener;
import com.guru.kantewala.rest.requests.RegisterProfileReq;
import com.guru.kantewala.rest.response.MessageRP;
import com.guru.kantewala.rest.response.UserRP;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class RegisterActivity extends AppCompatActivity implements PhoneAuthHelper.PhoneAuthListener {

    ActivityRegisterBinding binding;
    private int selectedStateIndex = 0;

    //Todo: Implement country code picker
    int requiredPhoneNumberDigits = 10;
    String countryCode = "+91";

    AlertDialog dialog;
    DialogLoadingBinding dialogBinding;

    private UserRP userRP;

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new PickVisualMedia(), uri -> {
                if (uri != null) {
                    Picasso.get()
                            .load(uri)
                            .transform(new RoundedCornerTransformation(50, 50))
                            .into(binding.profileImageView);
                    toUploadImageUri = uri;
                }
            });

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
                if (FirebaseAuth.getInstance().getCurrentUser() == null){
                    startVerification();
                } else {
                    saveProfileInformation();
                }
            }
        });

        binding.profileImageView.setOnClickListener(view->pickImage());
        binding.termsBtn.setOnClickListener(view->Utils.openBrowser(Constants.termsUrl, this));
    }

    private void pickImage() {
        ActivityResultContracts.PickVisualMedia.VisualMediaType mediaType =
                (ActivityResultContracts.PickVisualMedia.VisualMediaType) ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE;
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(mediaType)
                .build());
    }

    Uri toUploadImageUri = null;
    String uploadedImageUrl = "";
    private void uploadImage() {
        if (toUploadImageUri == null){
            saveProfileInformation();
            return;
        }

        startProgress("Uploading Image");
        APIMethods.uploadProfilePicture(toUploadImageUri, this, new FileTransferResponseListener<MessageRP>() {
            @Override
            public void success(MessageRP response) {
                uploadedImageUrl = response.getMessage();
                toUploadImageUri = null;
                saveProfileInformation();
            }

            @Override
            public void onProgress(int percent) {

            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                message = "While uploading profile picture: " + message + " (EC"+code+")";
                showError(message);
            }
        });

    }

    private void startVerification() {
        String phoneNumber = countryCode + binding.phoneNumberEt.getText().toString();
        startProgress("We are sending OTP to your mobile number");
        helper.sendOTP(phoneNumber);
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


            isValid = false;
        }


//        if (binding.companyEt.getText().toString().isEmpty()){
//            binding.companyEt.setError("Required");
//            isValid = false;
//        } else {
//            binding.companyEt.setError(null);
//        }
//
//        if (binding.gstET.getText().toString().isEmpty()){
//            binding.gstET.setError("Required");
//            isValid = false;
//        } else {
//            binding.gstET.setError(null);
//        }

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
        if (toUploadImageUri != null){
            uploadImage();
            return;
        }
        startProgress("Please wait while we update your profile");
        RegisterProfileReq req = new RegisterProfileReq(
                binding.nameEt.getText().toString(),
                (countryCode + binding.phoneNumberEt.getText().toString()),
                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                binding.emailEt.getText().toString(),
                binding.companyEt.getText().toString(),
                binding.gstET.getText().toString(),
                binding.cityEt.getText().toString(),
                Constants.getIndianStates().get(selectedStateIndex),
                selectedStateIndex, uploadedImageUrl
        );
        APIMethods.registerProfile(mode, req, new APIResponseListener<MessageRP>() {
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
            if (mode == Mode.EDIT)
                message = "Almost there!";
            else
                message = "Almost there! setting up your new account";
        startProgress(message);

        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder()
                .setDisplayName(binding.nameEt.getText().toString());
        if (uploadedImageUrl != null && !uploadedImageUrl.isEmpty()){
            builder.setPhotoUri(Uri.parse(uploadedImageUrl));
        }

        if (uploadedImageUrl != null && userRP != null
                && !uploadedImageUrl.equals(userRP.getPhotoUrl())) {
            Picasso.get()
                    .invalidate(uploadedImageUrl);
            Picasso.get()
                    .load(uploadedImageUrl)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(binding.profileImageView);
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
        if (mode == Mode.EDIT){
            Toast.makeText(this, "Saved changes!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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

    public enum Mode{NONE, FRESH, PENDING, EDIT};
    private Mode mode = Mode.NONE;
    private void initialise() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null){
            mode = Mode.FRESH;
            initialiseFreshUI();
        } else if (user.getDisplayName() == null
                || user.getDisplayName().isEmpty()){
            mode = Mode.PENDING;
            loadPendingSignUpUI();
        } else {
            mode = Mode.EDIT;
            loadEditingProfileUI();
        }
    }

    private PhoneAuthHelper helper;
    private void initialiseFreshUI() {
        binding.phoneNumberEt.setEnabled(true);
        binding.phoneNumberEt.setText("");

        helper = new PhoneAuthHelper(this, this);
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
        ViewGroup.LayoutParams params = binding.topImg.getLayoutParams();
        params.height = params.height/4;
        binding.topImg.setLayoutParams(params);
        binding.welcomeTxt.setVisibility(View.GONE);
        binding.headingTxt.setText("Edit Profile");
        binding.continueBtn.setText("Save Profile");
        //Todo: give option or instructions to change it

        startProgress("Loading your user profile details");
        APIMethods.getUserProfile(new APIResponseListener<UserRP>() {
            @Override
            public void success(UserRP response) {
                dismissProgressDialog();
                userRP = response;
                updateUIForEdit();
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                showError("(EC-"+code+") " + message);
            }
        });
    }

    private void updateUIForEdit() {
        uploadedImageUrl = userRP.getPhotoUrl();
        binding.nameEt.setText(userRP.getName());
        binding.emailEt.setText(userRP.getEmail());
        //Todo: handle country codes
        binding.phoneNumberEt.setText(userRP.getPhone().replace(countryCode, ""));
        binding.companyEt.setText(userRP.getCompany());
        binding.gstET.setText(userRP.getCompanyGST());
        binding.cityEt.setText(userRP.getCity());
        selectedStateIndex = userRP.getStateCode();
        binding.stateSpinner.setSelection(selectedStateIndex);
        Log.i("Photo", "Printing");
        if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null){
            Log.i("PhotoURL", String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()));
        }
        if (userRP.getPhotoUrl() != null && !userRP.getPhotoUrl().isEmpty()){
            Picasso.get()
                    .load(userRP.getPhotoUrl())
                    .transform(new RoundedCornerTransformation(50, 50))
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(binding.profileImageView);
        }
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
        if (otpDialog != null) {
            otpDialog.dismiss();
            otpDialog = null;
            otpBinding = null;
        }
        dismissProgressDialog();
        if (FirebaseAuth.getInstance().getCurrentUser() == null
                || FirebaseAuth.getInstance().getCurrentUser().getDisplayName() == null
                || FirebaseAuth.getInstance().getCurrentUser().getDisplayName().isEmpty()){
            confirmNewUser();
        } else {
            showSuccess("This phone number is already linked to another account. Click outside the dialog to login into previoius account!",
                    ()->startMainActivity());
        }
    }
    private void confirmNewUser() {
        if (otpDialog != null) {
            otpDialog.dismiss();
            otpDialog = null;
            otpBinding = null;
        }

        startProgress("Checking linked businesses");
        APIMethods.getUserProfile(new APIResponseListener<UserRP>() {
            @Override
            public void success(UserRP response) {
                dismissProgressDialog();
                if (response.isRegisteredUser()){
                    userDetailsAlreadyExistFlow(response);
                } else {
                    saveProfileInformation();
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
                    Utils.hideSoftKeyboard(RegisterActivity.this);
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
    private void invalidOTP() {
        if (otpBinding != null){
            otpBinding.continueBtn.setEnabled(true);
            otpBinding.progressBar.setVisibility(View.GONE);
            otpBinding.imageView.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Wrong otp!", Toast.LENGTH_SHORT).show();
            otpBinding.pinview.setLineColor(Color.RED);
        }
    }
    private void handlePhoneVerification() {
        if (otpDialog != null) {
            otpDialog.dismiss();
            otpDialog = null;
            otpBinding = null;
        }

        startProgress("Phone number is verified!\nCreating a new profile for you.");
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