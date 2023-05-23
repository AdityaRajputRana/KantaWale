package com.guru.kantewala.rest.response;

import com.guru.kantewala.Models.Company;

import java.util.ArrayList;

public class DashboardRP {
    ArrayList<Company> recommendedCompanies;

    public DashboardRP() {
    }

    public ArrayList<Company> getRecommendedCompanies() {
        if (recommendedCompanies == null)
            recommendedCompanies = new ArrayList<Company>();
        return recommendedCompanies;
    }
}
