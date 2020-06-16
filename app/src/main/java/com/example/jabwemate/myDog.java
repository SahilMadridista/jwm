package com.example.jabwemate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jabwemate.HomePadeAdapter.HomeAdapter;
import com.example.jabwemate.HomePadeAdapter.MyDogAdapter;
import com.example.jabwemate.model.Dog;
import com.example.jabwemate.model.Upload;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import maes.tech.intentanim.CustomIntent;

public class myDog extends AppCompatActivity {

    FloatingActionButton FAB;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private String UserID;
    private String ownername, ownerphone,city;
    androidx.appcompat.widget.Toolbar toolbar;
    private HomeAdapter adapter;
    private CollectionReference collectionReference = firestore.collection("Dogs");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_dog);

        assert firebaseAuth.getCurrentUser()!=null;
        UserID = firebaseAuth.getCurrentUser().getUid();

        toolbar = findViewById(R.id.your_dog_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getName();

        setUpRecyclerview();

    }

    private void setUpRecyclerview() {

        Query query = collectionReference.whereEqualTo("UID",UserID);
        FirestoreRecyclerOptions<Dog> options = new FirestoreRecyclerOptions.Builder<Dog>()
                .setQuery(query, Dog.class)
                .build();

        adapter = new HomeAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.your_dogs_recycler_view);
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

    public void getName() {
        DocumentReference documentReference = firestore.collection("Users").document(UserID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ownername = String.valueOf(documentSnapshot.getString("name"));
                ownerphone = String.valueOf(documentSnapshot.getString("phone"));
                city=String.valueOf(documentSnapshot.getString("city"));
                FAB = findViewById(R.id.fab);
                FAB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(myDog.this,AddDogActivity.class);
                        i.putExtra("Owner Name", ownername);
                        i.putExtra("Owner Phone", ownerphone);
                        i.putExtra("City",city);
                        startActivity(i);
                        CustomIntent.customType(myDog.this,"bottom-to-up");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(myDog.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
