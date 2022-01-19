package com.example.pfdhelpinghand;

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
import android.service.controls.actions.BooleanAction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class CaregiverMainActivity extends AppCompatActivity {

    // here
    TextView caregiverTest, caregiverViewElderly, caregiverTest2;
    FirebaseUser user;
    FirebaseFirestore fStore;
    Caretaker caretaker;
    Dialog myDialog;
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

        // Initialise a new firestore instance
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


        myDialog = new Dialog(this);


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
                                    //caregiverTest2.setText(Integer.toString(size));


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

                                Elderly elderly = documentSnapshot.toObject(Elderly.class);

                                //Check if the elderly object has already been added
                                Boolean isContain = false;
                                for (String e : elderlyIDList)
                                {
                                    if (e.equals(elderId))
                                    {
                                        isContain = true;
                                    }
                                }
                                if (!isContain)
                                {
                                    elderlyIDList.add(elderId);
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
                                fStore.collection("Elderly").document(elderId)
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




    private Runnable mRefreshPage = new Runnable() {
        public void run() {
            // do what you need to do here after the delay
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        }
    };



}