package com.guru.kantewala.rest.api;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.net.Uri;

import com.guru.kantewala.Models.Company;
import com.guru.kantewala.Models.CompanyImages;
import com.guru.kantewala.Models.State;
import com.guru.kantewala.Models.SubscriptionPack;
import com.guru.kantewala.RegisterActivity;
import com.guru.kantewala.Tools.Utils;
import com.guru.kantewala.rest.api.interfaces.APIResponseListener;
import com.guru.kantewala.rest.api.interfaces.FileTransferResponseListener;
import com.guru.kantewala.rest.requests.ChangeCategoriesReq;
import com.guru.kantewala.rest.requests.CompanyReq;
import com.guru.kantewala.rest.requests.EditCompanyDetailsReq;
import com.guru.kantewala.rest.requests.EditCompanyReq;
import com.guru.kantewala.rest.requests.FileReq;
import com.guru.kantewala.rest.requests.GenerateOrderReq;
import com.guru.kantewala.rest.requests.HomeReq;
import com.guru.kantewala.rest.requests.ImageBlockReq;
import com.guru.kantewala.rest.requests.ImageReq;
import com.guru.kantewala.rest.requests.RegisterProfileReq;
import com.guru.kantewala.rest.requests.SearchReq;
import com.guru.kantewala.rest.requests.VerifyLessonPaymentReq;
import com.guru.kantewala.rest.response.CategoryRP;
import com.guru.kantewala.rest.response.DashboardRP;
import com.guru.kantewala.rest.response.LessonOrderIdRp;
import com.guru.kantewala.rest.response.MessageRP;
import com.guru.kantewala.rest.response.MyCompanyRP;
import com.guru.kantewala.rest.response.PackHistoryRP;
import com.guru.kantewala.rest.response.PhonePeOrderRP;
import com.guru.kantewala.rest.response.SearchRP;
import com.guru.kantewala.rest.response.SubscriptionPackagesRP;
import com.guru.kantewala.rest.response.UnlockedStatesRP;
import com.guru.kantewala.rest.response.UserRP;

import java.util.ArrayList;
import java.util.List;

public class APIMethods {

    public static void uploadImageForBlock(Uri fileUri, Context context, CompanyImages.ImageBlock block, FileTransferResponseListener<MessageRP> listener){
        byte[] file = Utils.getImageBytes(fileUri, context);
        ImageBlockReq req = new ImageBlockReq(block.getId());
        API.postFile(context, listener, req, EndPoints.uploadCompanyImage, MessageRP.class, "testImage", "image/png", file);
    }

    public static void uploadCompanyLogo(Uri fileUri, Context context, int companyId, FileTransferResponseListener<MessageRP> listener){
        byte[] file = Utils.getImageBytes(fileUri, context);
        EditCompanyReq req = new EditCompanyReq(companyId);
        API.postFile(context, listener, req, EndPoints.uploadCompanyLogo, MessageRP.class, "logo", "image/png", file);
    }


    public static void uploadProfilePicture(Uri fileUri, Context context, FileTransferResponseListener<MessageRP> listener){
        byte[] file = Utils.getImageBytes(fileUri, context);
        FileReq req = new FileReq();
        API.postFile(context, listener, req, EndPoints.updateProfilePhoto, MessageRP.class, "profilePhoto", "image/png", file);
    }

    public static void registerProfile(Context context, RegisterActivity.Mode mode, RegisterProfileReq req, APIResponseListener<MessageRP> listener) {
        String endpoint = EndPoints.registerProfile;
        if (mode == RegisterActivity.Mode.EDIT)
            endpoint = EndPoints.editProfile;
        API.postData(listener, req, endpoint, MessageRP.class, context);
    }

    public static void editCompanyDetails(Context context, EditCompanyDetailsReq req, APIResponseListener<MessageRP> listener){
        API.postData(listener, req, EndPoints.saveCompanyDetails, MessageRP.class, context);
    }

    public static void getDashboard(Context context, APIResponseListener<DashboardRP> listener) {
        HomeReq req = new HomeReq();
        API.postData(listener, req, EndPoints.dashboard, DashboardRP.class, context);
    }

    public static void createImageBlock(Context context, int companyId, String blockName, APIResponseListener<MessageRP> listener) {
        ImageBlockReq req = new ImageBlockReq(companyId, blockName);
        API.postData(listener, req, EndPoints.createImageBlock, MessageRP.class, context);
    }

    public static void getCategories(Context context, APIResponseListener<CategoryRP> listener) {
        HomeReq req = new HomeReq();
        API.postData(listener, req, EndPoints.categories, CategoryRP.class, context);
    }

    public static void getCompany(Context context, int companyId, boolean isRecommendation, APIResponseListener<Company> listener) {
        CompanyReq req = new CompanyReq(companyId, isRecommendation);
        API.postData(listener, req, EndPoints.companyDetails, Company.class, context);
    }

    public static void getCompany(Context context, APIResponseListener<MyCompanyRP> listener) {
        CompanyReq req = new CompanyReq();
        API.postData(listener, req, EndPoints.getMyCompany, MyCompanyRP.class, context);
    }


    public static void getUserProfile(Context context, APIResponseListener<UserRP> listener){
        HomeReq req = new HomeReq();
        API.postData(listener, req, EndPoints.userProfile, UserRP.class, context);
    }

    public static void getUserPurchaseHistory(Context context, APIResponseListener<PackHistoryRP> listener){
        HomeReq req = new HomeReq();
        API.postData(listener, req, EndPoints.purchaseHistory, PackHistoryRP.class, context);
    }

    public static void search(Context context, int page, String keyword, int categoryId, int stateCode,
                              APIResponseListener<SearchRP> listener){
        SearchReq req = new SearchReq(page, keyword, stateCode, categoryId);
        API.postData(listener, req, EndPoints.search, SearchRP.class, context);
    }

    public static void getSubscriptionPackages(Context context, APIResponseListener<SubscriptionPackagesRP> listener){
        HomeReq req = new HomeReq();
        API.postData(listener, req, EndPoints.subscriptionPackages, SubscriptionPackagesRP.class, context);
    }

    public static void getUnlockedStates(Context context, APIResponseListener<UnlockedStatesRP> listener){
        HomeReq req = new HomeReq();
        API.postData(listener, req, EndPoints.unlockedStates, UnlockedStatesRP.class, context);
    }

    public static void getOrderId(Context context, ArrayList<State> states, SubscriptionPack pack, APIResponseListener<LessonOrderIdRp> listener){
        GenerateOrderReq req = new GenerateOrderReq(pack, states);
        API.postData(listener, req, EndPoints.generateOrder, LessonOrderIdRp.class, context);
    }

    public static void generatePhonePeOrder(Context context, ArrayList<State> states, SubscriptionPack pack, APIResponseListener<PhonePeOrderRP> listener, String upiId){
        GenerateOrderReq req = new GenerateOrderReq(pack, states, upiId);
        API.postData(listener, req, EndPoints.generatePhonePeOrder, PhonePeOrderRP.class, context);
    }

    public static void verifyPhonePeOrder(Context context, String order_id, APIResponseListener<MessageRP> listener){
        VerifyLessonPaymentReq req = new VerifyLessonPaymentReq(order_id);
        API.postData(listener, req, EndPoints.verifyPhonePeOrder, MessageRP.class, context);
    }

    public static void verifyPayment(Context context, String s, String order_id, APIResponseListener<MessageRP> listener){
        VerifyLessonPaymentReq req = VerifyLessonPaymentReq.getInstance(s, order_id);
        API.postData(listener, req, EndPoints.verifyOrder, MessageRP.class, context);
    }

    public static void editImageBlock(Context context, CompanyImages.ImageBlock block, int companyId, String blockName, APIResponseListener<MessageRP> listener) {
        ImageBlockReq req = new ImageBlockReq(companyId, blockName, block.getId());
        API.postData(listener, req, EndPoints.editBlockName, MessageRP.class, context);
    }

    public static void deleteImageBlock(Context context, CompanyImages.ImageBlock block,APIResponseListener<MessageRP> listener) {
        ImageBlockReq req = new ImageBlockReq(block.getId());
        API.postData(listener, req, EndPoints.deleteImageBlock, MessageRP.class, context);
    }

    public static void deleteImage(Context context, CompanyImages.ImageBlock.Image image, APIResponseListener<MessageRP> listener) {
        ImageReq req = new ImageReq(image.getId());
        API.postData(listener, req, EndPoints.deleteImage, MessageRP.class, context);
    }

    public static void updateCategories(Context context, int companyId, List<Integer> selectedCategories, APIResponseListener<MessageRP> listener) {
        ChangeCategoriesReq req = new ChangeCategoriesReq(selectedCategories, companyId);
        API.postData(listener, req, EndPoints.updateCompanyCategories, MessageRP.class, context);
    }
}