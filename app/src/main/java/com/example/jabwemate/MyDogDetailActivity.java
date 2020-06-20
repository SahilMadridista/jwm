package com.example.jabwemate;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
public class MyDogDetailActivity extends AppCompatActivity {

   private Button EditDetails,SeeAllPhotos;
   private TextView Owner, Dog, Breed, Age, City, Gender,Email,Phone;
   private String owner, dog, breed, age, city, gender, ID, URL,email,phone,ownerid,URL_list;
   private FirebaseFirestore dogs_db = FirebaseFirestore.getInstance();
   private ImageView image;
   FirebaseFirestore firestore;
   ProgressDialog progressDialog;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_my_dog_detail);

      Owner = findViewById(R.id.detail_owner_name);
      Dog = findViewById(R.id.detail_dog_name);
      Breed = findViewById(R.id.detail_dog_breed);
      Age = findViewById(R.id.detail_dog_age);
      City = findViewById(R.id.detail_owner_city);
      Gender = findViewById(R.id.detail_dog_gender);
      EditDetails = findViewById(R.id.edit_button);
      SeeAllPhotos = findViewById(R.id.see_all_photos);
      image = findViewById(R.id.detail_dog_image);
      Email = findViewById(R.id.detail_owner_email);
      Phone = findViewById(R.id.detail_owner_phone);

      progressDialog = new ProgressDialog(this);
      progressDialog.setMessage("Loading information...");
      progressDialog.setCancelable(false);

      firestore = FirebaseFirestore.getInstance();

      Intent i = getIntent();
      ID = i.getStringExtra("REF");

      /*AddMorePhotosButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Intent intent = new Intent(MyDogDetailActivity.this,AddMorePhotosActivity.class);
            intent.putExtra("dogname",dog);
            startActivity(intent);
         }
      });*/

      /*EditDetails.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Intent i = new Intent(getApplicationContext(),EditDogDetails.class);

            i.putExtra("Name",dog);
            i.putExtra("Breed",breed);
            i.putExtra("Age",age);
            i.putExtra("Gender",gender);
            i.putExtra("Url",URL);
            i.putExtra("REG",ID);
            i.putExtra("URL list",URL_list);
            startActivity(i);
            finish();

         }
      });*/


      setView();
   }

   private void setView() {

      progressDialog.show();

      Runnable progressRunnable = new Runnable() {

         @Override
         public void run() {
            progressDialog.cancel();
         }
      };

      Handler pdCanceller = new Handler();
      pdCanceller.postDelayed(progressRunnable, 2500);

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
            ownerid = String.valueOf(documentSnapshot.getString("UID"));
            URL_list=String.valueOf(documentSnapshot.get("URL List"));

            if (URL != null) {
               Picasso
                       .get()
                       .load(Uri.parse(URL))
                       .fit()
                       .centerCrop()
                       .into(image);
            }

            firestore.collection("Users").document(ownerid).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                       @Override
                       public void onSuccess(DocumentSnapshot documentSnapshot) {

                          Email.setText(documentSnapshot.getString("email"));
                          Phone.setText(documentSnapshot.getString("phone"));

                       }
                    });

            Owner.setText(owner);
            Breed.setText(breed);
            Dog.setText(dog);
            Age.setText(age);
            City.setText(city);
            Gender.setText(gender);

            EditDetails.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                 Intent i=new Intent(MyDogDetailActivity.this,EditDogDetails.class);
                 i.putExtra("Name",dog);
                 i.putExtra("Breed",breed);
                 i.putExtra("Age",age);
                 i.putExtra("Gender",gender);
                 i.putExtra("Url",URL);
                 i.putExtra("REG",ID);
                 i.putExtra("URL list",URL_list);
                 startActivity(i);
                 finish();
               }
            });

             SeeAllPhotos.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     Intent i=new Intent(MyDogDetailActivity.this,SeeAllPhotosActivity.class);
                     i.putExtra("URL list",URL_list);
                     startActivity(i);

                 }
             });

         }
      });

   }

}
