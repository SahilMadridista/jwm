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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jabwemate.HomePadeAdapter.PairAdapter;
import com.example.jabwemate.model.Dog;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

public class DogPairsActivity extends AppCompatActivity {

   TextView DogName,DogAge,DogBreed,DogGender,PairHead;
   ImageView DogImage;
   ProgressDialog progressDialog;
   String ID,URL;
   String headtext = "Matches of ";
   PairAdapter adapter;
   FirebaseFirestore firestore = FirebaseFirestore.getInstance();
   FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
   CollectionReference collectionReference = firestore.collection("Dogs");

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
              WindowManager.LayoutParams.FLAG_FULLSCREEN);
      setContentView(R.layout.activity_dog_pairs);

      DogName = findViewById(R.id.pair_dogname);
      DogAge = findViewById(R.id.pair_dogage);
      DogBreed = findViewById(R.id.pair_dogbreed);
      DogGender = findViewById(R.id.pair_doggender);
      DogImage = findViewById(R.id.pair_dog_image);
      PairHead = findViewById(R.id.pair_header);

      progressDialog = new ProgressDialog(this);
      progressDialog.setTitle("Loading");
      progressDialog.setMessage("Loading image may time some extra time...");
      progressDialog.setCancelable(false);

      Intent i = getIntent();
      ID = i.getStringExtra("REF");

      setUpDetails();
      setAdapter(ID);


   }

   private void setUpDetails() {

      progressDialog.show();

      Runnable progressRunnable = new Runnable() {

         @Override
         public void run() {
            progressDialog.cancel();
         }
      };

      Handler pdCanceller = new Handler();
      pdCanceller.postDelayed(progressRunnable, 2500);

      FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

      firebaseFirestore.collection("Dogs").document(ID)
              .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
         @Override
         public void onSuccess(DocumentSnapshot documentSnapshot) {

            DogName.setText(String.valueOf(documentSnapshot.getString("Name")));
            DogBreed.setText(String.valueOf(documentSnapshot.getString("Breed")));
            DogAge.setText(String.valueOf(documentSnapshot.getString("Age")));
            DogGender.setText(String.valueOf(documentSnapshot.getString("Gender")));
            String name = DogName.getText().toString().trim();
            String head = headtext+name;
            PairHead.setText(head);
            URL = String.valueOf(documentSnapshot.getString("URL"));

            if (URL != null) {
               Picasso
                       .get()
                       .load(Uri.parse(URL))
                       .fit()
                       .centerCrop()
                       .into(DogImage);

            }

         }
      });

   }

   private void setAdapter(String id) {

      Query query = collectionReference
              .whereArrayContains("Pair", id);
      FirestoreRecyclerOptions<Dog> options = new FirestoreRecyclerOptions.Builder<Dog>()
              .setQuery(query, Dog.class)
              .build();

      adapter = new PairAdapter(options);
      adapter.getId(ID);
      RecyclerView recyclerView = findViewById(R.id.partner_recycler_view);
      recyclerView.setHasFixedSize(true);
      recyclerView.setLayoutManager(new LinearLayoutManager(this));
      recyclerView.setAdapter(adapter);
      adapter.startListening();

   }

}
