package com.example.pfdhelpinghand;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ElderlyViewMore extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    Elderly elderly;
    SharedPreferences sharedPreferences;

    ArrayList<EmergencyPerson> emerList;
    TextView elderlyName, elderlyPhone, elderlyEmail, elderlyAddress, emergencyName, emergencyPhone;
    Button medReport, apptReport, delete;
    ImageButton callBut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_view_more);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();

        String userID;

        sharedPreferences = getSharedPreferences("CaretakerValues", Context.MODE_PRIVATE);
        userID = sharedPreferences.getString("elderlyID", "Elderly");

        //Set textviews
        elderlyName = findViewById(R.id.viewMore_elderlyName);
        elderlyPhone = findViewById(R.id.viewMore_elderlyPhone);
        elderlyEmail = findViewById(R.id.viewMore_elderlyEmail);
        elderlyAddress = findViewById(R.id.viewMore_elderlyAddress);
        emergencyName = findViewById(R.id.viewMore_emergencyName);
        emergencyPhone = findViewById(R.id.viewMore_emergencyPhone);

        //Set buttons
        medReport = findViewById(R.id.viewMore_medReport);
        apptReport = findViewById(R.id.viewMore_apptReport);
        callBut = findViewById(R.id.viewMore_emergencyPersonCall);

        //Set image button


        fStore.collection("Elderly").document(userID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                elderly = documentSnapshot.toObject(Elderly.class);
                elderlyName.setText(elderly.getFullName());
                elderlyPhone.setText(elderly.getPhoneNumber());
                elderlyEmail.setText(elderly.getEmail());
                elderlyAddress.setText(elderly.getAddress());

                emerList = elderly.getEmergencyPerson();
                emergencyName.setText(emerList.get(0).getFullName());
                emergencyPhone.setText(emerList.get(0).getPhoneNumber());

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