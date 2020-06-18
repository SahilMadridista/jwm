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
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddMorePhotosActivity extends AppCompatActivity {

   Button AddImageButton,UploadImageButton;
   ImageView AddImage;
   String currentPhotoPath;
   private FirebaseAuth firebaseAuth;
   private FirebaseFirestore firestore;
   private StorageReference reference;
   private FirebaseStorage firebaseStorage;
   private static final int CAMERA_REQUEST = 1888;
   private static final int CAMERA_PERM_CODE = 100;
   private Uri imageUri = null;
   ProgressDialog progressDialog;
   String UserID;
   String url;
   String DogName;
   long time;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
              WindowManager.LayoutParams.FLAG_FULLSCREEN);
      setContentView(R.layout.activity_add_more_photos);

      time = System.currentTimeMillis();

      firebaseAuth = FirebaseAuth.getInstance();
      firestore = FirebaseFirestore.getInstance();
      reference = FirebaseStorage.getInstance().getReference("dogimages/");

      progressDialog = new ProgressDialog(this);
      progressDialog.setMessage("Adding image...");
      progressDialog.setCancelable(false);

      DogName = getIntent().getExtras().getString("dogname");
      Toast.makeText(getApplicationContext(),DogName,Toast.LENGTH_LONG).show();

      UserID = firebaseAuth.getCurrentUser().getUid();

      AddImageButton = findViewById(R.id.add_more_button);
      UploadImageButton = findViewById(R.id.add_more_upload_button);
      AddImage = findViewById(R.id.add_more_image);

      AddImageButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            dispatchTakePictureIntent();
         }
      });

      UploadImageButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            uploadImage();
         }
      });

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


   @Override
   protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      /*  if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
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
            AddImage.setImageURI(Uri.fromFile(f));
            Log.d("tag", "Asolute Url of Image is " + Uri.fromFile(f));

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
            imageUri = contentUri;
         }
      }
   }

   private void uploadImage() {


      if (imageUri != null) {

         progressDialog.show();
         StorageReference fileref = reference.child(UserID).child(DogName).child("more images")
                 .child(String.valueOf(time));

         fileref.putFile(imageUri)
                 .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                       Toast.makeText(AddMorePhotosActivity.this, "Upload successful", Toast.LENGTH_LONG).show();

                       progressDialog.dismiss();

                    }
                 }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

               Toast.makeText(AddMorePhotosActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
               progressDialog.dismiss();

            }
         });

      } else {
         Toast.makeText(getApplicationContext(), "Select Image", Toast.LENGTH_SHORT).show();
      }

   }

   public void setUrl() {
      StorageReference storageReference = firebaseStorage.getReference();
      storageReference.child("dogimages/").child(UserID).child(DogName).child("more images")
              .child(String.valueOf(time)).getDownloadUrl()
              .addOnSuccessListener(new OnSuccessListener<Uri>() {

                 @Override
                 public void onSuccess(Uri uri) {
                    url = uri.toString();
                    //dataUpdate(url);

                 }
              });
   }

   private void dataUpdate(String url) {

      firestore.collection("Dogs").document().update("URL list", FieldValue.arrayUnion(url))
              .addOnSuccessListener(new OnSuccessListener<Void>() {
                 @Override
                 public void onSuccess(Void aVoid) {

                    Toast.makeText(getApplicationContext(),"Photo uploaded and URL stored",Toast.LENGTH_LONG).show();

                 }
              });

   }


}
