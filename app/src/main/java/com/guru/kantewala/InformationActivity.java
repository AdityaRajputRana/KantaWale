package com.guru.kantewala;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class InformationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        findViewById(R.id.backBtn).setOnClickListener(view->onBackPressed());
        initialise();
    }

    private void initialise() {
        TextView headTxt = findViewById(R.id.headTxt);
        TextView bodyTxt = findViewById(R.id.bodyTxt);

        if (getIntent().getBooleanExtra("isHead", false)) {
            headTxt.setText(getIntent().getStringExtra("head"));
        }

        if (getIntent().getBooleanExtra("isBody", false))
            bodyTxt.setText(getIntent().getStringExtra("body"));
    }
}