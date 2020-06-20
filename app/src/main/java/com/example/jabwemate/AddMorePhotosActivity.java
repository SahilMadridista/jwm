/*
 * Copyright (c) 2020 JabWeMet(shubham & sahil). All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jabwemate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jabwemate.HomePadeAdapter.GridAdaptar;
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

   private   Button AddImageButton, SaveButton;
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
   private static final int READ_PERMISSION_REQUEST_CODE = 101;
   private static final int WRITE_PERMISSION_REQUEST_CODE = 102;
   private ArrayList<String> ImageList = new ArrayList<String>();
   private ArrayList<String> RemoveList = new ArrayList<String>();
   private ArrayList<String> ImageList2 ;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_add_more_photos);



      firebaseAuth = FirebaseAuth.getInstance();
      firestore = FirebaseFirestore.getInstance();
      reference = FirebaseStorage.getInstance().getReference("dogimages/");

      progressDialog = new ProgressDialog(this);
      progressDialog.setMessage("Adding image...");
      progressDialog.setCancelable(false);

      DogName = getIntent().getExtras().getString("dogname");
      ImageList2= getIntent().getStringArrayListExtra("URL List");

      if(ImageList2!=null)
         ImageList=ImageList2;

      UserID = firebaseAuth.getCurrentUser().getUid();
      firebaseStorage = FirebaseStorage.getInstance();
      AddImageButton=findViewById(R.id.add_image_btn);
      SaveButton=findViewById(R.id.save_image_button);

       if(ImageList.contains("")){
           ImageList.clear();
       }

      if(ImageList!=null && !ImageList.contains("")) {
         final GridAdaptar gridAdaptar = new GridAdaptar(this, ImageList);
         GridView gridView=findViewById(R.id.add_image_gridview);
         gridView.setAdapter(gridAdaptar);
         gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
               AlertDialog.Builder builder = new AlertDialog.Builder(AddMorePhotosActivity.this);

               builder.setMessage("Do you really want to delete this image ?").setCancelable(false)
                       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(final DialogInterface dialogInterface, int i) {

                             RemoveList.add(ImageList.get(position));
                             ImageList.remove(position);
                             dialogInterface.cancel();
                             gridAdaptar.notifyDataSetChanged();
                          }
                       }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                     dialogInterface.cancel();
                  }
               });

               AlertDialog alert = builder.create();
               alert.show();

               return false;
            }
         });
      }

      AddImageButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            time = System.currentTimeMillis();
            checkReadPermission();
         }
      });
      SaveButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent=new Intent();
            intent.putExtra("URL list", ImageList);
            intent.putExtra("Remove List",RemoveList);
            setResult(RESULT_OK, intent);
            finish();
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
            //  AddImage.setImageURI(Uri.fromFile(f));
            Log.d("tag", "Asolute Url of Image is " + Uri.fromFile(f));

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
            imageUri = contentUri;
            uploadImage();
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
                       setUrl();
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
                    dataUpdate(url);
                 }
              });

   }

   private void dataUpdate(final String url) {
      ImageList.add(url);
      final GridAdaptar gridAdaptar = new GridAdaptar(this, ImageList);
      GridView gridView=findViewById(R.id.add_image_gridview);
      gridView.setAdapter(gridAdaptar);
      gridAdaptar.notifyDataSetChanged();
      gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
         @Override
         public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddMorePhotosActivity.this);

            builder.setMessage("Do you really want to delete this image ?").setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(final DialogInterface dialogInterface, int i) {
                          RemoveList.add(ImageList.get(position));
                          ImageList.remove(position);
                          dialogInterface.cancel();
                          gridAdaptar.notifyDataSetChanged();
                       }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                  dialogInterface.cancel();
               }
            });

            AlertDialog alert = builder.create();
            alert.show();

            return false;
         }
      });


   }

}