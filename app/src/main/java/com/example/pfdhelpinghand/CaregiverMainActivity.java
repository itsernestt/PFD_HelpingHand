package com.example.pfdhelpinghand;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CaregiverMainActivity extends AppCompatActivity {

    TextView caregiverTest, caregiverViewElderly, caregiverTest2;
    FirebaseUser user;
    FirebaseFirestore fStore;
    Caretaker caretaker;
    Dialog myDialog;
    Dialog myDialog2;

    String userID;
    ArrayList<String> elderlyIDList;
    ArrayList<Elderly> elderlyList;
    Elderly elderly;
    SharedPreferences sharedPreferences;

    //Float button animation
    FloatingActionButton addBut, settingBut, pairupBut;
    Animation openAni, closeAni, rotateForward, rotateBackward;
    boolean isOpen = false;

    //For RecyclerView
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_main);

        sharedPreferences = getSharedPreferences("CaretakerValues", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("elderlyID", "");
        editor.commit();

        // Initialise a new fire store instance
        fStore = FirebaseFirestore.getInstance();

        // Get the current user if he/she has already signed in
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        // initialise all the lists
        elderlyIDList = new ArrayList<String>();
        elderlyList = new ArrayList<Elderly>();

        // Main components on the page

        caregiverTest = findViewById(R.id.caregiverTest);
        caregiverViewElderly = findViewById(R.id.caregiverViewElderly);
        addBut = findViewById(R.id.caregiver_addBut);
        settingBut = findViewById(R.id.caregiver_settingBut);
        pairupBut = findViewById(R.id.caregiver_pairupBut);
        caregiverTest2 = findViewById(R.id.caregiverTest2);

        // Animation
        openAni = AnimationUtils.loadAnimation(this, R.anim.floatbut_open);
        closeAni = AnimationUtils.loadAnimation(this, R.anim.floatbut_close);
        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        myDialog = new Dialog(this);
        myDialog2 = new Dialog(this);

        addBut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                animation();
            }
        });

        settingBut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(CaregiverMainActivity.this, CaregiverSetting.class));
            }
        });

        //For Recycler View Adapter
        recyclerView = findViewById(R.id.elderlyRecyclerView);

        getCaretakerInfo();



    }
    //Avoid going backwards
    @Override
    public void onBackPressed() {
    }

    public void getCaretakerInfo()
    {
        fStore.collection("Caregiver").document(userID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                caretaker = task.getResult().toObject(Caretaker.class);

                elderlyIDList = caretaker.getElderlyList();

                // Title bar

                getSupportActionBar().setTitle("Welcome, " + caretaker.getFullName());


                if (elderlyIDList.size() >= 1)
                {
                    fStore.collection("Elderly")
                            .whereIn("id", elderlyIDList)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value,
                                                    @Nullable FirebaseFirestoreException e) {


                                    for (DocumentChange dc: value.getDocumentChanges())
                                    {

                                        QueryDocumentSnapshot queryDocumentSnapshot = dc.getDocument();

                                        Integer old_index = dc.getOldIndex();
                                        Integer new_index = dc.getNewIndex();

                                        //caregiverTest.setText("Old index: " + old_index + "//" + new_index);

                                        elderly = queryDocumentSnapshot.toObject(Elderly.class);

                                        switch (dc.getType())
                                        {
                                            case ADDED:
                                                elderlyList.add(elderly);
                                                break;
                                            case MODIFIED:
                                                elderlyList.set(new_index, elderly);
                                                break;
                                        }
                                    }



                                    ElderlyRecyclerAdapter eAdapter = new ElderlyRecyclerAdapter(elderlyList);
                                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                                    recyclerView.setLayoutManager(layoutManager);
                                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                                    recyclerView.setAdapter(eAdapter);
                                    eAdapter.notifyDataSetChanged();
                                    Integer size = elderlyList.size();




                                }
                            });
                }
            }
        });

    }


    //Testing!!
    public void addElderlyRecord()
    {
        ArrayList<EmergencyPerson> ePerson = new ArrayList<EmergencyPerson>();
        ePerson.add(new EmergencyPerson("Chance123", "98284455"));
        elderlyList.add(new Elderly("12345", "Chen Han", "c@gmail.com","982955865",
                "12345678", "Clementi", "Clementi mall", ePerson,
                new ArrayList<Medication>(), new ArrayList<Appointment>(), new ArrayList<String>()));

    }

    // For the floating button
    private void animation(){
        if (isOpen)
        {
            addBut.startAnimation(rotateForward);
            settingBut.startAnimation(closeAni);
            pairupBut.startAnimation(closeAni);
            settingBut.setClickable(false);
            pairupBut.setClickable(false);
            isOpen=false;

        }
        else{
            addBut.startAnimation(rotateBackward);
            settingBut.startAnimation(openAni);
            pairupBut.startAnimation(openAni);
            settingBut.setClickable(true);
            pairupBut.setClickable(true);
            isOpen=true;
        }
    }



    private Handler mHandler = new Handler();

    public void ShowPopup2(View v)
    {

        TextView closeBut2, pairUpHistory;

        myDialog2.setContentView(R.layout.activity_pop_up_window2);
        myDialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog2.show();

        closeBut2 = (TextView) myDialog2.findViewById(R.id.closePopupButton2);
        pairUpHistory = myDialog2.findViewById(R.id.pairUpHIstory);


        closeBut2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog2.dismiss();
            }
        });


        fStore.collection("PairingRequest")
                .whereEqualTo("senderEmail", caretaker.getEmail())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(TAG, "Listen failed.", e);
                                return;
                            }
                            pairUpHistory.setText("");

                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                if (doc.get("receiverEmail") != null) {
                                    pairUpHistory.setText(pairUpHistory.getText() + "Receiver email: " + doc.getString("receiverEmail") + "\n");
                                    if (doc.getBoolean("isPairUpSuccess")) {
                                        pairUpHistory.setText(pairUpHistory.getText() + "Status: Completed pairing");
                                    }
                                    else {
                                        pairUpHistory.setText(pairUpHistory.getText() + "    Status: Pending...");
                                    }
                                    pairUpHistory.setText(pairUpHistory.getText() + "\n\n");
                                }
                            }


                        }



                });





    }


    public void ShowPopup(View v) {
        TextView closeBut, foundMessage, pairupMessage;
        ProgressBar progressBar;
        EditText pairupEmail;
        Button pairUpBut, pairUpHistoryBut;


        myDialog.setContentView(R.layout.activity_pop_up_window);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();


        closeBut = (TextView) myDialog.findViewById(R.id.closePopupButton);

        pairUpBut = myDialog.findViewById(R.id.pairupButton);
        pairUpHistoryBut = myDialog.findViewById(R.id.viewPairUpHistoryBut);

        pairupEmail = myDialog.findViewById(R.id.pairupEmail);
        foundMessage = myDialog.findViewById(R.id.elderlyFoundMessage);
        pairupMessage = myDialog.findViewById(R.id.elderlyPairupMessage);
        progressBar = myDialog.findViewById(R.id.pairupProgressbar);


        closeBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        pairUpHistoryBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowPopup2(v);
            }
        });





        pairUpBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String elderlyEmail = pairupEmail.getText().toString().trim();
                if (TextUtils.isEmpty(elderlyEmail)) {
                    pairupEmail.setError("Please do not leave it blank");
                    return;
                }

                // Searching on the elderly document by inputting the elderlyID
                fStore.collection("Elderly")
                        .whereEqualTo("email", elderlyEmail)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {

                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        Elderly elderlyPaired = document.toObject(Elderly.class);
                                        ArrayList<String> caregiverList = elderlyPaired.getCaretakerList();
                                        String elderlyPairedID = elderlyPaired.getID();

                                        if (caregiverList.contains(userID))
                                        {

                                            pairupEmail.setError("This account has already connected");
                                            return;
                                        }
                                        else {

                                            pairupEmail.setText("");
                                            foundMessage.setText("Account found! ");



                                            // waiting for elderly to confirm the pairing
                                            progressBar.setVisibility(View.VISIBLE);
                                            pairupMessage.setVisibility(View.VISIBLE);

                                            Map<String, Object> pairingRequest = new HashMap<>();
                                            pairingRequest.put("senderEmail", caretaker.getEmail());
                                            pairingRequest.put("receiverEmail", elderlyPaired.getEmail());
                                            pairingRequest.put("isPairUpSuccess", false);


                                            fStore.collection("PairingRequest")
                                                    .add(pairingRequest)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error adding document", e);
                                                        }
                                                    });

                                            fStore.collection("PairingRequest")
                                                    .document(userID)
                                                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                                            @Nullable FirebaseFirestoreException e) {
                                                            if (e != null) {
                                                                Log.w(TAG, "listen:error", e);
                                                                return;
                                                            }

                                                            if (snapshot != null && snapshot.exists()) {
                                                                Boolean pairedUp = snapshot.getBoolean("isPairUpSuccess");
                                                                if (pairedUp)
                                                                {

                                                                    pairupMessage.setText("Paired up successfully!");
                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                    //1.  If found, add the elderly id into the list
                                                                    caretaker.assignElderly(elderlyPairedID);
                                                                    elderlyIDList = caretaker.getElderlyList();

                                                                    // 2. Update the caregiverList at the elderly side
                                                                    elderlyPaired.addCaretaker(userID);


                                                                    // 4. Updates the elderly on the fire store
                                                                    fStore.collection("Elderly").document(elderlyPairedID)
                                                                            .set(elderlyPaired)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    Log.d("TAG", "onSuccess: Caregiver user updated for" + userID);
                                                                                }
                                                                            });

                                                                    // Updates the caregiver on the firestore
                                                                    fStore.collection("Caregiver").document(userID)
                                                                            .set(caretaker)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    Log.d("TAG", "onSuccess: Caregiver user updated for" + userID);

                                                                                    // Add a two second delay before refreshing the page just to show the connection result
                                                                                    mHandler.postDelayed(mRefreshPage, 2500);
                                                                                }
                                                                            });
                                                                }
                                                            } else {
                                                                Log.d(TAG, "Current data: null");
                                                            }


                                                        }
                                                    });
                                        }

                                    }
                                }

                            }
                        });
            }
        });




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