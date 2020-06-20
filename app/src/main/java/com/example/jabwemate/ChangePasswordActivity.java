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
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangePasswordActivity extends AppCompatActivity {

   androidx.appcompat.widget.Toolbar toolbar;
   EditText NewPasswordEdittext,ConfirmPasswordEdittext;
   Button ConfirmButton;
   String UserID;
   private FirebaseAuth firebaseAuth;
   private FirebaseFirestore firestore;
   ProgressDialog ChangePasssowrdProgressDialog;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_change_password);

      firebaseAuth = FirebaseAuth.getInstance();
      firestore = FirebaseFirestore.getInstance();

      toolbar = findViewById(R.id.ChangePassToolbar);
      setSupportActionBar(toolbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowTitleEnabled(false);

      ChangePasssowrdProgressDialog  =new ProgressDialog(this);
      ChangePasssowrdProgressDialog.setCancelable(false);
      ChangePasssowrdProgressDialog.setMessage("Changing password...");

      NewPasswordEdittext = findViewById(R.id.new_pass_et);
      ConfirmPasswordEdittext = findViewById(R.id.confirm_new_pass_et);

      UserID = firebaseAuth.getCurrentUser().getUid();

      ConfirmButton = findViewById(R.id.confirm_button);

      ConfirmButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            passwordChange();
         }
      });

   }

   private void passwordChange() {

      final String newpass = NewPasswordEdittext.getText().toString().trim();
      final String confirmnewpass = ConfirmPasswordEdittext.getText().toString().trim();

      FirebaseUser user = firebaseAuth.getCurrentUser();
      assert user!=null;
      UserID = firebaseAuth.getCurrentUser().getUid();


      if(newpass.isEmpty()){
         NewPasswordEdittext.setError("New password can't be empty.");
         NewPasswordEdittext.requestFocus();
         return;
      }

      if(confirmnewpass.isEmpty()){
         ConfirmPasswordEdittext.setError("Enter your new password again.");
         ConfirmPasswordEdittext.requestFocus();
         return;
      }


      if(!newpass.equals(confirmnewpass)){

         NewPasswordEdittext.setError("Password doesn't match");
         NewPasswordEdittext.requestFocus();

         ConfirmPasswordEdittext.setError("Password doesn't match");
         ConfirmPasswordEdittext.requestFocus();
         return;

      }

      if(newpass.length()<6){

         NewPasswordEdittext.setError("New password must be at least 6 characters long");
         NewPasswordEdittext.requestFocus();
         return;

      }

      ChangePasssowrdProgressDialog.show();

      user.updatePassword(newpass).addOnCompleteListener(new OnCompleteListener<Void>() {
         @Override
         public void onComplete(@NonNull Task<Void> task) {

            if(task.isSuccessful()){

               firestore.collection("Users").document(UserID).update("password"
                       ,newpass).addOnSuccessListener(new OnSuccessListener<Void>() {
                  @Override
                  public void onSuccess(Void aVoid) {
                     ChangePasssowrdProgressDialog.dismiss();
                     Toast.makeText(getApplicationContext(),"Password changed",Toast.LENGTH_LONG).show();
                     startActivity(new Intent(getApplicationContext(),YourInformationActivity.class));
                     finish();
                  }
               });

            }

            else {
               Toast.makeText(getApplicationContext(),"Some error occurred. Can't change password right now"
               ,Toast.LENGTH_LONG).show();

               ChangePasssowrdProgressDialog.dismiss();
            }

         }
      });

   }
}
