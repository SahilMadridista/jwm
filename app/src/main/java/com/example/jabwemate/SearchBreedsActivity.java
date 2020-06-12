package com.example.jabwemate;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class SearchBreedsActivity extends AppCompatActivity {

    androidx.appcompat.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_breeds);

        toolbar = findViewById(R.id.search_breeds_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
