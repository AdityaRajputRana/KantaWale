package com.guru.kantewala.Helpers;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.guru.kantewala.Tools.Methods;
import com.guru.kantewala.rest.api.APIMethods;
import com.guru.kantewala.rest.api.interfaces.APIResponseListener;
import com.guru.kantewala.rest.response.MessageRP;

import java.util.concurrent.TimeUnit;

public class PhoneAuthHelper {

    public PhoneAuthHelper(PhoneAuthListener listener, Activity context) {
        setUpPhoneAuth();
        this.listener = listener;
        this.context = context;
    }

    public PhoneAuthListener listener;
    public Activity context;
    //Todo: Define Enum for Error Codes
    public interface PhoneAuthListener{
        void authMessage(boolean success, String message, int code);
    }

    public void resendOTP(){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(mPhoneNumber)
                        .setForceResendingToken(mResendToken)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(context)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void authMessage(boolean success, String message, int code){
        /*
            Code Meanings for UI Changes or Logs
            0 - Unknown Error: Check String message
            1 - Phone Number is verified -
            2 - "Invalid Phone number",
            3 - SMS Quota
            4 - reCaptcha Failed
            5 - OTP sent
            6 - Wrong OTP -
            7 - Sign in failure: Check String message
         */

        if (listener != null && listener instanceof PhoneAuthListener){
            listener.authMessage(success, message, code);
        }
        Log.i("PhoneAuthLogs-" + code, message);
    }

    FirebaseAuth mAuth;
    String mVerificationId;
    String mPhoneNumber;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private void setUpPhoneAuth(){
        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                authMessage(true, "Phone Number verified", 1);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    authMessage(false, "Invalid Phone number", 2);
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    authMessage(false, "SMS Quota Exceeded", 3);
                } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                    authMessage(false, "reCaptcha Failed", 4);
                } else {
                    authMessage(false, e.getMessage(), 0);
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                authMessage(true, "OTP sent", 5);
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
    }
    public void sendOTP(String phoneNumber){
        mPhoneNumber = phoneNumber;
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(context)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void verifyOTP(String otp){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        saveDeviceIdOnServer(task);
                    }
                });
    }

    private void saveDeviceIdOnServer(Task<AuthResult> task) {
        APIMethods.saveDeviceId(context, new APIResponseListener<MessageRP>() {
            @Override
            public void success(MessageRP response) {
                verifyNewUser(task);
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                Methods.showFailedAlert(context, code, message, redirectLink, false, true);
                verifyNewUser(task);
            }
        });
    }

    private void verifyNewUser(Task<AuthResult> task) {
        if (task.isSuccessful()) {
            if (task.getResult().getAdditionalUserInfo().isNewUser())
                authMessage(true, "New user joined!", 11);
            else
                authMessage(true, "Sign In Successful!", 10);
        } else {
            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                authMessage(false, "The otp entered was incorrect", 6);
            } else {
                authMessage(false, task.getException().getMessage(), 7);
            }
        }
    }
}