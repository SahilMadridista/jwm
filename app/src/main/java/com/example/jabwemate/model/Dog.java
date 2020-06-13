package com.example.jabwemate.model;

import java.util.ArrayList;

public class Dog {

    public String dogname;
    public String dogage;
    public String doggender;
    public String dogbreed;
    public ArrayList<String> imageurl;  // Firebase storage download URL
    public String ownername;
    public String ownerphone;

    public Dog(){
        //Empty constructor needed
    }

    public Dog(String dogname, String dogage, String doggender,
               String dogbreed, ArrayList<String> imageurl,
               String ownername, String ownerphone) {

        this.dogname = dogname;
        this.dogage = dogage;
        this.doggender = doggender;
        this.dogbreed = dogbreed;
        this.imageurl = imageurl;
        this.ownername = ownername;
        this.ownerphone = ownerphone;
    }

    public String getDogname() {
        return dogname;
    }

    public String getDogage() {
        return dogage;
    }

    public String getDoggender() {
        return doggender;
    }

    public String getDogbreed() {
        return dogbreed;
    }

    public ArrayList<String> getImageurl() {
        return imageurl;
    }

    public String getOwnername() {
        return ownername;
    }

    public String getOwnerphone() {
        return ownerphone;
    }
}
