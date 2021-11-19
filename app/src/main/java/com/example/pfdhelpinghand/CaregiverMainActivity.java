package com.example.pfdhelpinghand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CaregiverMainActivity extends AppCompatActivity {

    // here
    TextView welcomeBanner, caregiverViewElderly;
    FirebaseUser user;
    FirebaseFirestore fStore;
    Caretaker caretaker;
    Dialog myDialog;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_main);

        fStore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        welcomeBanner = findViewById(R.id.welcomeBanner);
        caregiverViewElderly = findViewById(R.id.caregiverViewElderly);

        userID = user.getUid();

        DocumentReference docRef = fStore.collection("Caregiver").document(userID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                caretaker = documentSnapshot.toObject(Caretaker.class);
                welcomeBanner.setText(caretaker.getFullName());
            }
        });

        myDialog = new Dialog(this);

    }

    public void ShowPopup(View v) {
        TextView closeBut, pairupMessage;
        EditText pairUpId;
        Button pairUpBut;

        myDialog.setContentView(R.layout.activity_pop_up_window);

        closeBut = (TextView) myDialog.findViewById(R.id.closePopupButton);
        pairUpBut = myDialog.findViewById(R.id.pairupButton);
        pairUpId = myDialog.findViewById(R.id.pairupId);
        pairupMessage = myDialog.findViewById(R.id.popupMessage);

        closeBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        pairUpBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String elderId = pairUpId.getText().toString().trim();
                if (TextUtils.isEmpty(elderId))
                {
                    pairUpId.setError("Please do not leave it blank");
                    return;
                }

                DocumentReference docRef = fStore.collection("Elderly").document(elderId);
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess (DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {

                                pairUpId.setText("");
                                pairupMessage.setText("Pair up successfully! ");

                                Elderly elderly = documentSnapshot.toObject(Elderly.class);
                                List<Elderly> elderlyList;
                                elderlyList = new ArrayList<Elderly>();
                                elderlyList.add(elderly);
                                elderlyList = caretaker.returnElderly();

                                caregiverViewElderly.setText("Elderly name: " + elderly.getFullName() + "\n" +
                                        "Elderly phone: " + elderly.phoneNumber + "\n" +
                                                "Elderly address: " + elderly.getAddress() + "\n"
                                );
                                fStore.collection("Caregiver").document(userID)
                                        .set(caretaker)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("TAG","onSuccess: Caregiver user profile created for " + userID);
                                            }
                                        });



                            } else {
                                Log.d("TAG", "No such document");
                                pairupMessage.setText("Not found!");
                                pairUpId.setText("");
                            }
                        }
                    });


            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }



}