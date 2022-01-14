package com.example.pfdhelpinghand;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MedicationAppointmentActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    Elderly elderly;
    ArrayList<Medication> meds;
    ArrayList<Appointment> appts;
    RecyclerView medRecyclerView;
    RecyclerView apptRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medappt_alarm);

        TextView currentDate = findViewById(R.id.medicationCurrentDate);
        Button medicationBackButton = findViewById(R.id.medicationBackButton);
        Button weeklyMedButton = findViewById(R.id.weeklyMedicationButton);
        Button weeklyApptButton = findViewById(R.id.weeklyAppointmentButton);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        meds = new ArrayList<Medication>();
        appts = new ArrayList<Appointment>();

        String userID = user.getUid();
        medRecyclerView = findViewById(R.id.recyclerView1);
        apptRecyclerView = findViewById(R.id.recyclerView2);
        DocumentReference docRef = fStore.collection("Elderly").document(userID);
        Log.d("TAG", String.valueOf(docRef));
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                elderly = documentSnapshot.toObject(Elderly.class);
                long date = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("EEE");
                String dateString = sdf.format(date);




                List medications = new ArrayList();
                List appointments = new ArrayList();
                meds = elderly.getMedList();
                if (meds.isEmpty()){
                    Medication m = new Medication("No medications today!","", "", false);
                    medications.add(m);
                }else{
                    for (Medication m:
                            meds) {
                        String day = m.getDay();
                        if (day.equals(dateString)){
                            medications.add(m);
                        }
                    }
                }


                appts = elderly.getApptList();
                if (appts.isEmpty()){
                    Appointment a = new Appointment("No appointments today!","","");
                    appointments.add(a);
                }else{
                    for (Appointment a:
                            appts){
                        if (a.day.equals(dateString)){
                            appointments.add(a);
                        }
                    }
                }

                MedAdapter medAdapter = new MedAdapter(medications);
                ApptAdapter apptAdapter = new ApptAdapter(appointments);
                medRecyclerView.setAdapter(medAdapter);
                apptRecyclerView.setAdapter(apptAdapter);
            }
        });

        medRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        apptRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        setCurrentDay(currentDate);

        medicationBackButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent navigateToPreviousPage = new Intent(MedicationAppointmentActivity.this, com.example.pfdhelpinghand.MainActivity.class);
                startActivity(navigateToPreviousPage);
            }
        });

        weeklyMedButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent navigateTo = new Intent(MedicationAppointmentActivity.this, com.example.pfdhelpinghand.WeeklyMedicationActivity.class);
                startActivity(navigateTo);
            }
        });

        weeklyApptButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent navigateTo = new Intent(MedicationAppointmentActivity.this, com.example.pfdhelpinghand.WeeklyAppointmentActivity.class);
                startActivity(navigateTo);
            }
        });
    }

    private void setCurrentDay(TextView t){
        long date = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        String dateString = sdf.format(date);
        t.setText(dateString);
    }
}