package com.example.jabwemate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.jabwemate.ChooseDogAdapter.ChooseDogAdapter;
import com.example.jabwemate.model.Dog;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ChooseDogForRequestActivity extends AppCompatActivity {

   private FirebaseAuth firebaseAuth;
   private FirebaseFirestore firestore;
   String UserID;
   Button SendReqButton;
   ChooseDogAdapter adapter;
   private FirebaseFirestore dogs_db = FirebaseFirestore.getInstance();
   String senderId;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
              WindowManager.LayoutParams.FLAG_FULLSCREEN);
      setContentView(R.layout.activity_choose_dog_for_request);

      Intent i=getIntent();
      senderId=i.getStringExtra("REG");

      firebaseAuth = FirebaseAuth.getInstance();
      firestore = FirebaseFirestore.getInstance();
      SendReqButton = findViewById(R.id.send_req_button);

      CollectionReference collectionReference = firestore.collection("Dogs");
      UserID = firebaseAuth.getCurrentUser().getUid();


      Query query = collectionReference.whereEqualTo("UID",UserID);
      FirestoreRecyclerOptions<Dog> options = new FirestoreRecyclerOptions.Builder<Dog>()
              .setQuery(query, Dog.class)
              .build();

      adapter = new ChooseDogAdapter(options);
        adapter.getReceiverId(senderId);
      RecyclerView recyclerView = findViewById(R.id.choose_dog_recycler_view);
      recyclerView.setHasFixedSize(true);
      recyclerView.setLayoutManager(new LinearLayoutManager(this));
      recyclerView.setAdapter(adapter);

      SendReqButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            String id= adapter.getSelectedItem();
            if(id==null || id.isEmpty())
                Toast.makeText(ChooseDogForRequestActivity.this,"Please select a dog",Toast.LENGTH_SHORT).show();
            else{
           setArrayAccept(id,senderId);

         }}
      });

   }
   public void setArrayAccept(String id,String id2){
      dogs_db.collection("Dogs").document(id).update("Accept",FieldValue.arrayUnion(id2))
              .addOnSuccessListener(new OnSuccessListener<Void>() {
                  @Override
                  public void onSuccess(Void aVoid) {
                      startActivity(new Intent(ChooseDogForRequestActivity.this,HomePage.class));
                      finish();
                      Toast.makeText(ChooseDogForRequestActivity.this,"Request send",Toast.LENGTH_SHORT).show();
                  }
              });
   }


   @Override
   protected void onStart() {
      super.onStart();
      adapter.startListening();
   }
   @Override
   protected void onStop() {
      super.onStop();
      adapter.stopListening();
   }


}
