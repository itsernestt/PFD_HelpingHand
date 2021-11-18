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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CaregiverMainActivity extends AppCompatActivity {

    // here
    TextView welcomeBanner;
    FirebaseUser user;
    FirebaseFirestore fStore;

    Dialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_main);

        fStore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        welcomeBanner = findViewById(R.id.welcomeBanner);

        String userID = user.getUid();
        DocumentReference docRef = fStore.collection("Caregiver").document(userID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String userName = document.getString("FullName");
                        welcomeBanner.setText("Welcome! " + userName);
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
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
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete (@NonNull Task <DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                pairUpId.setText("");
                                pairupMessage.setText("Pair up successfully! ");

                            } else {
                                Log.d("TAG", "No such document");
                                pairupMessage.setText("Not found!");
                                pairUpId.setText("");
                            }
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