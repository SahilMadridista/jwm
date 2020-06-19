package com.example.jabwemate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EditDogDetails extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner AgeSpinner;
    private ImageView DogImage;
    private EditText DogName, DogBreed;
    private Button AddPhoto, Save,AddMoreButton;
    private StorageReference reference;
    private Uri imageUri = null;
    private FirebaseFirestore dogs_db = FirebaseFirestore.getInstance();
    private FirebaseStorage firebaseStorage;
    private static final int CAMERA_PERM_CODE = 100;
    private static final int CAMERA_REQUEST = 1888;
    private static final int READ_PERMISSION_REQUEST_CODE = 101;
    private static final int WRITE_PERMISSION_REQUEST_CODE = 102;
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 1010;
    private String[] age = {"Age", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"};
    String Age = "", Url;
    private RadioButton gendermale, genderfemale;
    private boolean male, female;
    String name, gender, breed, url, aage, UserID,ID;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private boolean flag = false;
    String currentPhotoPath;
    private ArrayList<String> ImageList;
    String ImageLit;


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
        ImageLit=i.getStringExtra("URL list");
        ImageLit=ImageLit.substring(1, ImageLit.length()-1);
        System.out.println("Image is........................"+ImageLit);
        ImageList = new ArrayList<String>(Arrays.asList(ImageLit.split(", ")));
        System.out.println("Image list is........................"+ImageList);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading data...");

        setDefault();

        AddMoreButton = findViewById(R.id.edit_dog_add_more_image_button);
        AddMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!DogName.getText().toString().trim().isEmpty()){
                    Intent i=new Intent(EditDogDetails.this,AddMorePhotosActivity.class);
                    i.putExtra("dogname",DogName.getText().toString().trim());
                    i.putExtra("URL List",ImageList);
                    startActivity(i);
                }
                else{
                    DogName.setError("Required!!");
                    Toast.makeText(EditDogDetails.this, "Please fill dog name", Toast.LENGTH_SHORT).show();
                }
            }
        });



        AddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = true;
                // Intent takePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                checkReadPermission();
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

    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else {
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
        dogs.put("URL List",ImageList);

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
       /* if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                DogImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                DogImage.setImageURI(Uri.fromFile(f));
                Log.d("tag", "Asolute Url of Image is " + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri =  Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
                imageUri=contentUri;
            }
        }
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Get String data from Intent
                ImageList= data.getStringArrayListExtra("URL list");


            }
        }
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

}