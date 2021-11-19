package com.example.pfdhelpinghand;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MedicationAlarmActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    Elderly elderly;
    ArrayList<Medication> meds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_alarm);

        TextView currentDate = findViewById(R.id.medicationCurrentDate);
        TextView medicationListTextView = findViewById(R.id.medicationListTV);
        TextView appointmentListTextView = findViewById(R.id.appointmentListTV);
        Button medicationBackButton = findViewById(R.id.medicationBackButton);
        Button weeklyMedButton = findViewById(R.id.weeklyMedicationButton);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        meds = new ArrayList<Medication>();

        String userID = user.getUid();
        DocumentReference docRef = fStore.collection("Elderly").document(userID);
        Log.d("TAG", String.valueOf(docRef));
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                elderly = documentSnapshot.toObject(Elderly.class);
                long date = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("EEE");
                String dateString = sdf.format(date);
                Log.d("TAG", dateString);

                Toast.makeText(getApplicationContext(), elderly.getFullName(), Toast.LENGTH_LONG).show();

                meds = elderly.getMedList();
                Medication medication = meds.get(0);
                medicationListTextView.setText(medication.day);

                for (Medication m:
                     elderly.getMedList()) {
                    if (m.day == dateString){
                        medicationListTextView.append("\n"+m.medName + "\t" + m.medDescription);
                    }
                }

                for (Appointment a:
                elderly.getApptList()){
                    if (a.day == dateString){
                        appointmentListTextView.append("\n"+a.apptName + "\t" + a.location);
                    }
                }
            }
        });

        setCurrentDay(currentDate);

        medicationBackButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent navigateToPreviousPage = new Intent(MedicationAlarmActivity.this, com.example.pfdhelpinghand.MainActivity.class);
                startActivity(navigateToPreviousPage);
            }
        });

        weeklyMedButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent navigateTo = new Intent(MedicationAlarmActivity.this, com.example.pfdhelpinghand.WeeklyMedicationActivity.class);
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