package com.guru.kantewala.rest.requests;

import com.google.firebase.auth.FirebaseAuth;

public class CompanyReq {
    int companyId;
    String uid;
    boolean isRecommendation;

    public CompanyReq(int companyId) {
        this.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.companyId = companyId;
        this.isRecommendation = false;
    }

    public CompanyReq(int companyId, boolean isRecommendation) {
        this.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.companyId = companyId;
        this.isRecommendation = isRecommendation;
    }
}
