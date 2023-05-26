package com.guru.kantewala.rest.requests;

import com.google.firebase.auth.FirebaseAuth;

public class SearchReq {
    int page;
    String keyword;
    int stateCode;
    int categoryId;
    String uid;

    public SearchReq(int page, String keyword, int stateCode, int categoryId) {
        this.page = page;
        this.keyword = keyword;
        this.stateCode = stateCode;
        this.categoryId = categoryId;
        this.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
