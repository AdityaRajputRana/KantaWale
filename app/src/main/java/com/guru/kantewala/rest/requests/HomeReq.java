package com.guru.kantewala.rest.requests;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeReq {
    String uid;
    String phoneNumber;

    public HomeReq() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        this.uid = user.getUid();
        this.phoneNumber = user.getPhoneNumber();
    }
}
