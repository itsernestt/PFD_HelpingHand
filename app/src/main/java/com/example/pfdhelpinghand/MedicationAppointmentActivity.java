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
    RecyclerView recyclerView;

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
        appts = new ArrayList<Appointment>();

        String userID = user.getUid();
        recyclerView = findViewById(R.id.recyclerView1);
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

                List medications = new ArrayList();
                meds = elderly.getMedList();
                for (Medication m:
                     meds) {
                    String day = m.getDay();
                    if (day.equals(dateString)){
                        medications.add(m);
                    }
                }

                appts = elderly.getApptList();
                for (Appointment a:
                appts){
                    if (a.day.equals(dateString)){
                        appointmentListTextView.append("\nAppointment name: "+ a.apptName
                                + "\nLocation: " + a.location + "\nDay: " + a.day);
                    }
                }

                MedAdapter medAdapter = new MedAdapter(medications);

                recyclerView.setAdapter(medAdapter);

            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
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
    }

    private void setCurrentDay(TextView t){
        long date = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        String dateString = sdf.format(date);
        t.setText(dateString);
    }
}