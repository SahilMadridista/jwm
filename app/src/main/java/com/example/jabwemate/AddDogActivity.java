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
import android.view.View;
import android.webkit.MimeTypeMap;
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

public class AddDogActivity extends AppCompatActivity {

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
    String currentPhotoPath,path;


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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ageArray,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AgeSpinner.setAdapter(adapter);

        AddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
               Uri uri  = Uri.parse("/storage/emulated/0/Pictures/s"+timeStamp+".jpg");
                path=uri.toString();
               // takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        });

        AddDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addDogDetails();

            }
        });

    }
    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else {
            dispatchTakePictureIntent();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERM_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            }else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.setType("image/*");
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
*/
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            System.out.println("Phto file is............." + photoFile);
            System.out.println("current photo path is............." + currentPhotoPath);
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

  /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST){
            if(resultCode == Activity.RESULT_OK){
                File f = new File(currentPhotoPath);
                DogImage.setImageURI(Uri.fromFile(f));
                Log.d("tag", "Absolute Url of Image is " + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
                this.imageUri=contentUri;

               // uploadImageToFirebase(f.getName(),contentUri);



            }

        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("result ....................."+resultCode+"............"+RESULT_OK);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK ) {

            File file = new File(path);
            imageUri= Uri.fromFile(file);
            //imageUri = data.getData();
            System.out.println("Uri is......................"+imageUri);
            DogImage.setImageURI(imageUri);
            /*StorageReference fileref = reference.child(UserID).child("image");

            fileref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            //dataUpdate();
                            Toast.makeText(AddDogActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(AddDogActivity.this,HomePage.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(AddDogActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                }
            });*/

        }
    }

  private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  //prefix
                ".jpg",         //suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void addDogDetails() {
        if (imageUri != null) {

            StorageReference fileref = reference.child(UserID).child("image");

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
        dogs.put("name",ownername);
        dogs.put("phone",ownerphone);

        dogs_db.collection("Dogs").document().set(dogs)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddDogActivity.this, "Job Created", Toast.LENGTH_SHORT).show();
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
