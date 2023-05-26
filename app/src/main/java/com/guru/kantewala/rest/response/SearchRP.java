package com.guru.kantewala.rest.response;

import com.guru.kantewala.Models.Company;

import java.util.ArrayList;

public class SearchRP {
    int page;
    String keyword;
    int stateCode;
    int categoryId;
    int totalPages;
    int totalAvailable;
    boolean moreResultsAvailable(){
        return page > totalPages-1;
    }
    ArrayList<Company> companies;

    public ArrayList<Company> getRecommendedCompanies() {
        if (companies == null)
            companies = new ArrayList<>();
        return companies;
    }
}
