package com.example.jabwemate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.jabwemate.HomePadeAdapter.MyDogAdapter;
import com.example.jabwemate.model.Dog;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class RequestActivity extends AppCompatActivity {

   androidx.appcompat.widget.Toolbar toolbar;
   private FirebaseAuth firebaseAuth;
   private FirebaseFirestore firestore;
   String UserID;
   MyDogAdapter adapter;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_request);

      firebaseAuth = FirebaseAuth.getInstance();
      firestore = FirebaseFirestore.getInstance();

      CollectionReference collectionReference = firestore.collection("Dogs");

      toolbar = findViewById(R.id.requests_toolbar);
      setSupportActionBar(toolbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);

      assert firebaseAuth.getCurrentUser() != null;

      UserID = firebaseAuth.getCurrentUser().getUid();

      Query query = collectionReference.whereEqualTo("UID",UserID);
      FirestoreRecyclerOptions<Dog> options = new FirestoreRecyclerOptions.Builder<Dog>()
              .setQuery(query, Dog.class)
              .build();

      adapter = new MyDogAdapter(options);

      RecyclerView recyclerView = findViewById(R.id.req_recycler_view);
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
