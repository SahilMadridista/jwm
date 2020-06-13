package com.example.jabwemate.HomePadeAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jabwemate.R;
import com.example.jabwemate.model.Dog;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class HomeAdapter extends FirestoreRecyclerAdapter<Dog,HomeAdapter.HomeViewHolder> {

   public HomeAdapter(@NonNull FirestoreRecyclerOptions<Dog> options) {
      super(options);
   }

   @Override
   protected void onBindViewHolder(@NonNull HomeViewHolder holder, int position, @NonNull Dog model) {

      holder.DogName.setText(model.getDogname());
      holder.DogBreed.setText(model.getDogbreed());
      holder.DogGender.setText(model.getDoggender());

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

      public HomeViewHolder(@NonNull View itemView) {
         super(itemView);

         DogName = itemView.findViewById(R.id.dog_name_text);
         DogBreed = itemView.findViewById(R.id.dog_breed_text);
         DogGender = itemView.findViewById(R.id.dog_gender_text);

      }
   }
}
