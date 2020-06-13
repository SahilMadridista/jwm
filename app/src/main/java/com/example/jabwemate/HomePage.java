package com.example.jabwemate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.jabwemate.HomePadeAdapter.HomeAdapter;
import com.example.jabwemate.consts.SharedPrefConsts;
import com.example.jabwemate.model.Dog;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class HomePage extends AppCompatActivity {

    androidx.appcompat.widget.Toolbar toolbar;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference =firestore.collection("Dogs");
    String userID;
    private HomeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        toolbar = findViewById(R.id.home_page_toolbar);
        setSupportActionBar(toolbar);

        loadDogData();


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.your_info:

                // Add code here
                startActivity(new Intent(getApplicationContext(),YourInformationActivity.class));
                break;


            case R.id.your_pet_info:

                // Add Code here
                startActivity(new Intent(HomePage.this,myDog.class));
                break;

            case R.id.search_breeds:

                startActivity(new Intent(getApplicationContext(),SearchBreedsActivity.class));
                break;


            case R.id.sign_out:

                SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("login", SharedPrefConsts.NO_LOGIN);
                editor.apply();
                finish();

                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class));
                finish();


            default:
                return super.onOptionsItemSelected(item);

        }

        return true;

    }

    private void loadDogData() {

        assert firebaseAuth.getCurrentUser() != null;

        userID = firebaseAuth.getCurrentUser().getUid();

        Query query = collectionReference.orderBy("Age", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Dog> options = new FirestoreRecyclerOptions.Builder<Dog>()
                .setQuery(query, Dog.class)
                .build();

        adapter = new HomeAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.home_page_recycler_view);
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
