package com.guru.kantewala.rest.requests;

import com.google.firebase.auth.FirebaseAuth;

public class ImageBlockReq {
    String uid;
    int blockId;
    int companyId;
    String name;

    public ImageBlockReq(int companyId, String name) {
        this.companyId = companyId;
        this.name = name;
        this.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
