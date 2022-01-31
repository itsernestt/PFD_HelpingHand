package com.example.pfdhelpinghand;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class ElderlyAppointmentReportActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    Button apptBack;
    RecyclerView reportsRV;
    ArrayList<Appointment> appts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_appointment_report);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        reportsRV = findViewById(R.id.apptReportRV);
        apptBack = findViewById(R.id.apptReportBack);

        Intent intent = getIntent();
        String elderlyID = intent.getStringExtra("elderlyID");

        DocumentReference docRef = fStore.collection("ElderlyAppointmentHistory").document(elderlyID);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot){
                ArrayList<Appointment> aList = new ArrayList<>();
                ApptReportElderly report = documentSnapshot.toObject(ApptReportElderly.class);
                aList = report.apptList;
                if (aList.isEmpty()){
                    Calendar c = Calendar.getInstance();
                    aList.add(new Appointment("No appointments yet!","", new Timestamp(c.getTime())));
                }
                Collections.sort(aList);
                ApptReportAdapter adapter = new ApptReportAdapter(aList);
                reportsRV.setAdapter(adapter);
                reportsRV.setLayoutManager(new LinearLayoutManager(ElderlyAppointmentReportActivity.this));
            }
        });
    }
}