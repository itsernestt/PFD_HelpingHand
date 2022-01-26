package com.example.pfdhelpinghand;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ElderlyViewMore extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    Elderly elderly;
    ArrayList<Appointment> appts;
    RecyclerView apptsRV;
    SharedPreferences sharedPreferences;
    DocumentReference docRef;

    TextView elderlyName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_view_more);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();

        apptsRV = findViewById(R.id.allApptsRV);
        String userID;


        sharedPreferences = getSharedPreferences("usertype", Context.MODE_PRIVATE);
        String type = sharedPreferences.getString("type", "Elderly");

        //Set textviews
        elderlyName = findViewById(R.id.viewMore_elderlyName);


        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                elderly = documentSnapshot.toObject(Elderly.class);
                //elderlyName.setText(elderly.getFullName());

            }
        });


        Button backBtn = findViewById(R.id.viewMore_backBut);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent navigate = new Intent(ElderlyViewMore.this, CaregiverMainActivity.class);
                    startActivity(navigate);

            }
        });

    }
}