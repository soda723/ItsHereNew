package com.capstone.itshere.coupon;

public class ImageModel {
    private String imageUrl;
    ImageModel(){}

    public ImageModel(String mimageUrl){
        this.imageUrl = mimageUrl;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public void setImageUrl(String mimageUrl){
        this.imageUrl = mimageUrl;
    }
}
