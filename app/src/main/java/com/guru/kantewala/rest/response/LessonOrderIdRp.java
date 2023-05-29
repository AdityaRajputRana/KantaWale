package com.guru.kantewala.rest.response;


import com.guru.kantewala.Models.Order;

public class LessonOrderIdRp {
    String api_key;
    Order order;

    public String getApi_key() {
        return api_key;
    }

    public Order getOrder() {
        return order;
    }

    public LessonOrderIdRp() {
    }
}
