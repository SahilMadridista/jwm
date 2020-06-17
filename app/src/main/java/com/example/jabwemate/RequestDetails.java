package com.example.jabwemate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jabwemate.HomePadeAdapter.RequestAdaptar;
import com.example.jabwemate.model.Dog;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

public class RequestDetails extends AppCompatActivity {

    TextView Name, Age, Breed, Gender;
    ImageView Image;
    String ID, URL;
    ProgressDialog progressDialog;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firestore.collection("Dogs");
    private RequestAdaptar adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);

        Name = findViewById(R.id.req_dog_name);
        Age = findViewById(R.id.req_dog_age);
        Breed = findViewById(R.id.req_dog_breed);
        Gender = findViewById(R.id.req_dog_gender);
        Image = findViewById(R.id.req_details_dog_image);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading image may time some extra time...");
        progressDialog.setCancelable(false);

        Intent i = getIntent();
        ID = i.getStringExtra("REF");


        setUpView();
        setAdaptar(ID);

    }

    private void setAdaptar(String id) {
        Query query = collectionReference
                .whereArrayContains("Accept", id);
        FirestoreRecyclerOptions<Dog> options = new FirestoreRecyclerOptions.Builder<Dog>()
                .setQuery(query, Dog.class)
                .build();

        adapter = new RequestAdaptar(options);
        adapter.getId(ID);
        RecyclerView recyclerView = findViewById(R.id.req_details_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    private void setUpView() {

        progressDialog.show();

        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                progressDialog.cancel();
            }
        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 1500);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Dogs").document(ID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Name.setText(documentSnapshot.getString("Name"));
                Age.setText(documentSnapshot.getString("Age"));
                Breed.setText(documentSnapshot.getString("Breed"));
                Gender.setText(documentSnapshot.getString("Gender"));
                URL = String.valueOf(documentSnapshot.getString("URL"));

                if (URL != null) {
                    Picasso
                            .get()
                            .load(Uri.parse(URL))
                            .fit()
                            .centerCrop()
                            .into(Image);
                }

            }
        });


    }
}
