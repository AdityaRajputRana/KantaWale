package com.guru.kantewala.rest.requests;

public class RegisterProfileReq {
    String name;
    String phoneNumber;
    String uid;
    String email;
    String companyName;
    String gst;
    String city;
    String state;
    int stateCode;

    public RegisterProfileReq(
            String name, String phoneNumber, String uid,
            String email, String companyName, String gst,
            String city, String state, int stateCode, String photoUrl) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.uid = uid;
        this.email = email;
        this.companyName = companyName;
        this.gst = gst;
        this.city = city;
        this.state = state;
        this.stateCode = stateCode;
    }
}
