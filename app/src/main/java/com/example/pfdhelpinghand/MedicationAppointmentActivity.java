package com.example.pfdhelpinghand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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
    Dialog alarmDialog;
    SharedPreferences sharedpreferences;

    @Override
    protected void onResume() {
        super.onResume();
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        meds = new ArrayList<Medication>();
        appts = new ArrayList<Appointment>();
        long date = System.currentTimeMillis();

        String userID = user.getUid();
        DocumentReference docRef = fStore.collection("Elderly").document(userID);
        medRecyclerView = findViewById(R.id.recyclerView1);
        apptRecyclerView = findViewById(R.id.recyclerView2);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Elderly elderly = documentSnapshot.toObject(Elderly.class);
                SimpleDateFormat sdf = new SimpleDateFormat("EEE");
                SimpleDateFormat sdf2 = new SimpleDateFormat("dd MM yyyy");
                String dateString = sdf.format(date);
                String dateString2 = sdf2.format(date);

                List medications = new ArrayList();
                List appointments = new ArrayList();
                meds = elderly.getMedList();
                appts = elderly.getApptList();

                for (Medication m:
                        meds) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(m.getDay().toDate());
                    String medDay = sdf.format(cal.getTime());
                    if (medDay.equals(dateString)){
                        medications.add(m);
                    }
                }
                if (medications.isEmpty()){
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        Date date1 = dateFormat.parse("01/01/2003");
                        Medication m = new Medication("No medications today!","", new Timestamp(date1));
                        medications.add(m);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                for (Appointment a:appts){
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(a.getTime().toDate());

                    String temp = sdf2.format(cal.getTime());
                    if (dateString2.equals(temp)){
                        appointments.add(a);
                    }
                }
                Collections.sort(appts);
                if (appointments.isEmpty()){
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        Date date1 = dateFormat.parse("01/01/2003");
                        Appointment a = new Appointment("No appointments today!", "", new Timestamp(date1));
                        appointments.add(a);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                MedAdapter medAdapter = new MedAdapter(medications);
                ApptAdapter apptAdapter = new ApptAdapter(appointments);
                medRecyclerView.setAdapter(medAdapter);
                apptRecyclerView.setAdapter(apptAdapter);
            }
        });
    }

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
                SimpleDateFormat sdf2 = new SimpleDateFormat("dd MM yyyy");
                String dateString = sdf.format(date);
                String dateString2 = sdf2.format(date);

                List medications = new ArrayList();
                List appointments = new ArrayList();
                meds = elderly.getMedList();
                appts = elderly.getApptList();

                Intent intent = getIntent();
                if (intent.hasExtra("medname")){
                    String medname = intent.getStringExtra("medname");
                    alarmDialog = new Dialog(MedicationAppointmentActivity.this);
                    alarmDialog.setContentView(R.layout.alarm_popup);
                    TextView medName = (TextView) alarmDialog.findViewById(R.id.medNameTV);
                    TextView medDesc = (TextView) alarmDialog.findViewById(R.id.medDescriptionTV);

                    for (Medication m:
                        meds){
                        if (m.medName.equals(medname)){
                            medName.setText(medname);
                            medDesc.setText(m.medDescription);
                            meds.remove(m);
                            DocumentReference addtoML = fStore.collection("ElderlyMedicationML").document(userID);
                            addtoML.update("alarmSuccess", FieldValue.arrayUnion(m)).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MedicationAppointmentActivity.this, "Fail to add ML record!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    alarmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alarmDialog.show();
                    Collections.sort(meds);
                    docRef.update("medList", meds).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(MedicationAppointmentActivity.this, "Alarm removed.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else if (intent.hasExtra("apptname")){
                    String apptName = intent.getStringExtra("apptname");
                    alarmDialog = new Dialog(MedicationAppointmentActivity.this);
                    alarmDialog.setContentView(R.layout.alarm_popup);
                    TextView medName = (TextView) alarmDialog.findViewById(R.id.medNameTV);
                    TextView medDesc = (TextView) alarmDialog.findViewById(R.id.medDescriptionTV);

                    for (Appointment a: appts){
                        if (a.apptName.equals(apptName)){
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
                            medName.setText(apptName);
                            Calendar temp = Calendar.getInstance();
                            temp.setTime(a.getTime().toDate());
                            medDesc.setText(a.location + "\n" + simpleDateFormat.format(temp.getTime()));
                            appts.remove(a);
                        }
                    }
                    alarmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alarmDialog.show();
                    Collections.sort(appts);

                    docRef.update("apptList", appts).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(MedicationAppointmentActivity.this, "Alarm removed.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                for (Medication m:
                        meds) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(m.getDay().toDate());
                    String medDay = sdf.format(cal.getTime());
                    if (medDay.equals(dateString)){
                        medications.add(m);
                    }
                }
                if (medications.isEmpty()){
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        Date date1 = dateFormat.parse("01/01/2003");
                        Medication m = new Medication("No medications today!","", new Timestamp(date1));
                        medications.add(m);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                for (Appointment a:appts){
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(a.getTime().toDate());

                    String temp = sdf2.format(cal.getTime());
                    if (dateString2.equals(temp)){
                        appointments.add(a);
                    }
                }
                Collections.sort(appts);
                if (appointments.isEmpty()){
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        Date date1 = dateFormat.parse("01/01/2003");
                        Appointment a = new Appointment("No appointments today!", "", new Timestamp(date1));
                        appointments.add(a);
                    } catch (ParseException e) {
                        e.printStackTrace();
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