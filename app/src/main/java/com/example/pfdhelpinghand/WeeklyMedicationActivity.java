package com.example.pfdhelpinghand;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class WeeklyMedicationActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    Elderly elderly;
    ArrayList<Medication> meds;
    RecyclerView medRV;
    SharedPreferences sharedPreferences;
    DocumentReference docRef;
    LottieAnimationView emptyMed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_medication);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        meds = new ArrayList<Medication>();
        emptyMed = findViewById(R.id.emptyMedAnimationView);

        medRV = findViewById(R.id.allMedsRV);
        sharedPreferences = getSharedPreferences("CaretakerValues", Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString("elderlyID", "UfDuCIuLDqUo3niFg73o5jK3Jml2");

        sharedPreferences = getSharedPreferences("usertype", Context.MODE_PRIVATE);
        String type = sharedPreferences.getString("type", "Elderly");
        if (type.equals("Elderly")){
            docRef = fStore.collection("Elderly").document(user.getUid());
        }else{
            docRef = fStore.collection("Elderly").document(userID);

        }
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                elderly = documentSnapshot.toObject(Elderly.class);
                List medications = new ArrayList();

                meds = elderly.getMedList();
                Collections.sort(meds);

                if (meds.isEmpty()){
                    emptyMed.setVisibility(View.VISIBLE);
                }

                MedAdapter medAdapter = new MedAdapter(meds);
                medRV.setAdapter(medAdapter);
            }
        });


        medRV.setLayoutManager(new LinearLayoutManager(this));

        Button weeklyMedBackButton = findViewById(R.id.weeklyMedicationBackButton);
        Button editButton = findViewById(R.id.EditMedBtn);

        weeklyMedBackButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sharedPreferences = getSharedPreferences("usertype", Context.MODE_PRIVATE);
                String temp = sharedPreferences.getString("type", "Elderly");
                if (temp.equals("Elderly")){
                    Intent navigateToPreviousPage = new Intent(WeeklyMedicationActivity.this, MedicationAppointmentActivity.class);
                    startActivity(navigateToPreviousPage);
                }else{
                    Intent navigateToPreviousPage = new Intent(WeeklyMedicationActivity.this, CaregiverMainActivity.class);
                    startActivity(navigateToPreviousPage);
                }

            }
        });
        sharedPreferences = getSharedPreferences("usertype", Context.MODE_PRIVATE);
        String temp = sharedPreferences.getString("type", "Elderly");
        if (temp.equals("Elderly")){
            editButton.setVisibility(View.GONE);
        }
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navigate = new Intent(WeeklyMedicationActivity.this, AddMedication.class);
                startActivity(navigate);
            }
        });




    }
}