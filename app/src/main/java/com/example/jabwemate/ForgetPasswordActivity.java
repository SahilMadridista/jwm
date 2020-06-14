package com.example.jabwemate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

   private FirebaseAuth firebaseAuth;
   ProgressDialog progressDialog;
   EditText Email;
   Button SendLink;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
              WindowManager.LayoutParams.FLAG_FULLSCREEN);
      setContentView(R.layout.activity_forget_password);

      firebaseAuth = FirebaseAuth.getInstance();

      Email = findViewById(R.id.forget_pass_et);
      SendLink = findViewById(R.id.forget_pass_button);

      progressDialog = new ProgressDialog(this);
      progressDialog.setCancelable(false);
      progressDialog.setMessage("Sending password link...");

      SendLink.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            sendEmail();
         }
      });


   }

   private void sendEmail() {

      String email = Email.getText().toString().trim();

      if(email.isEmpty()){
         Email.setError("Email required");
         Email.requestFocus();
         return;
      }

      if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
         Email.setError("Enter a valid email address");
         Email.requestFocus();
         return;
      }

      progressDialog.show();

      firebaseAuth.sendPasswordResetEmail(email)
              .addOnCompleteListener(new OnCompleteListener<Void>() {
         @Override
         public void onComplete(@NonNull Task<Void> task) {

            if(task.isSuccessful()){
               progressDialog.dismiss();
               Toast.makeText(getApplicationContext(),"Password reset link has been sent to your email."
               ,Toast.LENGTH_LONG).show();
               startActivity(new Intent(ForgetPasswordActivity.this,LoginActivity.class));
               finish();
            }

            else {
               progressDialog.dismiss();
               Toast.makeText(getApplicationContext(),"Can't send link right now. Some error occurred."
                       ,Toast.LENGTH_LONG).show();
            }

         }
      });

   }
}
