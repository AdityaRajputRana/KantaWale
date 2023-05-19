package com.guru.kantewala.rest.api;

import com.android.volley.RequestQueue;
import com.guru.kantewala.app.MyApplication;

public class VolleyClient {

//    public static String BASE_URL = "https://paradox-backend.onrender.com/";
//    public static String BASE_URL = "http://206.189.132.227/";
    public static String BASE_URL = "https://0fb7-2401-4900-c87-376b-3b1-652d-9859-c1c1.ngrok-free.app/";
    public static String HOST= "quasar-edtech.vercel.app/edtech";

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static RequestQueue getRequestQueue() {
        return requestQueue;
    }

    private static RequestQueue requestQueue = MyApplication.getMainRequestQueue();

}
