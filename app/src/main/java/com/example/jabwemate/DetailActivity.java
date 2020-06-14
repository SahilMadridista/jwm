package com.example.jabwemate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.jabwemate.consts.SharedPrefConsts;

public class DetailActivity extends AppCompatActivity {

   androidx.appcompat.widget.Toolbar toolbar;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_detail);

      toolbar = findViewById(R.id.descritpion_toolbar);
      setSupportActionBar(toolbar);

   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.edit_menu,menu);
      return super.onCreateOptionsMenu(menu);
   }

   @Override
   public boolean onOptionsItemSelected(@NonNull MenuItem item) {

      switch (item.getItemId()) {

         case R.id.your_info:
            // Ad code for edit the details

         default:
            return super.onOptionsItemSelected(item);

      }

   }





}
