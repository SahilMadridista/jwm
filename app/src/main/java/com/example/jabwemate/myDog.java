package com.example.jabwemate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import maes.tech.intentanim.CustomIntent;

public class myDog extends AppCompatActivity {

    FloatingActionButton FAB;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private String UserID;
    private String ownername, ownerphone;
    androidx.appcompat.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_dog);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        UserID = firebaseAuth.getCurrentUser().getUid();

        toolbar = findViewById(R.id.your_dog_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getName();
    }

    /*
    public void getData(String email) {
        db.collection("dogs")
                .whereEqualTo("Email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                name= document.getString("Name");
                                breed = document.getString("Breed");
                                jobs.add(new listt2(tittle, companyName, salary, id));
                            }
                            ListAdaptar2 listAdapter = new ListAdaptar2(getActivity(), jobs);
                            ListView listView;
                            listView = (ListView) getView().findViewById(R.id.jobs_list_view);
                            listView.setAdapter(listAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    listt2 l=jobs.get(position);
                                    Intent intent=new Intent(getActivity(),JobView.class);
                                    intent.putExtra("Id",l.getId());
                                    startActivity(intent);
                                }
                            });

                        } else {
                            Toast.makeText(getActivity(), "Error!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    */


    public void getName() {
        DocumentReference documentReference = firestore.collection("Users").document(UserID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ownername = String.valueOf(documentSnapshot.getString("name"));
                ownerphone = String.valueOf(documentSnapshot.getString("phone"));
                FAB = findViewById(R.id.fab);
                FAB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(myDog.this,AddDogActivity.class);
                        i.putExtra("Owner Name", ownername);
                        i.putExtra("Owner Phone", ownerphone);
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
