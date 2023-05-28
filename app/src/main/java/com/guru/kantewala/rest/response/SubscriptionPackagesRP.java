package com.guru.kantewala.rest.response;

import com.guru.kantewala.Models.SubscriptionPackage;

import java.util.ArrayList;

public class SubscriptionPackagesRP {
    boolean isPremiumUser;
    //Todo: Subscription details for subscribed users in this
    ArrayList<SubscriptionPackage> subscriptionPackages;
    public SubscriptionPackage getMinimumPackage(){
        int price = Integer.MAX_VALUE;
        SubscriptionPackage pack = new SubscriptionPackage();
        for (SubscriptionPackage p: getSubscriptionPackages()){
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

    public ArrayList<SubscriptionPackage> getSubscriptionPackages() {
        if (subscriptionPackages == null)
            subscriptionPackages = new ArrayList<>();
        return subscriptionPackages;
    }
}
