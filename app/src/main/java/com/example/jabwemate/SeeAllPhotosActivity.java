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

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
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
      ImageList = new ArrayList<String>(Arrays.asList(ImageLit.split(", ")));


      if(ImageList.contains("")){
         ImageList.clear();
      }

      if(ImageList!=null && !ImageList.contains("")) {
         GridAdaptar gridAdaptar = new GridAdaptar(this, ImageList);
         GridView gridView=findViewById(R.id.add_image_gridview);
         gridView.setAdapter(gridAdaptar);
         gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Intent intent = new Intent(SeeAllPhotosActivity.this, ImageViewDisplay.class);
               intent.putExtra("image", ImageList.get(position)); // put image data in Intent
               startActivity(intent);
            }
         });
      }
   }

}