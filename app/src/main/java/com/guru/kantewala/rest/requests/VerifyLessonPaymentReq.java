package com.guru.kantewala.rest.requests;

import com.google.gson.Gson;
import com.razorpay.PaymentData;

public class VerifyLessonPaymentReq {
    String order_id;
    String razorpay_order_id;
    String razorpay_payment_id;
    String razorpay_signature;

    public static VerifyLessonPaymentReq getInstance(String s, String id){
        VerifyLessonPaymentReq req = new Gson().fromJson(s, VerifyLessonPaymentReq.class);
        req.order_id = id;
        return req;
    }

    public VerifyLessonPaymentReq(PaymentData paymentData){
        this.order_id = paymentData.getOrderId();
        this.razorpay_order_id = paymentData.getOrderId();
        this.razorpay_payment_id = paymentData.getPaymentId();
        this.razorpay_signature = paymentData.getSignature();
    }
}
