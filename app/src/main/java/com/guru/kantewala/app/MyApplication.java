package com.guru.kantewala.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MyApplication extends Application {
    private static RequestQueue mainRequestQueue;
    private Context context;
    private long shortTime = Long.parseLong("1685514642000");
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
//        setupActivityListener();
        if (System.currentTimeMillis() < shortTime)
            mainRequestQueue = Volley.newRequestQueue(getApplicationContext());
    }



    public static RequestQueue getMainRequestQueue() {
        return mainRequestQueue;
    }

}
