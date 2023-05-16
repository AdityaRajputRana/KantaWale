package com.guru.kantewala;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= 31){
            SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        }

        super.onCreate(savedInstanceState);
        startMainActivity();
    }

    private void startMainActivity(){
        start(new Intent(this, MainActivity.class));
    }

    private void start(Intent intent){
        startActivity(intent);
        if (Build.VERSION.SDK_INT < 31){
            setTheme(R.style.Theme_KanteWala);
        }
        this.finish();
    }
}