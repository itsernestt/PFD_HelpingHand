package com.example.pfdhelpinghand;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewCaregivers extends AppCompatActivity {
    TextView viewCaregiverInfo;
    FirebaseUser user;
    FirebaseFirestore fStore;
    Elderly elderly;
    ArrayList<String> caretakerList;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_caregivers);

        Button backButton = findViewById(R.id.viewCGBackBtn);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent navigateBack = new Intent(ViewCaregivers.this, ElderlySettingsActivity.class);
                startActivity(navigateBack);
            }
        });

        //Initialise the firebase firestore connection
        user = FirebaseAuth.getInstance().getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        caretakerList = new ArrayList<String>();
        viewCaregiverInfo = findViewById(R.id.viewCaregiverInfo);

        //Get the elderly user from fire store
        userID = user.getUid();
        DocumentReference docRef = fStore.collection("Elderly").document(userID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                elderly = documentSnapshot.toObject(Elderly.class);
                caretakerList = elderly.getCaretakerList();

                if (caretakerList.size() >= 1){
                    viewCaregiverInfo.setText("");
                    fStore.collection("Caregiver")
                            .whereIn("id", caretakerList)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (DocumentSnapshot snapshot: queryDocumentSnapshots)
                                    {
                                        Caretaker caretaker = snapshot.toObject(Caretaker.class);
                                        viewCaregiverInfo.setText(viewCaregiverInfo.getText() + "\n" + caretaker.getFullName() + " " + caretaker.getPhoneNumber());
                                    }
                                }
                            });


                }
            }
        });
    }
}