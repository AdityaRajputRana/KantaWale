package com.guru.kantewala.rest.response;

import com.guru.kantewala.Models.Category;

import java.util.ArrayList;

public class CategoryRP {
    ArrayList<Category> categories;

    public CategoryRP() {
    }

    public ArrayList<Category> getCategories() {
        if (categories == null)
            categories = new ArrayList<Category>();
        return categories;
    }
}
