package com.guru.kantewala.rest.response;

public class PhonePeOrderRP {
    String checksum;
    String base64Body;
    String phonePeEndPoint;
    String orderId;

    public String getOrderId() {
        return orderId;
    }

    public PhonePeOrderRP() {
    }

    public String getCheckSum() {
        return checksum;
    }

    public String getBase64Body() {
        return base64Body;
    }

    public String getPhonePeEndPoint() {
        return phonePeEndPoint;
    }
}
