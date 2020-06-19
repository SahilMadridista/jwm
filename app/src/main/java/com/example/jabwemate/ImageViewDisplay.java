package com.example.jabwemate;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageViewDisplay extends AppCompatActivity {

    private ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view_display);

        String Image=getIntent().getStringExtra("image");

        image=(ImageView) findViewById(R.id.selectedImage_display);

        Picasso
                .get()
                .load(Uri.parse(Image))
                .into(image);

    }
}
