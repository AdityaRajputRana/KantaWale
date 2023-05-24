package com.guru.kantewala.rest.api;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.guru.kantewala.rest.api.interfaces.APIResponseListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class API {

    public static void getData(APIResponseListener listener, Object rawData, String endpoint, Class klass){
        try {
            String data = HashUtils.getHashedData(rawData);
            JSONObject request = new JSONObject(data);
            String url = VolleyClient.getBaseUrl() + endpoint + VolleyClient.suffix;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, request, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Boolean successful = response.getBoolean("success");
                                if (successful) {
                                    String data = response.getJSONObject("data").toString();
//                                    String decodedData = HashUtils.fromBase64(data);
                                    listener.convertData(new Gson().fromJson(data, klass));
                                } else {
                                    listener.fail("2", request.getString("message"), "", true, true);
                                }
                            } catch (Exception e) {
                                listener.fail("1", "The received response is not good", "", true, true);
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error.networkResponse != null) {
                                String message = "";
                                if (error.networkResponse.data != null) {
                                    try {
                                        NetworkResponse networkResponse = error.networkResponse;
                                        String errorStr = new String(networkResponse.data);
                                        JSONObject jsonObject = new JSONObject(errorStr);
                                        message = message + " " + jsonObject.getString("message");
                                    } catch (Exception e){
                                        e.printStackTrace();
                                        message = message+" Json Conversion error.";
                                    }
                                }
                                if (error.getMessage() != null && !error.getMessage().isEmpty()) {
                                    message = message + " " + error.getMessage();
                                }
                                listener.fail(String.valueOf(error.networkResponse.statusCode), message, "", true, true);
                            }
                        }
                    }){

            };

            VolleyClient.getRequestQueue().add(jsonObjectRequest);

        } catch (Exception e){
            e.printStackTrace();
        }



    }


    public static void postData(APIResponseListener listener, Object rawData, String endpoint, Class klass){
        try {
            String data = HashUtils.getHashedData(rawData);
            JSONObject request;
            Log.i("eta", data);
            if (data.equals("\"{}\"")){
                request = new JSONObject();
            } else {
                 request = new JSONObject(data);
            }

            String url = VolleyClient.getBaseUrl() + endpoint + VolleyClient.suffix;

            Log.i("eta data", data);
            Log.i("eta url", url);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, url, request, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.i("Lesson Response", response.toString());
                                Boolean successful = response.getBoolean("success");
                                if (successful) {
                                    if (response.getString("data") != null
                                    && !response.getString("data").isEmpty() && !response.getString("data").trim().isEmpty()) {
                                        String data = "";
                                        if (klass == ArrayList.class){
                                             data = response.getJSONArray("data").toString();
                                        } else if (klass ==  String.class){
                                            data = response.getString("data");
                                        } else {
                                             data = response.getJSONObject("data").toString();
                                        }
//                                    String decodedData = HashUtils.fromBase64(data);
                                        if (klass == String.class)
                                            listener.success(data);
                                        else
                                            listener.convertData(new Gson().fromJson(data, klass));
                                    } else {
                                        listener.convertData(null);
                                    }
                                } else {
                                    listener.fail("2", response.getString("message"), "", true, true);
                                }
                            } catch (Exception e) {
                                Log.i("Lesson Response", response.toString());
                                listener.fail("1", "Response Conversion Error: " + e.getMessage().toString(), "", true, true);
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error != null) {
                                if (error.networkResponse != null) {
                                    String message = "";
                                    if (error.networkResponse.data != null) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(error.networkResponse.data.toString());
                                            message = message+" " +jsonObject.getString("message");
                                        } catch (Exception e){
                                            e.printStackTrace();
                                            message = message+" Json Conversion error.";
                                        }
                                    }
                                    message = message +" " + error.getMessage();
                                    listener.fail(String.valueOf(error.networkResponse.statusCode), message, "", true, true);
                                }
                            }
                        }
                    }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json");
                    return params;
                }

            };

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    2500,
                    4,
                    1.5f));
            VolleyClient.getRequestQueue().add(jsonObjectRequest);

        } catch (Exception e){
            e.printStackTrace();
        }



    }
}
