package com.example.jabwemate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.jabwemate.ChooseDogAdapter.ChooseDogAdapter;
import com.example.jabwemate.HomePadeAdapter.MyDogAdapter;
import com.example.jabwemate.model.Dog;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ChooseDogForRequestActivity extends AppCompatActivity {

   private FirebaseAuth firebaseAuth;
   private FirebaseFirestore firestore;
   String UserID;
   Button SendReqButton;
   ChooseDogAdapter adapter;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
              WindowManager.LayoutParams.FLAG_FULLSCREEN);
      setContentView(R.layout.activity_choose_dog_for_request);

      firebaseAuth = FirebaseAuth.getInstance();
      firestore = FirebaseFirestore.getInstance();
      SendReqButton = findViewById(R.id.send_req_button);

      SendReqButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Toast.makeText(getApplicationContext(),"Clicked",Toast.LENGTH_LONG).show();
         }
      });

      CollectionReference collectionReference = firestore.collection("Dogs");
      UserID = firebaseAuth.getCurrentUser().getUid();


      Query query = collectionReference.whereEqualTo("UID",UserID);
      FirestoreRecyclerOptions<Dog> options = new FirestoreRecyclerOptions.Builder<Dog>()
              .setQuery(query, Dog.class)
              .build();

      adapter = new ChooseDogAdapter(options);

      RecyclerView recyclerView = findViewById(R.id.choose_dog_recycler_view);
      recyclerView.setHasFixedSize(true);
      recyclerView.setLayoutManager(new LinearLayoutManager(this));
      recyclerView.setAdapter(adapter);


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
