package com.guru.kantewala.Models;

import java.util.ArrayList;

public class Company {
    int id;
    String logoUrl;
    String name;
    String location;
    int stateCode;
    String city;
    ArrayList<String> tags;
    boolean isLocked;


    boolean showDetails;
    String fullName;
    String address;
    String phoneNumber;
    String emailId;
    String gst;

    CompanyImages companyImages;

    public CompanyImages getCompanyImages() {
        return companyImages;
    }

    public boolean isShowDetails() {
        return showDetails;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getGst() {
        return gst;
    }

    public Company() {
    }

    public int getId() {
        return id;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public ArrayList<String> getTags() {
        if (tags == null)
            tags = new ArrayList<>();
        return tags;
    }

    public boolean isLocked() {
        return isLocked;
    }
}
