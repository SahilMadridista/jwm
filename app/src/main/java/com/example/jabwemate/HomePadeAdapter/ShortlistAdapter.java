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

import com.example.jabwemate.DogPairsActivity;
import com.example.jabwemate.R;
import com.example.jabwemate.model.Dog;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ShortlistAdapter extends FirestoreRecyclerAdapter<Dog, ShortlistAdapter.ShortlistViewHolder> {

   public ShortlistAdapter(@NonNull FirestoreRecyclerOptions<Dog> options) {
      super(options);
   }

   @Override
   protected void onBindViewHolder(@NonNull final ShortlistViewHolder holder,final int position, @NonNull final Dog model) {

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
      holder.itemView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {


            DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
            String g=snapshot.getId();
            Intent i=new Intent(view.getContext(), DogPairsActivity.class);
            i.putExtra("REF",g);
            view.getContext().startActivity(i);

         }

      });

   }

   @NonNull
   @Override
   public ShortlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_dog_layout,
              parent, false);
      return new ShortlistViewHolder(v);
   }

   class ShortlistViewHolder extends RecyclerView.ViewHolder{

      TextView DogName,DogBreed,DogGender;
      ImageView DogImage;

      public ShortlistViewHolder(@NonNull View itemView) {
         super(itemView);

         DogName = itemView.findViewById(R.id.dog_name_text);
         DogBreed = itemView.findViewById(R.id.dog_breed_text);
         DogGender = itemView.findViewById(R.id.dog_gender_text);
         DogImage=itemView.findViewById(R.id.cardview_dog_image);

      }
   }
}
