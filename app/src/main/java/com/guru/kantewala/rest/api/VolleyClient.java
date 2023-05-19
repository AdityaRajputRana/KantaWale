package com.guru.kantewala.rest.api;

import com.android.volley.RequestQueue;
import com.guru.kantewala.app.MyApplication;

public class VolleyClient {

//    public static String BASE_URL = "https://paradox-backend.onrender.com/";
    public static String testURL = "http://10.0.2.2:8888/KantewaleBackend/";
    public static String suffix = ".php";
    public static String BASE_URL = "https://0fb7-2401-4900-c87-376b-3b1-652d-9859-c1c1.ngrok-free.app/";
    public static String HOST= "quasar-edtech.vercel.app/edtech";

    public static String getBaseUrl() {
        if (true)
            return testURL;
        return BASE_URL;
    }

    public static RequestQueue getRequestQueue() {
        return requestQueue;
    }

    private static RequestQueue requestQueue = MyApplication.getMainRequestQueue();

}