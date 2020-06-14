package com.example.jabwemate.model;



public class Dog {

    private String URL;
    private String Name;
    private String Gender;
    private String Breed;


    public Dog(){
        //Empty constructor needed
    }

   public Dog(String name, String gender, String breed,String URL) {
      Name = name;
      Gender = gender;
      Breed = breed;
      this.URL=URL;

   }

   public String getName() {
      return Name;
   }

   public String getGender() {
      return Gender;
   }

   public String getBreed() {
      return Breed;
   }

    public String getURL() {
        return URL;
    }


}
