package com.example.jabwemate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;

import com.example.jabwemate.HomePadeAdapter.GridAdaptar;

import java.util.ArrayList;
import java.util.Arrays;

public class SeeAllPhotosActivity extends AppCompatActivity {

   private ArrayList<String> ImageList;
   private String ImageLit;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
              WindowManager.LayoutParams.FLAG_FULLSCREEN);
      setContentView(R.layout.activity_see_all_photos);

      Intent i = getIntent();
      ImageLit=i.getStringExtra("URL list");
      ImageLit=ImageLit.substring(1, ImageLit.length()-1);
      ImageList = new ArrayList<String>(Arrays.asList(ImageLit.split(",")));

      if(ImageList!=null) {
         GridAdaptar gridAdaptar = new GridAdaptar(this, ImageList);
         GridView gridView=findViewById(R.id.add_image_gridview);
         gridView.setAdapter(gridAdaptar);
      }
   }

}
