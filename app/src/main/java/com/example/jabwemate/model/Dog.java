package com.example.jabwemate.model;



public class Dog {

    private String Name;
    private String Gender;
    private String Breed;

    public Dog(){
        //Empty constructor needed
    }

   public Dog(String name, String gender, String breed) {
      Name = name;
      Gender = gender;
      Breed = breed;
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
}
