package com.example.jabwemate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.example.jabwemate.consts.SharedPrefConsts;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import maes.tech.intentanim.CustomIntent;

public class HomePage extends AppCompatActivity {

    androidx.appcompat.widget.Toolbar toolbar;
    FloatingActionButton FAB;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        firebaseAuth = FirebaseAuth.getInstance();

        FAB = findViewById(R.id.fab);
        toolbar = findViewById(R.id.home_page_toolbar);
        setSupportActionBar(toolbar);

        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePage.this,AddDogActivity.class));
                CustomIntent.customType(HomePage.this,"bottom-to-up");
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.your_info:

                // Add code here
                Toast.makeText(getApplicationContext(), "Your information Clicked", Toast.LENGTH_LONG).show();
                break;


            case R.id.your_pet_info:

                // Add Code here
                Toast.makeText(getApplicationContext(), "Your pet information Clicked", Toast.LENGTH_LONG).show();
                break;

            case R.id.search_breeds:

                Toast.makeText(getApplicationContext(), "Search breeds Clicked", Toast.LENGTH_LONG).show();
                break;


            case R.id.sign_out:

                SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("login", SharedPrefConsts.NO_LOGIN);
                editor.apply();
                finish();

                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class));
                finish();


            default:
                return super.onOptionsItemSelected(item);

        }

        return true;

    }
}
