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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.example.jabwemate.HomePadeAdapter.HomeAdapter;
import com.example.jabwemate.consts.SharedPrefConsts;
import com.example.jabwemate.model.Dog;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class HomePage extends AppCompatActivity {

    androidx.appcompat.widget.Toolbar toolbar;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference =firestore.collection("Dogs");
    String userID;
    private HomeAdapter adapter;
    RadioGroup radioGroup;
    RadioButton radioButton;
    Button SearchButton;
    EditText SearchBreed;
    TextView Showing,Remove;
    ProgressDialog progressDialog;
    String owner,ownerphone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        toolbar = findViewById(R.id.home_page_toolbar);
        setSupportActionBar(toolbar);
        radioGroup = findViewById(R.id.radioGroup);
        SearchBreed = findViewById(R.id.breed_filter);
        SearchButton = findViewById(R.id.filter_button);

        Showing = findViewById(R.id.showtext);
        Remove = findViewById(R.id.filter_remove);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Showing data...");

        showAllDogs();

        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFilterData();
            }
        });

        Remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAllDogs();
            }
        });


    }

    private void showAllDogs() {

        progressDialog.show();

        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                progressDialog.cancel();
            }
        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 1000);

        Query query = collectionReference.orderBy("Name", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Dog> options = new FirestoreRecyclerOptions.Builder<Dog>()
                .setQuery(query, Dog.class)
                .build();

        adapter = new HomeAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.home_page_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        Remove.setVisibility(View.GONE);
        Showing.setText(R.string.showingDogs);


    }

    private void loadFilterData() {

        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);

        String breedname = SearchBreed.getText().toString().toLowerCase().trim();
        String gender = radioButton.getText().toString().trim();

        if(breedname.isEmpty()){
            SearchBreed.setError("Please enter breed");
            SearchBreed.requestFocus();
            return;
        }

        progressDialog.show();

        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                progressDialog.cancel();
            }
        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 1000);

        assert firebaseAuth.getCurrentUser() != null;

        userID = firebaseAuth.getCurrentUser().getUid();

        Query query = collectionReference
                .whereEqualTo("BreedLowerCase",breedname)
                .whereEqualTo("Gender",gender);
        FirestoreRecyclerOptions<Dog> options = new FirestoreRecyclerOptions.Builder<Dog>()
                .setQuery(query, Dog.class)
                .build();

        adapter = new HomeAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.home_page_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        Showing.setText(R.string.filter);
        Remove.setVisibility(View.VISIBLE);

    }

    public void checkButton(View v) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.your_info:

                // Add code here
                startActivity(new Intent(getApplicationContext(),YourInformationActivity.class));
                break;

            case R.id.your_pet_info:

                // Add Code here
                startActivity(new Intent(getApplicationContext(),myDog.class));
                break;

            case R.id.buysell:

                startActivity(new Intent(getApplicationContext(),BuySellActivity.class));
                break;

            case R.id.requests:

                //Add code here

                startActivity(new Intent(getApplicationContext(),RequestActivity.class));
                break;

            case R.id.shortlist:

                // Add code here

                startActivity(new Intent(getApplicationContext(), MyShortlistsActivity.class));
                break;


            case R.id.sign_out:

                SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("login", SharedPrefConsts.NO_LOGIN);
                editor.apply();
                finish();

                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();


            default:
                return super.onOptionsItemSelected(item);

        }

        return true;

    }



}
