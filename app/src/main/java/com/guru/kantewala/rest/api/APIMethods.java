package com.guru.kantewala.rest.api;

import com.guru.kantewala.rest.api.interfaces.APIResponseListener;
import com.guru.kantewala.rest.requests.RegisterProfileReq;
import com.guru.kantewala.rest.response.MessageRP;

public class APIMethods {

    public static void registerProfile(RegisterProfileReq req, APIResponseListener<MessageRP> listener) {
        API.postData(listener, req, EndPoints.registerProfile, MessageRP.class);
    }

}