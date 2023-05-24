package com.guru.kantewala.rest.requests;

import com.google.firebase.auth.FirebaseAuth;

public class CompanyReq {
    int companyId;
    String uid;

    public CompanyReq(int companyId) {
        this.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.companyId = companyId;
    }
}
