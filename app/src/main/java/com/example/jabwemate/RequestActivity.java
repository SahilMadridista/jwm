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

import android.os.Bundle;

import com.example.jabwemate.HomePadeAdapter.MyDogAdapter;
import com.example.jabwemate.model.Dog;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class RequestActivity extends AppCompatActivity {

   androidx.appcompat.widget.Toolbar toolbar;
   private FirebaseAuth firebaseAuth;
   private FirebaseFirestore firestore;
   String UserID;
   MyDogAdapter adapter;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_request);

      firebaseAuth = FirebaseAuth.getInstance();
      firestore = FirebaseFirestore.getInstance();

      CollectionReference collectionReference = firestore.collection("Dogs");

      toolbar = findViewById(R.id.requests_toolbar);
      setSupportActionBar(toolbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);

      assert firebaseAuth.getCurrentUser() != null;

      UserID = firebaseAuth.getCurrentUser().getUid();

      Query query = collectionReference.whereEqualTo("UID",UserID);
      FirestoreRecyclerOptions<Dog> options = new FirestoreRecyclerOptions.Builder<Dog>()
              .setQuery(query, Dog.class)
              .build();

      adapter = new MyDogAdapter(options);

      RecyclerView recyclerView = findViewById(R.id.req_recycler_view);
      recyclerView.setHasFixedSize(true);
      recyclerView.setLayoutManager(new LinearLayoutManager(this));
      recyclerView.setAdapter(adapter);

   }

   @Override
   protected void onStart() {
      super.onStart();
      adapter.startListening();
   }
   @Override
   protected void onStop() {
      super.onStop();
      adapter.stopListening();
   }

}
