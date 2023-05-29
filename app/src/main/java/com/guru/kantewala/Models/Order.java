package com.guru.kantewala.Models;

public class Order {
    String id;
    String entity;
    int amount;
    int amount_paid;
    int amount_due;
    String currency;
    String receipt;
    String status;
    int attempts;
    long created_at;

    public String getId() {
        return id;
    }

    public String getEntity() {
        return entity;
    }

    public int getAmount() {
        return amount;
    }

    public int getAmount_paid() {
        return amount_paid;
    }

    public int getAmount_due() {
        return amount_due;
    }

    public String getCurrency() {
        return currency;
    }

    public String getReceipt() {
        return receipt;
    }

    public String getStatus() {
        return status;
    }

    public int getAttempts() {
        return attempts;
    }

    public long getCreated_at() {
        return created_at;
    }

    public Order() {
    }
}
