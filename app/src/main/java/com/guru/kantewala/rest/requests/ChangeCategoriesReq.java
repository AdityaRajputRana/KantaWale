package com.guru.kantewala.rest.requests;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ChangeCategoriesReq {
    List<Integer> selectedCategories;
    int companyId;
    String uid;

    public ChangeCategoriesReq(List<Integer> selectedCategories, int companyId) {
        this.selectedCategories = selectedCategories;
        this.companyId = companyId;
        this.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
