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

package com.example.jabwemate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class PairDogDetailsActivity extends AppCompatActivity {

   String URL, ID, ownerid,URL_list;
   private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
   private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
   TextView DogName, Age, Breed, Gender, OwnerName, Phone, Email, City;
   ImageView DogImage, CallUser, EmailUser;
   ProgressDialog progressDialog;
   private static final int REQUEST_PHONE_CALL = 1;
   private Button SeeAllPhotos;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_pair_dog_details);

      DogName = findViewById(R.id.pair_desc_dog_name);
      Age = findViewById(R.id.pair_desc_dog_age);
      Breed = findViewById(R.id.pair_desc_dog_breed);
      Gender = findViewById(R.id.pair_desc_dog_gender);
      DogImage = findViewById(R.id.pair_desc_image);
      OwnerName = findViewById(R.id.pair_desc_owner_name);
      Phone = findViewById(R.id.pair_desc_owner_phone);
      Email = findViewById(R.id.pair_desc_owner_email);
      City = findViewById(R.id.pair_desc_owner_city);
      CallUser = findViewById(R.id.call_user);
      EmailUser = findViewById(R.id.email_user);
      SeeAllPhotos = findViewById(R.id.see_all_photos_pair);

      progressDialog = new ProgressDialog(this);
      progressDialog.setTitle("Loading");
      progressDialog.setMessage("Loading image may take some extra time...");
      progressDialog.setCancelable(false);

      Intent i = getIntent();
      ID = i.getStringExtra("REF");

      progressDialog.show();

      Runnable progressRunnable = new Runnable() {

         @Override
         public void run() {
            progressDialog.cancel();
         }
      };

      Handler pdCanceller = new Handler();
      pdCanceller.postDelayed(progressRunnable, 1200);

      firestore.collection("Dogs").document(ID).get()
              .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                 @Override
                 public void onSuccess(DocumentSnapshot documentSnapshot) {

                    DogName.setText(String.valueOf(documentSnapshot.getString("Name")));
                    Age.setText(String.valueOf(documentSnapshot.getString("Age")));
                    Gender.setText(String.valueOf(documentSnapshot.getString("Gender")));
                    Breed.setText(String.valueOf(documentSnapshot.getString("Breed")));
                    OwnerName.setText(String.valueOf(documentSnapshot.getString("name")));
                    City.setText(String.valueOf(documentSnapshot.getString("city")));
                    URL = String.valueOf(documentSnapshot.getString("URL"));
                    ownerid = String.valueOf(documentSnapshot.getString("UID"));
                    URL_list=String.valueOf(documentSnapshot.get("URL List"));
                    if (URL != null) {
                       Picasso
                               .get()
                               .load(Uri.parse(URL))
                               .fit()
                               .centerCrop()
                               .into(DogImage);
                    }

                    firestore.collection("Users").document(ownerid).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                               @Override
                               public void onSuccess(DocumentSnapshot documentSnapshot) {

                                  Email.setText(documentSnapshot.getString("email"));
                                  Phone.setText(documentSnapshot.getString("phone"));

                               }
                            });

                    SeeAllPhotos.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                          Intent i=new Intent(PairDogDetailsActivity.this,SeeAllPhotosActivity.class);
                          i.putExtra("URL list",URL_list);
                          startActivity(i);

                       }
                    });

                 }
              });

      CallUser.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {

            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Phone.getText().toString().trim()));
            if (ContextCompat.checkSelfPermission(PairDogDetailsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
               ActivityCompat.requestPermissions(PairDogDetailsActivity
                       .this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
            }
            else
            {
               startActivity(intent);
            }
         }
      });

      EmailUser.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto",Email.getText().toString(), null));
            startActivity(Intent.createChooser(emailIntent, "Send email..."));;

         }
      });

   }



}
