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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.jabwemate.consts.SharedPrefConsts;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private Button MakeRequest,SeeAllPhotos;
    private TextView Owner, Dog, Breed, Age, City, Gender;
    private String owner, dog, breed, age, city, gender, ID, URL,URL_list;
    private FirebaseFirestore dogs_db = FirebaseFirestore.getInstance();
    private ImageView image;
    androidx.appcompat.widget.Toolbar toolbar;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Owner = findViewById(R.id.detail_owner);
        Dog = findViewById(R.id.detail_dog);
        Breed = findViewById(R.id.detail_breed);
        Age = findViewById(R.id.detail_age);
        City = findViewById(R.id.detail_city);
        Gender = findViewById(R.id.detail_gender);
        MakeRequest = findViewById(R.id.detail_make_req);
        SeeAllPhotos = findViewById(R.id.see_all_photos_details);
        image = findViewById(R.id.desc_dog_image);

        toolbar = findViewById(R.id.dog_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading information...");
        progressDialog.setCancelable(false);

        Intent i = getIntent();
        ID = i.getStringExtra("REF");

        setView();

        MakeRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),ChooseDogForRequestActivity.class);
                i.putExtra("REG",ID);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.report_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.report_item:
            {
                String S="I found this user inappropriate and fake. \nThis user is not valid and is a dummy account causing malpractice and disturbance.\n\n UserID is... ";
                S=S.concat(ID+"\n\nThanks User");

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_TEXT,S);
                intent.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] { "fartmagazine81@gmail.com" });
                intent.putExtra(Intent.EXTRA_SUBJECT, "REPORT");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }



            default:
                return super.onOptionsItemSelected(item);

        }

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
                        Intent i=new Intent(DetailActivity.this,SeeAllPhotosActivity.class);
                        i.putExtra("URL list",URL_list);
                        startActivity(i);

                    }
                });

            }
        });

    }

}
