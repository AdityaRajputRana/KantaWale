package com.guru.kantewala.Models;

import com.guru.kantewala.Tools.Constants;

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
    String phoneNumber;
    String emailId;
    String gst;
    String uid;
    int userId;

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
        return location;
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
        return String.valueOf(city) + ", " + Constants.getIndianStates().get(stateCode);
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
