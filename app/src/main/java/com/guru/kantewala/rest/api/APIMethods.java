package com.guru.kantewala.rest.api;

import android.content.Context;
import android.net.Uri;

import com.guru.kantewala.Models.Company;
import com.guru.kantewala.RegisterActivity;
import com.guru.kantewala.Tools.Utils;
import com.guru.kantewala.rest.api.interfaces.APIResponseListener;
import com.guru.kantewala.rest.requests.CompanyReq;
import com.guru.kantewala.rest.requests.FileReq;
import com.guru.kantewala.rest.requests.HomeReq;
import com.guru.kantewala.rest.requests.RegisterProfileReq;
import com.guru.kantewala.rest.response.CategoryRP;
import com.guru.kantewala.rest.response.DashboardRP;
import com.guru.kantewala.rest.response.MessageRP;
import com.guru.kantewala.rest.response.UserRP;

public class APIMethods {

    public static void uploadProfilePicture(Uri fileUri, Context context, APIResponseListener<MessageRP> listener){
        String file = Utils.getEncodedCompressedProfilePic(fileUri, context);
        FileReq req = new FileReq(file);
        API.postData(listener, req, EndPoints.updateProfilePhoto, MessageRP.class);
    }

    public static void registerProfile(RegisterActivity.Mode mode, RegisterProfileReq req, APIResponseListener<MessageRP> listener) {
        String endpoint = EndPoints.registerProfile;
        if (mode == RegisterActivity.Mode.EDIT)
            endpoint = EndPoints.editProfile;
        API.postData(listener, req, endpoint, MessageRP.class);
    }

    public static void getDashboard(APIResponseListener<DashboardRP> listener) {
        HomeReq req = new HomeReq();
        API.postData(listener, req, EndPoints.dashboard, DashboardRP.class);
    }

    public static void getCategories(APIResponseListener<CategoryRP> listener) {
        HomeReq req = new HomeReq();
        API.postData(listener, req, EndPoints.categories, CategoryRP.class);
    }

    public static void getCompany(int companyId, APIResponseListener<Company> listener) {
        CompanyReq req = new CompanyReq(companyId);
        API.postData(listener, req, EndPoints.companyDetails, Company.class);
    }

    public static void getUserProfile(APIResponseListener<UserRP> listener){
        HomeReq req = new HomeReq();
        API.postData(listener, req, EndPoints.userProfile, UserRP.class);
    }

}