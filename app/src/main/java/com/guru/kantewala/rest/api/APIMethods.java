package com.guru.kantewala.rest.api;

import android.content.Context;
import android.net.Uri;

import com.guru.kantewala.Models.Company;
import com.guru.kantewala.Models.State;
import com.guru.kantewala.Models.SubscriptionPack;
import com.guru.kantewala.RegisterActivity;
import com.guru.kantewala.Tools.Utils;
import com.guru.kantewala.rest.api.interfaces.APIResponseListener;
import com.guru.kantewala.rest.requests.CompanyReq;
import com.guru.kantewala.rest.requests.EditCompanyDetailsReq;
import com.guru.kantewala.rest.requests.FileReq;
import com.guru.kantewala.rest.requests.GenerateOrderReq;
import com.guru.kantewala.rest.requests.HomeReq;
import com.guru.kantewala.rest.requests.RegisterProfileReq;
import com.guru.kantewala.rest.requests.SearchReq;
import com.guru.kantewala.rest.requests.VerifyLessonPaymentReq;
import com.guru.kantewala.rest.response.CategoryRP;
import com.guru.kantewala.rest.response.DashboardRP;
import com.guru.kantewala.rest.response.LessonOrderIdRp;
import com.guru.kantewala.rest.response.MessageRP;
import com.guru.kantewala.rest.response.MyCompanyRP;
import com.guru.kantewala.rest.response.SearchRP;
import com.guru.kantewala.rest.response.SubscriptionPackagesRP;
import com.guru.kantewala.rest.response.UserRP;

import java.util.ArrayList;

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

    public static void editCompanyDetails(EditCompanyDetailsReq req, APIResponseListener<MessageRP> listener){
        API.postData(listener, req, EndPoints.saveCompanyDetails, MessageRP.class);
    }

    public static void getDashboard(APIResponseListener<DashboardRP> listener) {
        HomeReq req = new HomeReq();
        API.postData(listener, req, EndPoints.dashboard, DashboardRP.class);
    }

    public static void getCategories(APIResponseListener<CategoryRP> listener) {
        HomeReq req = new HomeReq();
        API.postData(listener, req, EndPoints.categories, CategoryRP.class);
    }

    public static void getCompany(int companyId, boolean isRecommendation, APIResponseListener<Company> listener) {
        CompanyReq req = new CompanyReq(companyId, isRecommendation);
        API.postData(listener, req, EndPoints.companyDetails, Company.class);
    }

    public static void getCompany(APIResponseListener<MyCompanyRP> listener) {
        CompanyReq req = new CompanyReq();
        API.postData(listener, req, EndPoints.getMyCompany, MyCompanyRP.class);
    }


    public static void getUserProfile(APIResponseListener<UserRP> listener){
        HomeReq req = new HomeReq();
        API.postData(listener, req, EndPoints.userProfile, UserRP.class);
    }

    public static void search(int page, String keyword, int categoryId, int stateCode,
                              APIResponseListener<SearchRP> listener){
        SearchReq req = new SearchReq(page, keyword, stateCode, categoryId);
        API.postData(listener, req, EndPoints.search, SearchRP.class);
    }

    public static void getSubscriptionPackages(APIResponseListener<SubscriptionPackagesRP> listener){
        HomeReq req = new HomeReq();
        API.postData(listener, req, EndPoints.subscriptionPackages, SubscriptionPackagesRP.class);
    }

    public static void getOrderId(ArrayList<State> states, SubscriptionPack pack, APIResponseListener<LessonOrderIdRp> listener){
        GenerateOrderReq req = new GenerateOrderReq(pack, states);
        API.postData(listener, req, EndPoints.generateOrder, LessonOrderIdRp.class);
    }

    public static void verifyPayment(String s, String order_id, APIResponseListener<MessageRP> listener){
        VerifyLessonPaymentReq req = VerifyLessonPaymentReq.getInstance(s, order_id);
        API.postData(listener, req, EndPoints.verifyOrder, MessageRP.class);
    }

}