package com.example.jabwemate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.jabwemate.consts.SharedPrefConsts;
import com.example.jabwemate.model.Owner;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import maes.tech.intentanim.CustomIntent;

public class SignUpActivity extends AppCompatActivity {

    TextView LoginText;
    EditText NameEditText, EmailEditText, PhoneEditText, PasswordEditText, CityEditText;
    CheckBox ShowPasswordCheckBox;
    Button SignUpButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private ProgressDialog progressDialog;
    String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        NameEditText = findViewById(R.id.name_edit_text_signup);
        EmailEditText = findViewById(R.id.email_edit_text_signup);
        PhoneEditText = findViewById(R.id.phone_edit_text_signup);
        PasswordEditText = findViewById(R.id.password_edit_text_signup);
        CityEditText = findViewById(R.id.city_edit_text_signup);
        SignUpButton = findViewById(R.id.sign_up_button);
        ShowPasswordCheckBox = findViewById(R.id.checkBox2);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Creating profile...");

        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        ShowPasswordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    PasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    PasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        LoginText = findViewById(R.id.LoginText);
        LoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                CustomIntent.customType(SignUpActivity.this,"fadein-to-fadeout");
                finish();
            }
        });

    }

    private void signUp() {

        final String name = NameEditText.getText().toString().trim();
        final String email = EmailEditText.getText().toString().trim();
        final String phone = PhoneEditText.getText().toString().trim();
        final String password = PasswordEditText.getText().toString().trim();
        final String city = CityEditText.getText().toString().trim();

        if(name.isEmpty()){
            NameEditText.setError("Please enter your name");
            NameEditText.requestFocus();
            return;
        }

        if(email.isEmpty()){
            EmailEditText.setError("Please enter your email");
            EmailEditText.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            EmailEditText.setError("Enter a valid email address");
            EmailEditText.requestFocus();
            return;
        }

        if(phone.isEmpty()){
            PhoneEditText.setError("Please enter phone number");
            PhoneEditText.requestFocus();
            return;
        }

        if(phone.length()!=10){
            PhoneEditText.setError("Invalid phone number");
            PhoneEditText.requestFocus();
            return;
        }

        if(password.isEmpty()){
            PasswordEditText.setError("Please enter password");
            PasswordEditText.requestFocus();
            return;
        }

        if(password.length()<6){
            PasswordEditText.setError("Password must be at least 6 characters long");
            PasswordEditText.requestFocus();
            return;
        }

        if(city.isEmpty()){
            CityEditText.setError("Please enter your city");
            CityEditText.requestFocus();
            return;
        }

        progressDialog.show();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        assert user!=null;

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            firebaseAuth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        UserID = firebaseAuth.getCurrentUser().getUid();
                                        DocumentReference documentReference = firestore.collection("Users")
                                                .document(UserID);

                                        Owner owner = new Owner();

                                        owner.username = name;
                                        owner.email = email;
                                        owner.phone = phone;
                                        owner.password = password;
                                        owner.city = city;

                                        documentReference.set(owner).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                /*SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putInt("login", SharedPrefConsts.USER_LOGIN);
                                                editor.apply();*/

                                                progressDialog.dismiss();
                                                Toast.makeText(SignUpActivity.this,"Registered successfully."+
                                                                "Please check your email to verify your account."+
                                                        "You can login after clicking on verification link.",Toast.LENGTH_LONG).show();

                                                EmailEditText.setText("");
                                                PasswordEditText.setText("");
                                                PhoneEditText.setText("");
                                                CityEditText.setText("");
                                                NameEditText.setText("");

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });



                                    }

                                    else{

                                        Toast.makeText(SignUpActivity.this,"Error occurred: " +
                                                task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                        finish();


                                    }

                                }
                            });



                        }
                        else {

                            Toast.makeText(getApplicationContext(),R.string.error_toast,Toast.LENGTH_LONG)
                                    .show();
                            progressDialog.dismiss();

                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Do you really want to exit ?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        SignUpActivity.super.onBackPressed();
                        finish();

                    }
                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}
