package com.example.pfdhelpinghand;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class WeeklyMedicationActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    Elderly elderly;
    ArrayList<Medication> meds;
    RecyclerView medRV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_medication);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        meds = new ArrayList<Medication>();

        medRV = findViewById(R.id.allMedsRV);
        String userID = user.getUid();


        DocumentReference docRef = fStore.collection("Elderly").document(userID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                elderly = documentSnapshot.toObject(Elderly.class);
                List medications = new ArrayList();
                List mon = new ArrayList();
                List tue = new ArrayList();
                List wed = new ArrayList();
                List thu = new ArrayList();
                List fri = new ArrayList();
                List sat = new ArrayList();
                List sun = new ArrayList();


                meds = elderly.getMedList();
                for (Medication m:
                     meds) {
                    String day = m.getDay();

                    switch (day){
                        case ("Mon"):
                            mon.add(m);
                            break;
                        case ("Tue"):
                            tue.add(m);
                            break;
                        case ("Wed"):
                            wed.add(m);
                            break;
                        case ("Thu"):
                            thu.add(m);
                            break;
                        case ("Fri"):
                            fri.add(m);
                            break;
                        case ("Sat"):
                            sat.add(m);
                            break;
                        case ("Sun"):
                            sun.add(m);
                            break;
                    }
                }

                medications.addAll(mon);
                medications.addAll(tue);
                medications.addAll(wed);
                medications.addAll(thu);
                medications.addAll(fri);
                medications.addAll(sat);
                medications.addAll(sun);
                // there has to be a better way of doing this but my mind is running really slow

                MedAdapter medAdapter = new MedAdapter(medications);
                medRV.setAdapter(medAdapter);
            }
        });


        medRV.setLayoutManager(new LinearLayoutManager(this));

        Button weeklyMedBackButton = findViewById(R.id.weeklyMedicationBackButton);
        Button editButton = findViewById(R.id.EditMedBtn);

        weeklyMedBackButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent navigateToPreviousPage = new Intent(WeeklyMedicationActivity.this, MedicationAppointmentActivity.class);
                startActivity(navigateToPreviousPage);
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navigate = new Intent(WeeklyMedicationActivity.this, AddMedication.class);
                startActivity(navigate);
            }
        });




    }
}