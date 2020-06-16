package com.example.jabwemate.ChooseDogAdapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jabwemate.R;
import com.example.jabwemate.model.Dog;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChooseDogAdapter extends FirestoreRecyclerAdapter<Dog, ChooseDogAdapter.ChooseDogViewHoler> {


   private Context context;
   private int selectedPosition = -1;
   private RadioButton lastCheckedRB = null;

   public ChooseDogAdapter(@NonNull FirestoreRecyclerOptions<Dog> options) {
      super(options);
   }

   @Override
   protected void onBindViewHolder(@NonNull final ChooseDogViewHoler holder,final int position, @NonNull final Dog model) {

      holder.choosedognametext.setText(model.getName());
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
                               .into(holder.choosedogimage);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                 });
      }

      holder.choosedognametext.setTag(position);
      holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(RadioGroup radioGroup, int i) {
            RadioButton checked_rb = radioGroup.findViewById(i);
            if (lastCheckedRB != null) {
               lastCheckedRB.setChecked(false);
            }

            lastCheckedRB = checked_rb;
         }
      });
   }


   @NonNull
   @Override
   public ChooseDogViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.choose_single_dog_layout,
              parent, false);
      return new ChooseDogViewHoler(v);
   }

   class ChooseDogViewHoler extends RecyclerView.ViewHolder{

      TextView choosedognametext;
      ImageView choosedogimage;
      RadioGroup radioGroup;

      public ChooseDogViewHoler(@NonNull View itemView) {
         super(itemView);

         choosedognametext = itemView.findViewById(R.id.choose_dog_name_text);
         choosedogimage = itemView.findViewById(R.id.choose_dog_card_image);
         radioGroup = itemView.findViewById(R.id.choose_dog_radioGroup);

      }
   }
}
