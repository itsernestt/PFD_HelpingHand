package com.example.pfdhelpinghand;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HelpLostActivity extends AppCompatActivity {
    TextView elderlyName, emerName, emerPhone;

    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    Elderly elderly;
    String userID;

    ArrayList<EmergencyPerson> emerList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_lost);

        elderlyName = findViewById(R.id.name);
        emerName = findViewById(R.id.helpLostContactName);
        emerPhone = findViewById(R.id.helpLostContactPhone);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        userID = user.getUid();

        fStore.collection("Elderly").document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        elderly = documentSnapshot.toObject(Elderly.class);
                        elderlyName.setText("My name is: " + elderly.getFullName());

                        emerList = elderly.getEmergencyPerson();
                        emerName.setText(emerList.get(0).getFullName());
                        emerPhone.setText(emerList.get(0).getPhoneNumber());
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Elderly not found!", Toast.LENGTH_SHORT).show();
            }
        });




        Button returnBack = findViewById(R.id.helpLostBackButton);
        returnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent navigateToPreviousPage = new Intent(HelpLostActivity.this, com.example.pfdhelpinghand.MainActivity.class);
                startActivity(navigateToPreviousPage);
            }
        });
    }
}