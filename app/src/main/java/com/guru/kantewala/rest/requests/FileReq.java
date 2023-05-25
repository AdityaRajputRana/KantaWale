package com.guru.kantewala.rest.requests;

import com.google.firebase.auth.FirebaseAuth;

public class FileReq {
    String file;
    String ext;
    String path;
    String uid;

    public FileReq(String file) {
        this.file = file;
        this.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
