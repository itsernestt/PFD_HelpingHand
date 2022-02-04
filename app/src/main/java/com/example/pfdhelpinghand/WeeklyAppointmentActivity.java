package com.example.pfdhelpinghand;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class WeeklyAppointmentActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    Elderly elderly;
    ArrayList<Appointment> appts;
    RecyclerView apptsRV;
    SharedPreferences sharedPreferences;
    DocumentReference docRef;
    LottieAnimationView emptyAnimation;

    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_appointment);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        appts = new ArrayList<Appointment>();
        emptyAnimation = findViewById(R.id.emptyApptAnimationView);
        apptsRV = findViewById(R.id.allApptsRV);
        String userID;

        sharedPreferences = getSharedPreferences("usertype", Context.MODE_PRIVATE);
        String type = sharedPreferences.getString("type", "Elderly");

        if (type.equals("Elderly")) {
            userID = user.getUid();
            docRef = fStore.collection("Elderly").document(userID);
        } else {
            sharedPreferences = getSharedPreferences("CaretakerValues", Context.MODE_PRIVATE);
            userID = sharedPreferences.getString("elderlyID", "UfDuCIuLDqUo3niFg73o5jK3Jml2");
            docRef = fStore.collection("Elderly").document(userID);
        }


        DocumentReference docRef = fStore.collection("Elderly").document(userID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                elderly = documentSnapshot.toObject(Elderly.class);

                appts = elderly.getApptList();
                Collections.sort(appts);

                if (appts.isEmpty()){
                    emptyAnimation.setVisibility(View.VISIBLE);
                }

                ApptAdapter apptAdapter = new ApptAdapter(appts);
                apptsRV.setAdapter(apptAdapter);
            }
        });

        apptsRV.setLayoutManager(new LinearLayoutManager(this));



        Button backBtn = findViewById(R.id.weeklyApptBackButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = getSharedPreferences("usertype", Context.MODE_PRIVATE);
                String temp = sharedPreferences.getString("type", "Elderly");
                if (temp.equals("Elderly")) {
                    Intent navigateToPreviousPage = new Intent(WeeklyAppointmentActivity.this, MedicationAppointmentActivity.class);
                    startActivity(navigateToPreviousPage);
                } else {
                    Intent navigate = new Intent(WeeklyAppointmentActivity.this, CaregiverMainActivity.class);
                    startActivity(navigate);
                }
            }
        });

        Button addAppt = findViewById(R.id.editApptBtn);

        sharedPreferences = getSharedPreferences("usertype", Context.MODE_PRIVATE);
        String temp = sharedPreferences.getString("type", "Elderly");
        if (temp.equals("Elderly")){
            addAppt.setVisibility(View.GONE);
        }
        addAppt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navigate = new Intent(WeeklyAppointmentActivity.this, AddAppointment.class);
                startActivity(navigate);
            }
        });

    }
}