package com.guru.kantewala.rest.requests;

import com.google.firebase.auth.FirebaseAuth;

public class EditCompanyDetailsReq {
    String uid;
    String companyName;
    String gst;
    String location;
    String city;
    int stateCode;

    public EditCompanyDetailsReq(String companyName, String gst, String location, String city, int stateCode) {
        this.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.companyName = companyName;
        this.gst = gst;
        this.location = location;
        this.city = city;
        this.stateCode = stateCode;
    }
}
