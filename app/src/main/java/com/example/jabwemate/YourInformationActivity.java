package com.example.jabwemate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Iterator;

public class YourInformationActivity extends AppCompatActivity {

  TextView ChangePasswordText;
  androidx.appcompat.widget.Toolbar Toolbar;
  TextView NameText,EmailText,PhoneText;
  String UserID;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_your_information);

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    Toolbar = findViewById(R.id.ProfileToolbar);
    setSupportActionBar(Toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowTitleEnabled(false);

    NameText = findViewById(R.id.name_text);
    EmailText = findViewById(R.id.email_text);
    PhoneText = findViewById(R.id.phone_text);

    NameText.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        //startActivity(new Intent(UserProfile.this,EditUserName.class));
      }
    });

    ChangePasswordText = findViewById(R.id.ChangePassTextview);
    ChangePasswordText.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(new Intent(YourInformationActivity.this, ChangePasswordActivity.class));
      }
    });

    PhoneText.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        //startActivity(new Intent(UserProfile.this,EditPhoneNumber.class));
      }
    });

    assert firebaseAuth.getCurrentUser()!=null;

    UserID = firebaseAuth.getCurrentUser().getUid();

    DocumentReference documentReference =firestore.collection("Users").document(UserID);

    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
      @Override
      public void onComplete(@NonNull Task<DocumentSnapshot> task) {

        if(task.isSuccessful()){
          DocumentSnapshot documentSnapshot = task.getResult();
          assert documentSnapshot != null;
          NameText.setText(documentSnapshot.getString("name"));
          EmailText.setText(documentSnapshot.getString("email"));
          PhoneText.setText(documentSnapshot.getString("phone"));

        }

        else {
          Toast.makeText(getApplicationContext(),"Can't load data. Some error occurred.",Toast.LENGTH_LONG)
                  .show();
        }
      }
    });

  }

}
