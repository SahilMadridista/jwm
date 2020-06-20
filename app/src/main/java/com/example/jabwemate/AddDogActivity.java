package com.example.jabwemate;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class AddDogActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner AgeSpinner;
    private ImageView DogImage;
    private EditText DogName, DogBreed;
    private Button AddPhoto, AddDetails,AddMorePhotos;
    private androidx.appcompat.widget.Toolbar toolbar;
    private static final int CAMERA_REQUEST = 1888;
    private static final int CAMERA_PERM_CODE = 100;
    private static final int READ_PERMISSION_REQUEST_CODE = 101;
    private static final int WRITE_PERMISSION_REQUEST_CODE = 102;
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 1010;
    private String UserID;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private String ownername, ownerphone, city;
    private StorageReference reference;
    private Uri imageUri = null;
    private FirebaseFirestore dogs_db = FirebaseFirestore.getInstance();
    private FirebaseStorage firebaseStorage;
    private String[] age = {"Age", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"};
    String Age = "", url;
    private RadioButton gendermale, genderfemale;
    private boolean male, female;
    private ProgressDialog progressDialog;
    String currentPhotoPath;
    private ArrayList<String> ImageList = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dog);

        firestore = FirebaseFirestore.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();
        reference = FirebaseStorage.getInstance().getReference("dogimages/");

        AgeSpinner = findViewById(R.id.age_spinner);
        DogImage = findViewById(R.id.dog_image);
        DogName = findViewById(R.id.dog_name_edit_text);
        DogBreed = findViewById(R.id.dog_breed_edit_text);
        gendermale = findViewById(R.id.su_male);
        genderfemale = findViewById(R.id.su_female);
        AddPhoto = findViewById(R.id.add_photo_button);
        AddDetails = findViewById(R.id.add_details_button);
        AddMorePhotos = findViewById(R.id.add_more_image_button);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading data...");



        toolbar = findViewById(R.id.add_dog_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        UserID = firebaseAuth.getCurrentUser().getUid();
        firebaseStorage = FirebaseStorage.getInstance();

        firestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ownername = documentSnapshot.getString("username");
                ownerphone = documentSnapshot.getString("phone");
                city = documentSnapshot.getString("city");


            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, age);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AgeSpinner.setAdapter(adapter);
        AgeSpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        AddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent takePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                checkReadPermission();
            }
        });

        AddDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DogName.getText().toString().trim().isEmpty()) {
                    DogName.setError("Required!!");
                    Toast.makeText(AddDogActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (genderCheck().isEmpty()) {
                    Toast.makeText(AddDogActivity.this, "Please select Gender", Toast.LENGTH_SHORT).show();
                } else if (DogBreed.getText().toString().trim().isEmpty()) {
                    DogBreed.setError("Required!!");
                    Toast.makeText(AddDogActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (Age.isEmpty() || Age.compareTo("Age") == 0) {
                    Toast.makeText(AddDogActivity.this, "Please select age", Toast.LENGTH_SHORT).show();
                } else
                    addDogDetails();

            }
        });
        AddMorePhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!DogName.getText().toString().trim().isEmpty()){
                    Intent i=new Intent(AddDogActivity.this,AddMorePhotosActivity.class);
                    i.putExtra("dogname",DogName.getText().toString().trim());
                    i.putExtra("URL List",ImageList);
                    startActivityForResult(i,SECOND_ACTIVITY_REQUEST_CODE);
                }
                else{
                    DogName.setError("Required!!");
                    Toast.makeText(AddDogActivity.this, "Please fill dog name", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.jab_we_met",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        // File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void checkReadPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_REQUEST_CODE);
        } else {
            checkWritePermission();
        }
    }

    private void checkWritePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST_CODE);
        } else {
            askCameraPermissions();
        }
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


    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        Age = age[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(AddDogActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == READ_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "STORAGE permission is Required", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == WRITE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Storage permission is Required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                DogImage.setImageURI(Uri.fromFile(f));
                Log.d("tag", "Asolute Url of Image is " + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
                imageUri = contentUri;
            }
        }
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Get String data from Intent
                ImageList= data.getStringArrayListExtra("URL list");


            }
        }
    }

    private void addDogDetails() {

        if (imageUri != null) {

            progressDialog.show();
            StorageReference fileref = reference.child(UserID).child(DogName.getText().toString().trim()).child("image");

            fileref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            setUrl();
                            //Toast.makeText(AddDogActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(AddDogActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();

                }
            });

        } else
            Toast.makeText(getApplicationContext(), "Select Image", Toast.LENGTH_SHORT).show();


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
        dogs.put("name", ownername);
        dogs.put("phone", ownerphone);
        dogs.put("UID", UserID);
        dogs.put("URL", URL);
        dogs.put("city", city);
        dogs.put("URL List",ImageList);


        dogs_db.collection("Dogs").document().set(dogs)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                        progressDialog.dismiss();
                        Toast.makeText(AddDogActivity.this, "Dog details added", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddDogActivity.this, myDog.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddDogActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void setUrl() {
        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child("dogimages/").child(UserID).child(DogName.getText().toString().trim()).child("image").getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {

                    @Override
                    public void onSuccess(Uri uri) {
                        url = uri.toString();
                        dataUpdate(url);

                    }
                });
    }

}