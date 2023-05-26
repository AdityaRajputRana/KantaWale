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

    public ArrayList<String> getCategoriesForDropDown(){
        ArrayList<String> mCats = new ArrayList<>();
        mCats.add("All Categories");
        for (Category c: getCategories()){
            mCats.add(c.getName());
        }
        return mCats;
    }

    public int getIdFromDropDownItem(String c, int position){
        int index =  position - 1;
        Category category = getCategories().get(index);
        if (category.getName().equals(c)){
            return category.getId();
        }
        return -1;
    }

    public int getPositionFromID(int id) {
        for (int i = 0; i < getCategories().size(); i++){
            if (getCategories().get(i).getId() == id){
                return i + 1;
            }
        }
        return 0;
    }
}
