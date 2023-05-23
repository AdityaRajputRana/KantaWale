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
