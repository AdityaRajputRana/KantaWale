package com.guru.kantewala.rest.response;

public class UserRP {
    boolean isRegisteredUser;
    String id;
    String name;
    String phone;
    String uid;
    String email;
    String company;
    String photoUrl;
    String companyGST;
    String city;
    String state;
    int stateCode;

    public boolean isRegisteredUser() {
        return isRegisteredUser;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getCompany() {
        return company;
    }

    public String getPhotoUrl() {
        if (photoUrl == null)
            return "";
        return photoUrl;
    }

    public String getCompanyGST() {
        return companyGST;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public int getStateCode() {
        return stateCode;
    }

    public UserRP() {
    }
}
