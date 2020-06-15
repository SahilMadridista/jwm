package com.example.jabwemate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class EditDogDetails extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner AgeSpinner;
    private ImageView DogImage;
    private EditText DogName, DogBreed;
    private Button AddPhoto, Save;
    private StorageReference reference;
    private Uri imageUri = null;
    private FirebaseFirestore dogs_db = FirebaseFirestore.getInstance();
    private FirebaseStorage firebaseStorage;
    private static final int CAMERA_REQUEST = 1888;
    private String[] age = {"Age", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"};
    String Age = "", Url;
    private RadioButton gendermale, genderfemale;
    private boolean male, female;
    String name, gender, breed, url, aage, UserID,ID;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private boolean flag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_dog_details);
        firebaseAuth = FirebaseAuth.getInstance();
        UserID = firebaseAuth.getCurrentUser().getUid();
        firebaseStorage = FirebaseStorage.getInstance();
        reference = FirebaseStorage.getInstance().getReference("dogimages/");
        setId();
        Intent i = getIntent();
        name = i.getStringExtra("Name");
        aage = i.getStringExtra("Age");
        breed = i.getStringExtra("Breed");
        gender = i.getStringExtra("Gender");
        url = i.getStringExtra("Url");
        ID=i.getStringExtra("REG");

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading data...");

        setDefault();
        AddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = true;
                Intent takePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DogName.getText().toString().trim().isEmpty()) {
                    DogName.setError("Required!!");
                    Toast.makeText(EditDogDetails.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (genderCheck().isEmpty()) {
                    Toast.makeText(EditDogDetails.this, "Please select Gender", Toast.LENGTH_SHORT).show();
                } else if (DogBreed.getText().toString().trim().isEmpty()) {
                    DogBreed.setError("Required!!");
                    Toast.makeText(EditDogDetails.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (Age.isEmpty() || Age.compareTo("Age") == 0) {
                    Toast.makeText(EditDogDetails.this, "Please select age", Toast.LENGTH_SHORT).show();
                } else
                    addDogDetails();

            }
        });


    }

    public String genderCheck() {
        String g = "";
        male = gendermale.isChecked();
        female = genderfemale.isChecked();
        if (male)
            g = "Male";
        if (female)
            g = "Female";

        return g;
    }

    private void setDefault() {
        DogName.setText(name);
        DogBreed.setText(breed);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, age);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AgeSpinner.setAdapter(adapter);
        AgeSpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        int i;
        for (i = 0; i < age.length; i++) {
            if (age[i].equals(aage))
                break;
        }
        AgeSpinner.setSelection(i);
        if (gender.equals("Male")) {
            gendermale.setChecked(true);
        } else {
            genderfemale.setChecked(true);
        }
        if (url != null) {
            Picasso
                    .get()
                    .load(Uri.parse(url))
                    .fit()
                    .centerCrop()
                    .into(DogImage);
        }
    }

    private void setId() {
        AgeSpinner = findViewById(R.id.age_spinner_edit);
        DogImage = findViewById(R.id.dog_image_edit);
        DogName = findViewById(R.id.dog_name);
        DogBreed = findViewById(R.id.dog_breed);
        gendermale = findViewById(R.id.su_male_edit);
        genderfemale = findViewById(R.id.su_female_edit);
        AddPhoto = findViewById(R.id.add_photo_button_edit);
        Save = findViewById(R.id.save_button);
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        Age = age[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(EditDogDetails.this, "Please fill all fields", Toast.LENGTH_SHORT).show();

    }


    private void addDogDetails() {
        if (flag == true) {

            if (imageUri != null) {

               progressDialog.show();
                StorageReference fileref = reference.child(UserID).child(DogName.getText().toString().trim()).child("image");

                fileref.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                setUrl();
                                Toast.makeText(EditDogDetails.this, "Upload successful", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(EditDogDetails.this, "error#", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();

                    }
                });

            } else
                Toast.makeText(getApplicationContext(), "Select Image", Toast.LENGTH_SHORT).show();

        } else {
            progressDialog.show();
            dataUpdate(url);
        }

    }

    public void dataUpdate(String URL) {

        String dogName = DogName.getText().toString().trim();
        String dogBreed = DogBreed.getText().toString().trim();
        String dogGender = genderCheck();

        Map<String, Object> dogs = new LinkedHashMap<>();

        dogs.put("Name", dogName);
        dogs.put("Breed", dogBreed);
        dogs.put("Gender", dogGender);
        dogs.put("Age", Age);
        dogs.put("URL", URL);

        dogs_db.collection("Dogs").document(ID).update(dogs)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                        progressDialog.dismiss();
                        Toast.makeText(EditDogDetails.this, "Dog details Updated", Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(EditDogDetails.this, MyDogDetailActivity.class);
                        i.putExtra("REF",ID);
                        startActivity(i);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       progressDialog.dismiss();
                        Toast.makeText(EditDogDetails.this, "Error!#!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void setUrl() {
        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child("dogimages/").child(UserID).child(DogName.getText().toString().trim()).child("image").getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {

                    @Override
                    public void onSuccess(Uri uri) {
                        Url = uri.toString();
                        dataUpdate(Url);

                    }
                });
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


}
