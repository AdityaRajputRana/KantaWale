package com.guru.kantewala.rest.requests;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

public class ImageReq {
    String uid;
    int imageId;

    public ImageReq(int imageId) {
        this.imageId = imageId;
        Log.i("KW-L-ImageId", String.valueOf(imageId));
        this.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
