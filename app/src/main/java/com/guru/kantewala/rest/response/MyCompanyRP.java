package com.guru.kantewala.rest.response;

import com.guru.kantewala.Models.Company;

public class MyCompanyRP {
    boolean isCompanyLinked;
    Company company;
    UserRP user;

    public MyCompanyRP() {
    }

    public boolean isCompanyLinked() {
        return isCompanyLinked;
    }

    public Company getCompany() {
        return company;
    }

    public UserRP getUser() {
        return user;
    }
}
