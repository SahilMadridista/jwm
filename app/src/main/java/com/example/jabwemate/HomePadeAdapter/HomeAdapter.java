

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

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jabwemate.DetailActivity;
import com.example.jabwemate.MyDogDetailActivity;
import com.example.jabwemate.R;
import com.example.jabwemate.model.Dog;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class HomeAdapter extends FirestoreRecyclerAdapter<Dog,HomeAdapter.HomeViewHolder> {

    private FirebaseAuth firebaseAuth;
    private String UserID;
    public HomeAdapter(@NonNull FirestoreRecyclerOptions<Dog> options) {
      super(options);
   }


   @Override
   protected void onBindViewHolder(@NonNull final HomeViewHolder holder, final int position, @NonNull final Dog model) {

      holder.DogName.setText(model.getName());
      holder.DogBreed.setText(model.getBreed());
      holder.DogGender.setText(model.getGender());
      if(model.getURL()!=null) {
          Picasso
                  .get()
                  .load(Uri.parse(model.getURL()))
                  .fetch(new Callback() {
                      @Override
                      public void onSuccess() {
                          Picasso
                                  .get()
                                  .load(Uri.parse(model.getURL()))
                                  .fit()
                                  .centerCrop()
                                  .into(holder.DogImage);
                      }

                      @Override
                      public void onError(Exception e) {

                      }
                  });
      }
       firebaseAuth = FirebaseAuth.getInstance();
       UserID = firebaseAuth.getCurrentUser().getUid();
       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

              DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
              String g=snapshot.getId();
              if(model.getUID().compareTo(UserID)==0){
                  Intent i=new Intent(view.getContext(), MyDogDetailActivity.class);
                  i.putExtra("REF",g);
                  view.getContext().startActivity(i);
              }else {
                  Intent i = new Intent(view.getContext(), DetailActivity.class);
                  i.putExtra("REF", g);
                  view.getContext().startActivity(i);
              }
           }

       });

   }

   @NonNull
   @Override
   public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_dog_layout,
              parent, false);
      return new HomeViewHolder(v);
   }

   class HomeViewHolder extends RecyclerView.ViewHolder{

      TextView DogName,DogBreed,DogGender;
      ImageView DogImage;

      public HomeViewHolder(@NonNull View itemView) {
         super(itemView);

         DogName = itemView.findViewById(R.id.dog_name_text);
         DogBreed = itemView.findViewById(R.id.dog_breed_text);
         DogGender = itemView.findViewById(R.id.dog_gender_text);
         DogImage=itemView.findViewById(R.id.cardview_dog_image);

      }
   }

}
