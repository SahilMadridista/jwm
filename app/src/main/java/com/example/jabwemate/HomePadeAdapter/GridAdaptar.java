package com.example.jabwemate.HomePadeAdapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.jabwemate.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class GridAdaptar extends ArrayAdapter<String> {
    Context context;
    ArrayList<String> arrayList;

    public GridAdaptar(@NonNull Context context, @NonNull ArrayList<String> objects) {
        super(context, 0, objects);
        this.context=context;
        arrayList=objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.gridcard, parent, false);
        }



        ImageView dogImageView = (ImageView) listItemView.findViewById(R.id.grid_card_image);

            Picasso
                    .get()
                    .load(Uri.parse(arrayList.get(position)))
                    .fit()
                    .centerCrop()
                    .into(dogImageView);



        return listItemView;
    }

}
