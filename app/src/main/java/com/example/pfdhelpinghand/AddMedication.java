package com.example.pfdhelpinghand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class AddMedication extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button addBtn, timeBtn;
    TextView timeTV;
    EditText editName, editDes;
    Spinner editDay;
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    Elderly elderly;
    Calendar finalCalendar = Calendar.getInstance();
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication);

        editName = findViewById(R.id.medNameInput);
        editDes = findViewById(R.id.medDescriptionInput);
        timeTV = findViewById(R.id.medTimeTV);
        editDay = findViewById(R.id.medDayInput);
        addBtn = findViewById(R.id.medAddBtn);
        timeBtn = findViewById(R.id.chooseTimeBtn);
        ArrayAdapter<CharSequence>adapter=ArrayAdapter.createFromResource(this, R.array.days,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editDay.setAdapter(adapter);
        editDay.setOnItemSelectedListener(this);

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
                    public void onClick(View v) { selectTime();}
                });

                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = editName.getText().toString().trim();
                        String des = editDes.getText().toString().trim();
                        Timestamp ts = new Timestamp(finalCalendar.getTime());


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
                });
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedDay = parent.getItemAtPosition(position).toString();
        switch (selectedDay){
            case "Monday":
                finalCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                break;
            case "Tuesday":
                finalCalendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                break;
            case "Wednesday":
                finalCalendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                break;
            case "Thursday":
                finalCalendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                break;
            case "Friday":
                finalCalendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                break;
            case "Saturday":
                finalCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                break;
            case "Sunday":
                finalCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
            }
        },HOUR, MINUTE, is24hr);

        timePickerDialog.show();
    }
}