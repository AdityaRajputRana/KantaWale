package com.guru.kantewala.rest.requests;

import com.google.firebase.auth.FirebaseAuth;

public class EditCompanyReq {
    String logo;
    String uid;
    int companyId;

    public EditCompanyReq(String logo, int companyId) {
        this.logo = logo;
        this.companyId = companyId;
        this.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
