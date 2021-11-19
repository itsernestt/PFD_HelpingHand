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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_medication);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        meds = new ArrayList<Medication>();

        monDisplay = findViewById(R.id.weeklyMedicationMon);

        String userID = user.getUid();
        Log.d("TAG", userID);

        DocumentReference docRef = fStore.collection("Elderly").document(userID);
        Log.d("TAG", String.valueOf(docRef));
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                elderly = documentSnapshot.toObject(Elderly.class);
                Log.d("TAG", String.valueOf(elderly==null));

                meds = elderly.getMedList();
                Medication monday = meds.get(0);
                monDisplay.setText("Medication name: " +monday.medName +
                        "\n" + "Medication description: " + monday.medDescription + "\n" +
                        "Day: " + monday.day);
            }
        });



        Button weeklyMedBackButton = findViewById(R.id.weeklyMedicationBackButton);


        weeklyMedBackButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent navigateToPreviousPage = new Intent(WeeklyMedicationActivity.this, com.example.pfdhelpinghand.MedicationAlarmActivity.class);
                startActivity(navigateToPreviousPage);
            }
        });




    }
}