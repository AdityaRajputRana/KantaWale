package com.guru.kantewala;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.guru.kantewala.Tools.ProfileUtils;
import com.guru.kantewala.rest.response.UnlockedStatesRP;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= 31){
            SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        }

        super.onCreate(savedInstanceState);
        signUpChecks();
    }

    private void signUpChecks(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Intent i = new Intent(this, MainActivity.class);
        if (auth.getCurrentUser() == null){
            i = new Intent(this, LoginActivity.class);
        } else if (ProfileUtils.isProfileEditRequired(this)) {
            i = new Intent(this, RegisterActivity.class);
            i.putExtra("signUpPending", true);
        } else {
            i = new Intent(this, MainActivity.class);
        }
        start(i);
    }

    private void start(Intent intent){
        startActivity(intent);
        if (Build.VERSION.SDK_INT < 31){
            setTheme(R.style.Theme_KanteWala);
        }
        this.finish();
    }
}