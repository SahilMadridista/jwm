package com.example.jabwemate;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddDogActivity extends AppCompatActivity {

    Spinner AgeSpinner;
    ImageView DogImage;
    EditText DogName,DogBreed,DogGender;
    Button AddPhoto,AddDetails;
    androidx.appcompat.widget.Toolbar toolbar;
    String UserID;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    String ownername,ownerphone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dog);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        //reference = FirebaseStorage.getInstance().getReference("dogimages/");

        AgeSpinner = findViewById(R.id.age_spinner);
        DogImage = findViewById(R.id.dog_image);
        DogName = findViewById(R.id.dog_name_edit_text);
        DogBreed = findViewById(R.id.dog_breed_edit_text);
        DogGender = findViewById(R.id.gender_edittext);
        AddPhoto = findViewById(R.id.add_photo_button);
        AddDetails = findViewById(R.id.add_details_button);

        toolbar = findViewById(R.id.add_dog_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.ageArray,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AgeSpinner.setAdapter(adapter);

        AddPhoto.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                openCamera();

                // This method is for opening camera and setting the image in the imageView

            }
        });

        AddDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addDogDetails();

                // This method stores the Dog data in the firestore DB.
                // We have to store the dog details as mentioned in the Dog.java model class.

            }
        });

        UserID = firebaseAuth.getCurrentUser().getUid();

        final DocumentReference documentReference = firestore.collection("Users").document(UserID);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot documentSnapshot = task.getResult();
                assert documentSnapshot!=null;

                ownername = String.valueOf(documentSnapshot.getString("name"));
                ownerphone = String.valueOf(documentSnapshot.getString("phone"));

            }
        });


    }

    private void openCamera() {


    }

    private void addDogDetails() {



    }

}
