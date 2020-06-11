package com.example.jabwemate.model;

public class Upload {

    private String imageURL;

    public Upload(){
        //Empty constructor needed
    }

    public Upload(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

}
