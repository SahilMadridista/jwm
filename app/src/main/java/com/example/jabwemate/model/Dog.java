/*
 * Copyright (c) 2020 JabWeMet(shubham & sahil). All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jabwemate.model;



public class Dog {

    private String URL;
    private String Name;
    private String Gender;
    private String Breed;
    private String UID;


    public Dog(){
        //Empty constructor needed
    }

   public Dog(String name, String gender, String breed,String URL,String UID) {
      Name = name;
      Gender = gender;
      Breed = breed;
      this.URL=URL;
      this.UID=UID;

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

    public String getUID() {
        return UID;
    }
}
