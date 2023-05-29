package com.guru.kantewala.Helpers;

import com.guru.kantewala.rest.response.SubscriptionPackagesRP;

public class SubscriptionInterface {
    public interface AskToSubListener{
        void redirectToSubscribeFragment();
    }

    public static SubscriptionPackagesRP subscriptionPackagesRP;
}
