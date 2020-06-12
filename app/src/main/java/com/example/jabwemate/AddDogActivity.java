package com.example.jabwemate;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class AddDogActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner AgeSpinner;
    private ImageView DogImage;
    private EditText DogName, DogBreed, DogGender;
    private Button AddPhoto, AddDetails;
    private androidx.appcompat.widget.Toolbar toolbar;
    private static final int CAMERA_REQUEST = 1888;
    private static final int CAMERA_PERM_CODE = 100;
    private String UserID;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private String ownername, ownerphone;
    private StorageReference reference;
    private Uri imageUri = null;
    private FirebaseFirestore dogs_db = FirebaseFirestore.getInstance();
    private String[] age = {"Age", "1", "2", "3", "4", "5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20"};
String Age="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dog);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        reference = FirebaseStorage.getInstance().getReference("dogimages/");

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

        UserID = firebaseAuth.getCurrentUser().getUid();

        ArrayAdapter<String> adapter= new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,age);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AgeSpinner.setAdapter(adapter);
        AgeSpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        AddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        });

        AddDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DogName.getText().toString().trim().isEmpty()) {
                    DogName.setError("Required!!");
                    Toast.makeText(AddDogActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (DogGender.getText().toString().trim().isEmpty()) {
                    DogGender.setError("Required!!");
                    Toast.makeText(AddDogActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }else if (DogBreed.getText().toString().trim().isEmpty()) {
                    DogBreed.setError("Required!!");
                    Toast.makeText(AddDogActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
                else if (Age.isEmpty() || Age.compareTo("Age") == 0) {
                    Toast.makeText(AddDogActivity.this, "Please select age", Toast.LENGTH_SHORT).show();
                }else
                addDogDetails();

            }
        });

    }



    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                Age = age[position];
        }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(AddDogActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                DogImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addDogDetails() {
        if (imageUri != null) {

            StorageReference fileref = reference.child(UserID).child(DogName.getText().toString().trim()).child("image");

            fileref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            dataUpdate();
                            Toast.makeText(AddDogActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(AddDogActivity.this,HomePage.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(AddDogActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                }
            });

        }
        else
            Toast.makeText(getApplicationContext(), "Select Image", Toast.LENGTH_SHORT).show();



    }

    public void dataUpdate() {

        String dogName = DogName.getText().toString().trim();
        String dogBreed = DogBreed.getText().toString().trim();
        String dogGender = DogGender.getText().toString().trim();

        getOwner();
        Map<String, Object> dogs = new LinkedHashMap<>();

        dogs.put("Name", dogName);
        dogs.put("Breed", dogBreed);
        dogs.put("Gender", dogGender);
        dogs.put("Age",Age);
        dogs.put("name",ownername);
        dogs.put("phone",ownerphone);
        dogs.put("UID",UserID);

        dogs_db.collection("Dogs").document().set(dogs)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddDogActivity.this, "Dog Profile Created", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddDogActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getOwner() {
        DocumentReference documentReference = firestore.collection("Users").document(UserID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot documentSnapshot = task.getResult();
                assert documentSnapshot != null;

                ownername = String.valueOf(documentSnapshot.getString("name"));
                ownerphone = String.valueOf(documentSnapshot.getString("phone"));

            }
        });

    }

}
