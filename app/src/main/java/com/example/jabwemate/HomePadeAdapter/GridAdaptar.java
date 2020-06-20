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

package com.example.jabwemate.HomePadeAdapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.jabwemate.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class GridAdaptar extends ArrayAdapter<String> {
    Context context;
    ArrayList<String> arrayList;
    LayoutInflater inflter;

    public GridAdaptar(@NonNull Context context, @NonNull ArrayList<String> objects) {
        super(context, 0, objects);
        this.context=context;
        arrayList=objects;
        inflter = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }
    @Override
    public String getItem(int i) {
        return null;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       /* View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.gridcard, parent, false);
        }
*/
        convertView = inflter.inflate(R.layout.gridcard, null); // inflate the layout
        ImageView icon = (ImageView) convertView.findViewById(R.id.grid_card_image); // get the reference of ImageView
        if(!arrayList.get(position).isEmpty()) {
            Picasso
                    .get()
                    .load(Uri.parse(arrayList.get(position)))
                    .fit()
                    .centerCrop()
                    .into(icon);

        }

        return convertView;
    }

}