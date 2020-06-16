package com.example.jabwemate;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MyShortlistsActivity extends AppCompatActivity {

    androidx.appcompat.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_shortlist);

        toolbar = findViewById(R.id.my_shortlist_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
