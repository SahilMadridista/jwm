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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChooseDogAdapter extends FirestoreRecyclerAdapter<Dog, ChooseDogAdapter.ChooseDogViewHoler> {


    private Context context;
    private int selectedPosition = -1;
    private RadioButton lastCheckedRB = null;
    private String id = "", ReceiverID, SenderID;
    private FirebaseFirestore dogs_db = FirebaseFirestore.getInstance();
    String accept;
    String pair;


    public ChooseDogAdapter(@NonNull FirestoreRecyclerOptions<Dog> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ChooseDogViewHoler holder, final int position, @NonNull final Dog model) {

        holder.choosedognametext.setText(model.getName());
        if (model.getURL() != null) {
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
        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.radioButton.setChecked(false);
            }
        });
        holder.radioButton.setChecked(position == selectedPosition);
        holder.radioButton.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
                SenderID = snapshot.getId();
                accept="";
                pair="";
                dogs_db.collection("Dogs").document(SenderID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                       DocumentSnapshot documentSnapshot = task.getResult();
                       if (documentSnapshot.get("Accept") != null)
                          accept = String.valueOf(documentSnapshot.get("Accept"));
                       if (documentSnapshot.get("Pair") != null)
                          pair = (String.valueOf(documentSnapshot.get("Pair")));
                            View V=holder.itemView;
                        if (accept != null && accept.contains(ReceiverID)) {
                            Toast.makeText(V.getContext(), "Already Accept Send", Toast.LENGTH_SHORT).show();
                        } else if (pair != null && pair.contains(ReceiverID)) {
                            Toast.makeText(V.getContext(), "Already In Pair", Toast.LENGTH_SHORT).show();
                        } else {
                           id=SenderID;
                            View view=holder.radioButton;
                            itemCheckChanged(view);
                        }

                    }
                });
            }
        });


    }

    private void itemCheckChanged(View v) {
        selectedPosition = (Integer) v.getTag();
        notifyDataSetChanged();
    }

    public String getSelectedItem() {
        return id;
    }

    public void getReceiverId(String id) {
        ReceiverID = id;

    }

    @NonNull
    @Override
    public ChooseDogViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.choose_single_dog_layout,
                parent, false);
        return new ChooseDogViewHoler(v);
    }

    class ChooseDogViewHoler extends RecyclerView.ViewHolder {

        TextView choosedognametext;
        ImageView choosedogimage;
        RadioButton radioButton;

        public ChooseDogViewHoler(@NonNull View itemView) {
            super(itemView);

            choosedognametext = itemView.findViewById(R.id.choose_dog_name_text);
            choosedogimage = itemView.findViewById(R.id.choose_dog_card_image);
            radioButton = itemView.findViewById(R.id.choose_dog_radioButton);

        }
    }
}
