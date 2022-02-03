package com.example.pfdhelpinghand;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ElderlySettingsActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    Elderly elderly;
    Caretaker caretaker;

    SharedPreferences sharedPreferences;
    String userID;
    String elderlyID;

    ArrayList<EmergencyPerson> emerList;
    ArrayList<String> elderlyArrayList;
    ArrayList<String> caretakerList;

    TextView elderlyName, elderlyPhone, elderlyEmail,
            elderlyAddress, emergencyName, emergencyPhone,
            elderlyPScore, viewCaretakers
            ;


    Button eldSettingsBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_settings);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        userID = user.getUid();


        sharedPreferences = getSharedPreferences("CaretakerValues", Context.MODE_PRIVATE);
        elderlyID = sharedPreferences.getString("elderlyID", "Elderly");

        //Set textviews
        elderlyName = findViewById(R.id.elderlySetting_elderlyName);
        elderlyPhone = findViewById(R.id.elderlySetting_phoneNum);
        elderlyEmail = findViewById(R.id.elderlySetting_email);
        elderlyAddress = findViewById(R.id.elderlySetting_address);
        emergencyName = findViewById(R.id.elderlySetting_emergencyname);
        emergencyPhone = findViewById(R.id.elderlySetting_emergencyPhone);
        elderlyPScore = findViewById(R.id.elderlySetting_pscore);

        //Set buttons
        eldSettingsBackBtn = findViewById(R.id.eldSettingsBack);
        viewCaretakers = findViewById(R.id.caregiversTV);

        //Get elderly object
        fStore.collection("Elderly").document(userID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                elderly = documentSnapshot.toObject(Elderly.class);
                elderlyName.setText(elderly.getFullName());
                elderlyPhone.setText(elderly.getPhoneNumber());
                elderlyEmail.setText(elderly.getEmail());
                elderlyPScore.setText(String.valueOf(elderly.getP_score()));
                String address = elderly.getAddress();

                String address2 = address.replaceAll("\\s+","");
                String[] str = address2.split("[,]", 0);
                String latString = str[0];
                String lngString = str[1];
                double lat = Double.parseDouble(latString);
                double lng = Double.parseDouble(lngString);

                Geocoder geocoder = new Geocoder(ElderlySettingsActivity.this);
                try {
                    List<Address> addressList = geocoder.getFromLocation(lat, lng, 1);
                    String addressLine = addressList.get(0).getAddressLine(0);
                    elderlyAddress.setText(addressLine);

                }
                catch (Exception e)
                {
                    elderlyAddress.setText(elderly.getAddress());
                }



                emerList = elderly.getEmergencyPerson();
                emergencyName.setText(emerList.get(0).getFullName());
                emergencyPhone.setText(emerList.get(0).getPhoneNumber());


            }
        });


        eldSettingsBackBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent goBack = new Intent(ElderlySettingsActivity.this, MainActivity.class);
                startActivity(goBack);
            }
        });


        viewCaretakers.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent navigateNext = new Intent(ElderlySettingsActivity.this, ViewCaregivers.class);
                startActivity(navigateNext);
            }
        });



    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
}