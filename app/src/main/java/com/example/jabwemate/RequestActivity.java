package com.example.jabwemate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class RequestActivity extends AppCompatActivity {

   androidx.appcompat.widget.Toolbar toolbar;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_request);

      toolbar = findViewById(R.id.requests_toolbar);
      setSupportActionBar(toolbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);

   }

}
