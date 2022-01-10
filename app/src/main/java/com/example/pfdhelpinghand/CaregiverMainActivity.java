package com.example.pfdhelpinghand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.os.Handler;
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
    TextView welcomeBanner, caregiverViewElderly, caregiverTest;
    FirebaseUser user;
    FirebaseFirestore fStore;
    Caretaker caretaker;
    Dialog myDialog;
    String userID;
    ArrayList<Elderly> elderlyList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_main);
        // Initialise a new firestore instance
        fStore = FirebaseFirestore.getInstance();

        // Get the current user if he/she has already signed in
        user = FirebaseAuth.getInstance().getCurrentUser();
        elderlyList = new ArrayList<Elderly>();

        welcomeBanner = findViewById(R.id.welcomeBanner);
        caregiverViewElderly = findViewById(R.id.caregiverViewElderly);
        caregiverTest = findViewById(R.id.caregiverTest);

        userID = user.getUid();

        DocumentReference docRef = fStore.collection("Caregiver").document(userID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                caretaker = documentSnapshot.toObject(Caretaker.class);
                welcomeBanner.setText(caretaker.getFullName());
                elderlyList = caretaker.getElderlyList();

                if (elderlyList.size() >= 1)
                {
                    for (Elderly e : elderlyList)
                    {
                        caregiverViewElderly.setText(caregiverViewElderly.getText() + "\n" + e.getFullName());
                    }
                }
                else
                {
                    caregiverViewElderly.setText("No elderly account linked, please use the pair up button");
                }



            }
        });

        myDialog = new Dialog(this);

    }

    private Handler mHandler = new Handler();
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

                // Searching on the elderly document by inputting the elderlyID
                DocumentReference docRef = fStore.collection("Elderly").document(elderId);
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess (DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                //1. Update the elderlyList in the caregiver side
                                // reset the pairup text input
                                pairUpId.setText("");
                                pairupMessage.setText("Pair up successfully! ");

                                //Initialise a new elderly object
                                Elderly elderly = documentSnapshot.toObject(Elderly.class);

                                //Check if the elderly object has already been added
                                Boolean isContain = false;
                                String elderlyID = elderly.getID();
                                for (Elderly e : elderlyList)
                                {
                                    if (e.getID().equals(elderlyID))
                                    {
                                        isContain = true;
                                    }
                                }
                                if (!isContain)
                                {
                                    elderlyList.add(elderly);
                                }
                                else{
                                    pairupMessage.setText("The elderly has already been added!");
                                }

                                // 2. Update the caregiverList at the elderly side
                                Boolean isContainCaregiver = false;
                                String caregiverID = caretaker.getID();
                                ArrayList<String> caregiverList = elderly.getCaretakerList();
                                for (String c : caregiverList)
                                {
                                    if (c.equals(caregiverID))
                                    {
                                        isContainCaregiver = true;
                                    }
                                }
                                if (!isContainCaregiver)
                                {
                                    elderly.addCaretaker(caregiverID);

                                }
                                else{
                                    pairupMessage.setText(pairupMessage.getText() + "\n" +
                                                        "The care-taker has been updated on the elderly too!");
                                }


                                // Updates the elderly on the firestore
                                fStore.collection("Elderly").document(elderlyID)
                                        .set(elderly)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("TAG","onSuccess: Caregiver user updated for" + userID);

                                            }
                                        });

                                // Updates the caregiver on the firestore
                                fStore.collection("Caregiver").document(userID)
                                        .set(caretaker)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("TAG","onSuccess: Caregiver user updated for" + userID);

                                                // Add a two second delay before refreshing the page just to show the connection result
                                                mHandler.postDelayed(mRefreshPage, 2000);
                                            }
                                        });



                            } else {
                                Log.d("TAG", "No such document");
                                pairupMessage.setText("Not found!");
                                pairUpId.setText("");
                            };


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


    private Runnable mRefreshPage = new Runnable() {
        public void run() {
            // do what you need to do here after the delay
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        }
    };



}