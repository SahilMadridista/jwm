package com.example.jabwemate.HomePadeAdapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jabwemate.MyDogDetailActivity;
import com.example.jabwemate.R;
import com.example.jabwemate.RequestDetails;
import com.example.jabwemate.model.Dog;
import com.example.jabwemate.myDog;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MyDogDetailsAdapter extends FirestoreRecyclerAdapter<Dog, MyDogDetailsAdapter.MyDogDetailsViewHolder> {

   private Context context;
   com.example.jabwemate.myDog myDog;
   private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

   public MyDogDetailsAdapter(@NonNull FirestoreRecyclerOptions<Dog> options) {
      super(options);
   }

   @Override
   protected void onBindViewHolder(@NonNull final MyDogDetailsViewHolder holder,final int position, @NonNull final Dog model) {

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

      holder.Delete.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {

            DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
            final String ID = snapshot.getId();

            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setMessage("Do you really want to delete this dog ?").setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(final DialogInterface dialogInterface, int i) {

                          firestore.collection("Dogs").document(ID).delete()
                                  .addOnSuccessListener(new OnSuccessListener<Void>() {
                                     @Override
                                     public void onSuccess(Void aVoid) {
                                        dialogInterface.cancel();
                                     }
                                  }).addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                                dialogInterface.cancel();
                             }
                          });

                       }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                  dialogInterface.cancel();
               }
            });

            AlertDialog alert = builder.create();
            alert.show();

         }
      });

      holder.itemView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {

            DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
            String g=snapshot.getId();
            Intent i=new Intent(view.getContext(), MyDogDetailActivity.class);
            i.putExtra("REF",g);
            view.getContext().startActivity(i);

         }

      });

   }

   @NonNull
   @Override
   public MyDogDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_dog_layout_user,
              parent, false);
      context = parent.getContext();
      return new MyDogDetailsViewHolder(v);
   }

   class MyDogDetailsViewHolder extends RecyclerView.ViewHolder{

      TextView DogName,DogBreed,DogGender;
      ImageView DogImage;
      ImageView Delete;

      public MyDogDetailsViewHolder(@NonNull View itemView) {
         super(itemView);

         DogName = itemView.findViewById(R.id.user_dog_name_text);
         DogBreed = itemView.findViewById(R.id.user_dog_breed_text);
         DogGender = itemView.findViewById(R.id.user_dog_gender_text);
         DogImage=itemView.findViewById(R.id.user_cardview_dog_image);
         Delete = itemView.findViewById(R.id.user_dog_delete_icon);

      }
   }
}
