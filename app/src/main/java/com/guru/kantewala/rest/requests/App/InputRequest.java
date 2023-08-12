package com.guru.kantewala.rest.requests.App;

import android.content.Context;
import android.provider.Settings;

public class InputRequest {
    String salt;
    Object input;
    long timestamp;
    String hash;
    String deviceId;
    String enpoint;

    public InputRequest(String random_string, Object input, long timestamp, String hash, Context context, String endpoint) {
        this.salt = random_string;
        this.input = input;
        this.timestamp = timestamp;
        this.hash = hash;
        this.deviceId = ""+ Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        this.enpoint = endpoint;
    }
}
