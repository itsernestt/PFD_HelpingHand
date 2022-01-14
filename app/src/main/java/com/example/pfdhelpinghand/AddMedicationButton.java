package com.example.pfdhelpinghand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AddMedicationButton extends AppCompatActivity {

    Button addBtn;

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
                        String day = editDay.getTag().toString().trim();


                        Medication newMed = new Medication(name, des, day, true);

                        mList.add(newMed);
                        docRef.update("medList", mList)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(AddMedicationButton.this, "Medication added!", Toast.LENGTH_SHORT).show();
                                        Intent navigateTo = new Intent(AddMedicationButton.this, com.example.pfdhelpinghand.WeeklyMedicationActivity.class);
                                        startActivity(navigateTo);
                                    }
                                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddMedicationButton.this, "Doesn't work...", Toast.LENGTH_SHORT).show();
                                Intent navigateTo = new Intent(AddMedicationButton.this, com.example.pfdhelpinghand.WeeklyMedicationActivity.class);
                                startActivity(navigateTo);
                            }
                        });

                    }
                });
            }
        });
    }
}