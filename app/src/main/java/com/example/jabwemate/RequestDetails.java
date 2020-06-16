package com.example.jabwemate;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class RequestDetails extends AppCompatActivity {

   TextView Name,Age,Breed,Gender;
   ImageView Image;
   String ID,URL;
   ProgressDialog progressDialog;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_request_details);

      Name = findViewById(R.id.req_dog_name);
      Age = findViewById(R.id.req_dog_age);
      Breed = findViewById(R.id.req_dog_breed);
      Gender = findViewById(R.id.req_dog_gender);
      Image = findViewById(R.id.req_details_dog_image);

      progressDialog = new ProgressDialog(this);
      progressDialog.setTitle("Loading");
      progressDialog.setMessage("Loading image may time some extra time...");
      progressDialog.setCancelable(false);

      Intent i = getIntent();
      ID = i.getStringExtra("REF");

      setUpView();

   }

   private void setUpView() {

      progressDialog.show();

      Runnable progressRunnable = new Runnable() {

         @Override
         public void run() {
            progressDialog.cancel();
         }
      };

      Handler pdCanceller = new Handler();
      pdCanceller.postDelayed(progressRunnable, 1500);

      FirebaseFirestore firestore = FirebaseFirestore.getInstance();
      firestore.collection("Dogs").document(ID)
              .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
         @Override
         public void onSuccess(DocumentSnapshot documentSnapshot) {

            Name.setText(documentSnapshot.getString("Name"));
            Age.setText(documentSnapshot.getString("Age"));
            Breed.setText(documentSnapshot.getString("Breed"));
            Gender.setText(documentSnapshot.getString("Gender"));
            URL = String.valueOf(documentSnapshot.getString("URL"));

            if (URL != null) {
               Picasso
                       .get()
                       .load(Uri.parse(URL))
                       .fit()
                       .centerCrop()
                       .into(Image);
            }

         }
      });



   }
}
