package com.example.pfdhelpinghand;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class CaregiverSetting extends AppCompatActivity {

    Button backBut, logoutBut;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_setting);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();



        backBut = findViewById(R.id.caregiver_setting_back);
        backBut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent goBack = new Intent(CaregiverSetting.this, CaregiverMainActivity.class);
                startActivity(goBack);
            }
        });



        logoutBut = findViewById(R.id.caregiver_logout);
        logoutBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fAuth.signOut();
                Intent intent = new Intent(CaregiverSetting.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }
        });






    }



}