package com.example.jabwemate.model;



public class Dog {

    private String dogname;
    private String doggender;
    private String dogbreed;

    public Dog(){
        //Empty constructor needed
    }

    public Dog(String dogname,String doggender, String dogbreed) {

        this.dogname = dogname;
        this.doggender = doggender;
        this.dogbreed = dogbreed;
    }

    public String getDogname() {
        return dogname;
    }


    public String getDoggender() {
        return doggender;
    }

    public String getDogbreed() {
        return dogbreed;
    }

}
