package com.guru.kantewala.rest.api;

import com.guru.kantewala.rest.api.interfaces.APIResponseListener;
import com.guru.kantewala.rest.requests.HomeReq;
import com.guru.kantewala.rest.requests.RegisterProfileReq;
import com.guru.kantewala.rest.response.CategoryRP;
import com.guru.kantewala.rest.response.DashboardRP;
import com.guru.kantewala.rest.response.MessageRP;

public class APIMethods {

    public static void registerProfile(RegisterProfileReq req, APIResponseListener<MessageRP> listener) {
        API.postData(listener, req, EndPoints.registerProfile, MessageRP.class);
    }

    public static void getDashboard(APIResponseListener<DashboardRP> listener) {
        HomeReq req = new HomeReq();
        API.postData(listener, req, EndPoints.dashboard, DashboardRP.class);
    }

    public static void getCategories(APIResponseListener<CategoryRP> listener) {
        HomeReq req = new HomeReq();
        API.postData(listener, req, EndPoints.categories, CategoryRP.class);
    }

}