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

    public ImageBlockReq(int companyId, String blockName, int id) {
        this.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.companyId = companyId;
        name = blockName;
        blockId = id;
    }

    public ImageBlockReq(int id) {
        this.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        blockId = id;
    }
}
