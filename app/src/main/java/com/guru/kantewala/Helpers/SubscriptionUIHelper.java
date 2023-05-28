package com.guru.kantewala.Helpers;

import com.guru.kantewala.Models.SubscriptionPackage;
import com.guru.kantewala.rest.response.SubscriptionPackagesRP;

public class SubscriptionUIHelper {
    public interface AskToSubListener{
        void redirectToSubscribeFragment();
    }

    public static SubscriptionPackagesRP subscriptionPackagesRP;
}
