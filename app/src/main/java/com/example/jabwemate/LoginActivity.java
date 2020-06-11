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

import com.example.jabwemate.consts.SharedPrefConsts;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import maes.tech.intentanim.CustomIntent;

public class LoginActivity extends AppCompatActivity {

    TextView RegisterText;
    Button LoginButton;
    EditText EmailEditText,PasswordEditText;
    CheckBox LoginCheckBox;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog LoginProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        LoginProgressDialog = new ProgressDialog(this);
        LoginProgressDialog.setCancelable(false);
        LoginProgressDialog.setMessage("Logging in...");

        EmailEditText = findViewById(R.id.email_sign_in_editText);
        PasswordEditText = findViewById(R.id.password_sign_in_editText);
        LoginCheckBox = findViewById(R.id.login_checkBox);

        RegisterText = findViewById(R.id.registerText);
        LoginButton = findViewById(R.id.login_button);

        LoginCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    PasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    PasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        RegisterText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                CustomIntent.customType(LoginActivity.this,"fadein-to-fadeout");
                finish();
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logInUser();
            }
        });



    }

    private void logInUser() {

        String email = EmailEditText.getText().toString().trim();
        String password = PasswordEditText.getText().toString().trim();

        if(email.isEmpty()){
            EmailEditText.setError("Email required");
            EmailEditText.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            EmailEditText.setError("Enter a valid email address");
            EmailEditText.requestFocus();
            return;
        }

        if(password.isEmpty()){
            PasswordEditText.setError("Please enter password");
            PasswordEditText.requestFocus();
            return;
        }

        if(password.length()<6){
            PasswordEditText.setError("Password must be atleast 6 characters long");
            PasswordEditText.requestFocus();
            return;
        }
        ;
        LoginProgressDialog.show();


        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putInt("login", SharedPrefConsts.USER_LOGIN);
                            editor.apply();

                            startActivity(new Intent(getApplicationContext(),HomePage.class));
                            finish();

                            LoginProgressDialog.dismiss();
                            Toast.makeText(LoginActivity.this,"You are logged in",
                                    Toast.LENGTH_LONG).show();

                        }else {

                            Toast.makeText(LoginActivity.this,R.string.error_toast,
                                    Toast.LENGTH_LONG).show();
                            LoginProgressDialog.dismiss();

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

                        LoginActivity.super.onBackPressed();
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
