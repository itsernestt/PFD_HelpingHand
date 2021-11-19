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
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class WeeklyMedicationActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    Elderly elderly;
    ArrayList<Medication> meds;
    TextView monDisplay;
    TextView tuesDisplay;
    TextView wedDisplay;
    TextView thursDisplay;
    TextView friDisplay;
    TextView satDisplay;
    TextView sunDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_medication);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        meds = new ArrayList<Medication>();

        monDisplay = findViewById(R.id.weeklyMedicationMon);
        tuesDisplay = findViewById(R.id.weeklyMedicationTues);
        wedDisplay = findViewById(R.id.weeklyMedicationWed);
        thursDisplay = findViewById(R.id.weeklyMedicationThurs);
        friDisplay = findViewById(R.id.weeklyMedicationFri);
        satDisplay = findViewById(R.id.weeklyMedicationSat);
        sunDisplay = findViewById(R.id.weeklyMedicationSun);

        String userID = user.getUid();
        Log.d("TAG", userID);

        DocumentReference docRef = fStore.collection("Elderly").document(userID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                elderly = documentSnapshot.toObject(Elderly.class);

                meds = elderly.getMedList();
                for (Medication m:
                     meds) {
                    String day = m.getDay();

                    switch (day){
                        case ("Mon"):
                            monDisplay.append("\nMedication name: " + m.medName +
                                    "\nInstructions: " + m.medDescription + "\nDay: " + m.day + "\n");
                            break;

                        case ("Tue"):
                            tuesDisplay.append("\nMedication name: " + m.medName +
                                    "\nInstructions: " + m.medDescription + "\nDay: " + m.day + "\n");
                            break;

                        case ("Wed"):
                            wedDisplay.append("\nMedication name: " + m.medName +
                                    "\nInstructions: " + m.medDescription + "\nDay: " + m.day + "\n");
                            break;
                        case ("Thurs"):
                            thursDisplay.append("\nMedication name: " + m.medName +
                                    "\nInstructions: " + m.medDescription + "\nDay: " + m.day + "\n");
                            break;
                        case ("Fri"):
                            friDisplay.append("\nMedication name: " + m.medName +
                                    "\nInstructions: " + m.medDescription + "\nDay: " + m.day + "\n");
                            break;
                        case ("Sat"):
                            satDisplay.append("\nMedication name: " + m.medName +
                                    "\nInstructions: " + m.medDescription + "\nDay: " + m.day + "\n");
                            break;
                        case ("Sun"):
                            sunDisplay.append("\nMedication name: " + m.medName +
                                    "\nInstructions: " + m.medDescription + "\nDay: " + m.day + "\n");
                            break;
                    }
                }
            }
        });



        Button weeklyMedBackButton = findViewById(R.id.weeklyMedicationBackButton);
        Button editButton = findViewById(R.id.EditMedBtn);

        weeklyMedBackButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent navigateToPreviousPage = new Intent(WeeklyMedicationActivity.this, com.example.pfdhelpinghand.MedicationAlarmActivity.class);
                startActivity(navigateToPreviousPage);
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navigate = new Intent(WeeklyMedicationActivity.this, AddMedicationButton.class);
                startActivity(navigate);
            }
        });




    }
}