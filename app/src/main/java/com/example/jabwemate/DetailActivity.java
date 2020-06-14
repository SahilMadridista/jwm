package com.example.jabwemate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jabwemate.consts.SharedPrefConsts;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    androidx.appcompat.widget.Toolbar toolbar;
    private Button accept;
    private TextView Owner, Dog, Breed, Age, City, Gender;
    private String owner, dog, breed, age, city, gender, ID, URL;
    private FirebaseFirestore dogs_db = FirebaseFirestore.getInstance();
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        toolbar = findViewById(R.id.descritpion_toolbar);
        setSupportActionBar(toolbar);

        Owner = findViewById(R.id.detail_owner);
        Dog = findViewById(R.id.detail_dog);
        Breed = findViewById(R.id.detail_breed);
        Age = findViewById(R.id.detail_age);
        City = findViewById(R.id.detail_city);
        Gender = findViewById(R.id.detail_gender);
        accept = findViewById(R.id.detail_accept);
        image = findViewById(R.id.desc_dog_image);

        Intent i = getIntent();
        ID = i.getStringExtra("REF");

        setView();

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetailActivity.this, "Accepted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setView() {
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

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.your_info:
                // Ad code for edit the details

            default:
                return super.onOptionsItemSelected(item);

        }

    }


}
