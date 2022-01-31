package com.example.pfdhelpinghand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class AddAppointment extends AppCompatActivity {

    Button dateBtn, createApptBtn;
    TextView dateTV, timeTV;
    EditText locationInput, apptNameInput;
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    Elderly elderly;
    Calendar finalCalendar = Calendar.getInstance();
    SharedPreferences sharedPreferences;
    boolean timeset = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        dateBtn = findViewById(R.id.dateBtn);
        dateTV = findViewById(R.id.dateTV);
        timeTV = findViewById(R.id.timeTV);
        locationInput = findViewById(R.id.locationInput);
        createApptBtn = findViewById(R.id.createApptBtn);
        apptNameInput = findViewById(R.id.apptNameInput);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();

        sharedPreferences = getSharedPreferences("CaretakerValues", Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString("elderlyID", "UfDuCIuLDqUo3niFg73o5jK3Jml2");
        DocumentReference docRef = fStore.collection("Elderly").document(userID);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                elderly = documentSnapshot.toObject(Elderly.class);
                ArrayList<Appointment> apptList = elderly.getApptList();

                createApptBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String apptName = apptNameInput.getText().toString().trim();
                        String location = locationInput.getText().toString().trim();

//                        Date date1 = finalCalendar.getTime();
//                        String strDate = dateFormat.format(date1);
                        com.google.firebase.Timestamp ts = new com.google.firebase.Timestamp(finalCalendar.getTime());
                        Calendar currentTime = Calendar.getInstance();
                        Calendar oneHourForward = Calendar.getInstance();
                        oneHourForward.add(Calendar.HOUR_OF_DAY, 1);
                        float x = currentTime.compareTo(finalCalendar);
                        float y = oneHourForward.compareTo(finalCalendar);
                        if (x>0 || !timeset){
                            Toast.makeText(AddAppointment.this, "Pick a future value for date and time!", Toast.LENGTH_SHORT).show();
                        }else if (y>0){
                            Toast.makeText(AddAppointment.this, "Time set must be more than one hour in the future!", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Appointment newAppt = new Appointment(apptName, location, ts);

                            apptList.add(newAppt);
                            docRef.update("apptList", apptList)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(AddAppointment.this, "Appointment added!", Toast.LENGTH_SHORT).show();
                                            Intent navigateTo = new Intent(AddAppointment.this, com.example.pfdhelpinghand.WeeklyAppointmentActivity.class);
                                            startActivity(navigateTo);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AddAppointment.this, "Doesn't work...", Toast.LENGTH_SHORT).show();
                                            Intent navigateTo = new Intent(AddAppointment.this, com.example.pfdhelpinghand.WeeklyAppointmentActivity.class);
                                            startActivity(navigateTo);
                                        }
                                    });

                            DocumentReference documentReference = fStore.collection("ElderlyAppointmentHistory").document(userID);
                            documentReference.update("apptList", FieldValue.arrayUnion(newAppt)).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddAppointment.this, "Fail to add record!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDateButton();
            }
        });

    }

    private void handleDateButton(){

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, year);
                calendar1.set(Calendar.MONTH, month);
                calendar1.set(Calendar.DATE, day);
                finalCalendar.set(Calendar.YEAR, year);
                finalCalendar.set(Calendar.MONTH, month);
                finalCalendar.set(Calendar.DATE, day);
                CharSequence dateCharSequence = DateFormat.format("EEEE, dd MMM yyyy", calendar1);
                dateTV.setText(dateCharSequence);
                handleTime();
            }
        }, YEAR, MONTH, DATE);

        datePickerDialog.show();
    }

    private void handleTime(){
        Calendar calendar = Calendar.getInstance();
        int HOUR = calendar.get(Calendar.HOUR_OF_DAY);
        int MINUTE = calendar.get(Calendar.MINUTE);

        boolean is24hr = DateFormat.is24HourFormat(this);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {


                Calendar calendar1 = Calendar.getInstance(Locale.ENGLISH);
                calendar1.set(Calendar.HOUR_OF_DAY, hour);
                calendar1.set(Calendar.MINUTE, minute);
                finalCalendar.set(Calendar.HOUR_OF_DAY, hour);
                finalCalendar.set(Calendar.MINUTE, minute);
                finalCalendar.set(Calendar.SECOND, 0);

                CharSequence timeCharSequence = DateFormat.format("hh:mm a", calendar1);
                timeTV.setText(timeCharSequence);
                timeset = true;
            }
        },HOUR, MINUTE, is24hr);

        timePickerDialog.show();
    }
}