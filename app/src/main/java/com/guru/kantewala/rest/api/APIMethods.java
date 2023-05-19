package com.guru.kantewala.rest.api;

import com.guru.kantewala.rest.api.interfaces.APIResponseListener;
import com.guru.kantewala.rest.requests.RegisterProfileReq;
import com.guru.kantewala.rest.response.RegisterProfileRP;

public class APIMethods {

    public static void registerProfile(APIResponseListener<RegisterProfileRP> listener) {
        RegisterProfileReq req = new RegisterProfileReq();
        API.postData(listener, req, EndPoints.registerProfile, RegisterProfileRP.class);
    }

}