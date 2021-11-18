package com.example.pfdhelpinghand;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MedicationAlarmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_alarm);

        TextView currentDate = findViewById(R.id.medicationCurrentDate);
        TextView medicationListTextView = findViewById(R.id.medicationListTV);
        TextView appointmentListTextView = findViewById(R.id.appointmentListTV);
        Button medicationBackButton = findViewById(R.id.medicationBackButton);
        Button weeklyMedButton = findViewById(R.id.weeklyMedicationButton);

        List<Medication> medicationList = new ArrayList<>();
        Gson gson = new Gson();
        SharedPreferences mPrefs = getSharedPreferences("details", MODE_PRIVATE);
        String json = mPrefs.getString("elderlyObj", "");
        Elderly elderly = gson.fromJson(json, Elderly.class);
        /*
        for (Medication m: elderly.medList) {
            medicationList.add(m);
        }

        */
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