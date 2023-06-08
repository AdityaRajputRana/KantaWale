package com.guru.kantewala.Models;

public class PlanHistoryModel {
    SubscriptionPack pack;
    PlanDetails plan;

    public PlanHistoryModel() {
    }

    public SubscriptionPack getPack() {
        return pack;
    }

    public PlanDetails getPlan() {
        return plan;
    }
}
