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

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class RequestRejectAccept extends AppCompatActivity {

    private Button Accept,Reject,SeeAllPhotos;
    private TextView Owner, Dog, Breed, Age, City, Gender;
    private String owner, dog, breed, age, city, gender, ID, URL,IDD,URL_list;
    private FirebaseFirestore dogs_db = FirebaseFirestore.getInstance();
    private ImageView image;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_reject_accept);

        Owner = findViewById(R.id.detail_owner_req);
        Dog = findViewById(R.id.detail_dog_req);
        Breed = findViewById(R.id.detail_breed_req);
        Age = findViewById(R.id.detail_age_req);
        City = findViewById(R.id.detail_city_req);
        Gender = findViewById(R.id.detail_gender_req);
        Accept = findViewById(R.id.accept_req);
        Reject = findViewById(R.id.reject_req);
        SeeAllPhotos = findViewById(R.id.see_all_photos_request);
        image = findViewById(R.id.desc_dog_image_req);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading information...");
        progressDialog.setCancelable(false);

        Intent i = getIntent();
        ID = i.getStringExtra("REF");
        IDD=i.getStringExtra("ID");

        setView();

        Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               addID(ID,IDD);
            }
        });
        Reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               removeId(ID,IDD);
            }
        });
    }

    private void addID(String id, String id2) {
        dogs_db.collection("Dogs").document(id).update("Accept", FieldValue.arrayRemove(id2))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("ACCEPT removed","Successfully removed accecpt");
                    }
                });

        dogs_db.collection("Dogs").document(id).update("Pair",FieldValue.arrayUnion(id2))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RequestRejectAccept.this,"Added in pair Check Match",Toast.LENGTH_SHORT).show();
                    }
                });
        dogs_db.collection("Dogs").document(id2).update("Pair",FieldValue.arrayUnion(id))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(RequestRejectAccept.this,RequestActivity.class));
                        finish();
                        Toast.makeText(RequestRejectAccept.this,"Success",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeId(String id,String id2) {
        dogs_db.collection("Dogs").document(id).update("Accept", FieldValue.arrayRemove(id2))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(RequestRejectAccept.this,RequestActivity.class));
                        finish();
                        Toast.makeText(RequestRejectAccept.this,"Rejected",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setView() {

        progressDialog.show();

        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                progressDialog.cancel();
            }
        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 2000);

        dogs_db.collection("Dogs").document(ID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                owner = String.valueOf(documentSnapshot.getString("name"));
                breed = String.valueOf(documentSnapshot.getString("Breed"));
                dog = String.valueOf(documentSnapshot.getString("Name"));
                age = String.valueOf(documentSnapshot.getString("Age"));
                city = String.valueOf(documentSnapshot.getString("city"));
                gender = String.valueOf(documentSnapshot.getString("Gender"));
                URL = String.valueOf(documentSnapshot.getString("URL"));
                URL_list=String.valueOf(documentSnapshot.get("URL List"));
                if (URL != null) {
                    Picasso
                            .get()
                            .load(Uri.parse(URL))
                            .fit()
                            .centerCrop()
                            .into(image);
                }

                Owner.setText(owner);
                Breed.setText(breed);
                Dog.setText(dog);
                Age.setText(age);
                City.setText(city);
                Gender.setText(gender);

                SeeAllPhotos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i=new Intent(RequestRejectAccept.this,SeeAllPhotosActivity.class);
                        i.putExtra("URL list",URL_list);
                        startActivity(i);

                    }
                });
            }
        });

    }

}

