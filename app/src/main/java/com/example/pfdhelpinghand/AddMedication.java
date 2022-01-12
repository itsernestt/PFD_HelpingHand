package com.example.pfdhelpinghand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AddMedication extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button addBtn;
    String day;
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    Elderly elderly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication_button);

        EditText editName = findViewById(R.id.medNameInput);
        EditText editDes = findViewById(R.id.medDescriptionInput);
        Spinner editDay = findViewById(R.id.medDayInput);
        addBtn = findViewById(R.id.medAddBtn);
        ArrayAdapter<CharSequence>adapter=ArrayAdapter.createFromResource(this, R.array.days,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editDay.setAdapter(adapter);
        editDay.setOnItemSelectedListener(this);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();

        String userID = user.getUid();
        DocumentReference docRef = fStore.collection("Elderly").document(userID);
        Log.d("TAG", String.valueOf(docRef));

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                elderly = documentSnapshot.toObject(Elderly.class);
                ArrayList<Medication> mList = elderly.getMedList();

                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = editName.getText().toString().trim();
                        String des = editDes.getText().toString().trim();


                        Medication newMed = new Medication(name, des, day, false);

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
                day = "Mon";
                break;
            case "Tuesday":
                day = "Tue";
                break;
            case "Wednesday":
                day = "Wed";
                break;
            case "Thursday":
                day = "Thu";
                break;
            case "Friday":
                day = "Fri";
                break;
            case "Saturday":
                day = "Sat";
                break;
            case "Sunday":
                day = "Sun";
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}