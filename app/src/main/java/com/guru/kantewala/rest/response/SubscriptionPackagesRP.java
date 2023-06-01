package com.guru.kantewala.rest.response;

import com.guru.kantewala.Models.PlanDetails;
import com.guru.kantewala.Models.SubscriptionPack;

import java.util.ArrayList;

public class SubscriptionPackagesRP {
    boolean isPremiumUser;
    SubscriptionPack mPack;
    PlanDetails mDetails;

    public SubscriptionPack getMyPack() {
        return mPack;
    }

    public PlanDetails getMyDetails() {
        return mDetails;
    }

    //Todo: Subscription details for subscribed users in this
    ArrayList<SubscriptionPack> subscriptionPackages;
    public SubscriptionPack getMinimumPackage(){
        int price = Integer.MAX_VALUE;
        SubscriptionPack pack = new SubscriptionPack();
        for (SubscriptionPack p: getSubscriptionPackages()){
            if (p.isMinimum)
                return p;
            if (p.getPrice() < price){
                pack = p;
            }
        }
        return pack;
    }

    public boolean isPremiumUser() {
        return isPremiumUser;
    }

    public ArrayList<SubscriptionPack> getSubscriptionPackages() {
        if (subscriptionPackages == null)
            subscriptionPackages = new ArrayList<>();
        return subscriptionPackages;
    }
}
