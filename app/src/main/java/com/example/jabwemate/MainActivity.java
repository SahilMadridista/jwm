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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jabwemate.consts.SharedPrefConsts;

public class MainActivity extends AppCompatActivity {

    RelativeLayout one,two;
    TextView head,desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        one = findViewById(R.id.relativeLayoutone);
        two = findViewById(R.id.relativeLayouttwo);
        head = findViewById(R.id.welcomeText);
        desc = findViewById(R.id.descText);

        one.animate().alpha((float)0.5).setDuration(1000);
        two.animate().alpha((float)0.5).setDuration(1000);

        head.animate().alpha(1).setDuration(1000);
        desc.animate().alpha(1).setDuration(1000);


        SharedPreferences preferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        final int loginStatus = preferences.getInt("login", SharedPrefConsts.NO_LOGIN);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(loginStatus == SharedPrefConsts.USER_LOGIN){
                    startActivity(new Intent(MainActivity.this,HomePage.class));
                    finish();
                }else {
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    finish();
                }

            }
        }, 2500);

    }
}
