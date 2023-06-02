package com.guru.kantewala.Models;

import java.util.ArrayList;

public class CompanyImages {
    String companyId;
    ArrayList<ImageBlock> blocks;

    public CompanyImages() {
    }

    public String getCompanyId() {
        return companyId;
    }

    public ArrayList<ImageBlock> getBlocks() {
        if (blocks == null)
            blocks = new ArrayList<>();
        return blocks;
    }

    public class ImageBlock{
        int id;

        public int getId() {
            return id;
        }

        String title;
        ArrayList<String> photos;

        public ImageBlock() {
        }

        public String getTitle() {
            if (title == null)
                return "";
            return title;
        }

        public ArrayList<String> getPhotos() {
            if (photos == null)
                photos = new ArrayList<>();
            return photos;
        }
    }
}
