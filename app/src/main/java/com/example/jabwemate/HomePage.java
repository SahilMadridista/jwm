package com.example.jabwemate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.example.jabwemate.HomePadeAdapter.HomeAdapter;
import com.example.jabwemate.consts.SharedPrefConsts;
import com.example.jabwemate.model.Dog;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class HomePage extends AppCompatActivity {

    androidx.appcompat.widget.Toolbar toolbar;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference =firestore.collection("Dogs");
    String userID;
    private HomeAdapter adapter;
    RadioGroup radioGroup;
    RadioButton radioButton;
    Button SearchButton;
    EditText BreedName;
    ProgressDialog filterdialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        BreedName = findViewById(R.id.breed_filter);

        toolbar = findViewById(R.id.home_page_toolbar);
        setSupportActionBar(toolbar);

        SearchButton = findViewById(R.id.filter_button);
        radioGroup = findViewById(R.id.radioGroup);

        filterdialog = new ProgressDialog(this);
        filterdialog.setCancelable(false);
        filterdialog.setMessage("Searching...");

        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadFilterData();

            }
        });

    }

    private void loadFilterData() {

        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);

        String breedname = BreedName.getText().toString().trim();
        String gender = radioButton.getText().toString().trim();

        if(breedname.isEmpty()){
            BreedName.setError("Can't be empty");
            BreedName.requestFocus();
            return;
        }

        filterdialog.show();

        Query query = collectionReference
                .whereEqualTo("Breed",breedname)
                .whereEqualTo("Gender",gender);
        FirestoreRecyclerOptions<Dog> options = new FirestoreRecyclerOptions.Builder<Dog>()
                .setQuery(query, Dog.class)
                .build();

        ArrayList<String> adapterlist = new ArrayList<>();

        adapter = new HomeAdapter(options);

        adapterlist.add(options.toString());

        RecyclerView recyclerView = findViewById(R.id.home_page_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        filterdialog.dismiss();

        Toast.makeText(getApplicationContext(),"Showing results...",Toast.LENGTH_SHORT).show();

        adapter.startListening();

    }


    public void checkButton(View v) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
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


}
