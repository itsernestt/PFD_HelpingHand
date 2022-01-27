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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class AddMedication extends AppCompatActivity {

    Button addBtn, timeBtn;
    TextView timeTV, dateTV;
    EditText editName, editDes;
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
        setContentView(R.layout.activity_add_medication);

        editName = findViewById(R.id.medNameInput);
        editDes = findViewById(R.id.medDescriptionInput);
        timeTV = findViewById(R.id.medTimeTV);
        dateTV = findViewById(R.id.datetimeTV);

        addBtn = findViewById(R.id.medAddBtn);
        timeBtn = findViewById(R.id.chooseTimeBtn);


        sharedPreferences = getSharedPreferences("CaretakerValues", Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString("elderlyID", "UfDuCIuLDqUo3niFg73o5jK3Jml2");

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();


        DocumentReference docRef = fStore.collection("Elderly").document(userID);
        Log.d("TAG", String.valueOf(docRef));

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                elderly = documentSnapshot.toObject(Elderly.class);
                ArrayList<Medication> mList = elderly.getMedList();

                timeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { selectDate();}
                });

                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = editName.getText().toString().trim();
                        String des = editDes.getText().toString().trim();
                        Timestamp ts = new Timestamp(finalCalendar.getTime());

                        Calendar currentTime = Calendar.getInstance();

                        float x = currentTime.compareTo(finalCalendar);

                        if (x>0 || !timeset){
                            Toast.makeText(AddMedication.this, "Pick a future value for date and time!", Toast.LENGTH_SHORT).show();
                        }else{
                            Medication newMed = new Medication(name, des, ts);

                            mList.add(newMed);
                            docRef.update("medList", mList)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(AddMedication.this, "Medication added!", Toast.LENGTH_SHORT).show();
                                            Intent navigateTo = new Intent(AddMedication.this, com.example.pfdhelpinghand.WeeklyMedicationActivity.class);
                                            startActivity(navigateTo);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AddMedication.this, "Doesn't work...", Toast.LENGTH_SHORT).show();
                                            Intent navigateTo = new Intent(AddMedication.this, com.example.pfdhelpinghand.WeeklyMedicationActivity.class);
                                            startActivity(navigateTo);
                                        }
                                    });
                        }
                    }
                });
            }
        });
    }

    private void selectDate(){

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
                selectTime();
            }
        }, YEAR, MONTH, DATE);

        datePickerDialog.show();
    }

    public void selectTime(){
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

                CharSequence timeCharSequence = DateFormat.format("hh:mm a", calendar1);
                timeTV.setText(timeCharSequence);
                timeset = true;
            }
        },HOUR, MINUTE, is24hr);

        timePickerDialog.show();
    }
}