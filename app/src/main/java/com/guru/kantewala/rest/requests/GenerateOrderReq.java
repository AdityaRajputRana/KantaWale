package com.guru.kantewala.rest.requests;

import com.google.firebase.auth.FirebaseAuth;
import com.guru.kantewala.Models.State;
import com.guru.kantewala.Models.SubscriptionPack;

import java.util.ArrayList;

public class GenerateOrderReq {
    SubscriptionPack selectedPack;
    ArrayList<State> selectedStates;
    String uid;

    public GenerateOrderReq(SubscriptionPack selectedPack, ArrayList<State> selectedStates) {
        this.selectedPack = selectedPack;
        this.selectedStates = selectedStates;
        this.uid = FirebaseAuth.getInstance().getUid();
    }
}
