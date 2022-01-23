package com.example.pfdhelpinghand;
//ernest

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    Elderly elderly;
    Dialog pairUpDialog;

    ArrayList<PairUpRequest> requests = new ArrayList<PairUpRequest>();
    TextView welcomeBanner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button cancelButton = findViewById(R.id.cancelButton);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();

        welcomeBanner = findViewById(R.id.elderlyWelcomeBanner);

        String userID = user.getUid();

        FloatingActionButton viewPairUp = findViewById(R.id.floatingButtonElderly);
        viewPairUp.setVisibility(View.INVISIBLE);


        DocumentReference docRef = fStore.collection("Elderly").document(userID);


        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                elderly = documentSnapshot.toObject(Elderly.class);

                fStore.collection("PairingRequest")
                        .whereEqualTo("receiverEmail", elderly.getEmail())
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                                @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    Log.w(TAG, "listen:error", e);
                                    return;
                                }


                                for (DocumentChange dc: queryDocumentSnapshots.getDocumentChanges())
                                {
                                    DocumentSnapshot snapshot = dc.getDocument();
                                    String documentID = snapshot.getId();
                                    String senderEmail = snapshot.getString("senderEmail");
                                    String receiverEmail = snapshot.getString("receiverEmail");
                                    Boolean isPaired = snapshot.getBoolean("isPairUpSuccess");


                                    PairUpRequest request = new PairUpRequest(documentID, senderEmail, receiverEmail, isPaired);

                                    switch (dc.getType()){
                                        case ADDED:
                                        case MODIFIED:
                                            if (!request.getPairUpSuccess())
                                            {
                                                requests.add(request);
                                            }
                                            else{
                                                Iterator<PairUpRequest> itr = requests.iterator();
                                                while (itr.hasNext())
                                                {
                                                    PairUpRequest r = itr.next();
                                                    if (request.getDocumentID().equals(r.getDocumentID()))
                                                    {
                                                        itr.remove();
                                                    }
                                                }
                                            }
                                            welcomeBanner.setText(welcomeBanner.getText() + Integer.toString(requests.size()) + "  ");

                                            if (requests.size() > 0)
                                            {
                                                viewPairUp.setVisibility(View.VISIBLE);

                                            }
                                            else{
                                                viewPairUp.setVisibility(View.INVISIBLE);
                                            }

                                            return;
                                        case REMOVED:
                                            Iterator<PairUpRequest> itr = requests.iterator();
                                            while (itr.hasNext())
                                            {
                                                PairUpRequest r = itr.next();
                                                if (request.getDocumentID().equals(r.getDocumentID()))
                                                {
                                                    itr.remove();
                                                }
                                            }

                                            if (requests.size() > 0)
                                            {
                                                viewPairUp.setVisibility(View.VISIBLE);

                                            }
                                            else{
                                                viewPairUp.setVisibility(View.INVISIBLE);
                                            }

                                            Toast.makeText(MainActivity.this, "Rejected the request for: " + senderEmail, Toast.LENGTH_SHORT).show();
                                            mHandler.postDelayed(mRefreshPage, 1000);




                                    }



                                }






                            }

                });


            }
        });



        Button settingsButton = findViewById(R.id.settingsBtn);
        settingsButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent navigateToSettings = new Intent(MainActivity.this, ElderlySettingsActivity.class);
                startActivity(navigateToSettings);
            }
        });

        Button lostButton = findViewById(R.id.lostButton);
        lostButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent navigateToLostPage = new Intent(MainActivity.this,HelpLostActivity.class);
                startActivity(navigateToLostPage);
            }

        });

        Button medAlarmButton = findViewById(R.id.medAlarmButton);
        medAlarmButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent navigateToMedAlarmPage = new Intent(MainActivity.this, MedicationAppointmentActivity.class);
                startActivity(navigateToMedAlarmPage);
            }

        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.cancelButton:
                        Toast.makeText(getApplicationContext(), "Restarting to cancel SOS functions", Toast.LENGTH_SHORT).show();
                        MagicAppRestart.doRestart(MainActivity.this);
                }
            }});

        ToggleButton sosToggle = findViewById(R.id.toggleButton);
        Button sosButton = findViewById(R.id.sosButton);
        TextView sosText = findViewById(R.id.sosText);
        int[] seconds = {0};
        sosToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    seconds[0] = 0;
                }
                else
                    seconds[0] = 5000;
            }
        });

        sosButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sosButton.setClickable(false);
                new CountDownTimer(seconds[0], 1000) {

                    public void onTick(long millisUntilFinished) {
                        sosText.setText("   " + millisUntilFinished / 1000);
                    }

                    public void onClick(View view){
                        cancel();
                    }

                    public void onFinish() {
                        sosText.setText("SOS");
                        sosButton.setClickable(true);
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        //startActivity(callIntent);
                        Toast.makeText(getApplicationContext(), "Calling person, please wait", Toast.LENGTH_SHORT).show();

                    }

                }.start();


                switch (view.getId()){
                    case R.id.sosButton:
                        Toast.makeText(getApplicationContext(), "SOS Button activated", Toast.LENGTH_SHORT).show();

                }
            }
            public void buttonEffect(View button){
                button.setOnTouchListener(new View.OnTouchListener() {

                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN: {
                                v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                                v.invalidate();
                                break;
                            }
                            case MotionEvent.ACTION_UP: {
                                v.getBackground().clearColorFilter();
                                v.invalidate();
                                break;
                            }
                        }
                        return false;
                    }
                });
            }
        }

        );

//        public static class Post {
//            public String address;
//        }
//
//        final FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference ref = database.getReference("");
//
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Post post = dataSnapshot.getValue(Post.class);
//                System.out.printIn(post);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        })

        Button locationButton = findViewById(R.id.locationButton);
        locationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                Uri gmmIntentUri =
                        Uri.parse("geo:0,0?q=535 Clementi R, Singapore 599489");

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);

//
            }
        });


        pairUpDialog = new Dialog(this);

        viewPairUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowPairUpRequest(view);
            }

        });








    }

    // disable the backbutton after logged in
    @Override
    public void onBackPressed() {
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    // For elderly to view incoming requests
    public void ShowPairUpRequest(View v)
    {

        TextView closePairUpDialog;
        RecyclerView recyclerView;


        pairUpDialog.setContentView(R.layout.activity_pop_up_window3);



        closePairUpDialog = (TextView) pairUpDialog.findViewById(R.id.closePairingPopup);
        recyclerView = (RecyclerView) pairUpDialog.findViewById(R.id.pairUpRequestRecyclerView);


        closePairUpDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairUpDialog.dismiss();
            }
        });


        ElderlyPairingRequestAdapter adapter = new ElderlyPairingRequestAdapter(requests);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(pairUpDialog.getContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

        pairUpDialog.show();


    }

    //Testing!!!
    public ArrayList<Elderly> addElderlyRecord()
    {
        ArrayList<EmergencyPerson> ePerson = new ArrayList<EmergencyPerson>();
        ePerson.add(new EmergencyPerson("Chance123", "98284455"));
        ArrayList<Elderly> eList = new ArrayList<Elderly>();
        eList.add(new Elderly("12345", "Chen Han", "c@gmail.com","982955865",
                "12345678", "Clementi", "Clementi mall", ePerson,
                new ArrayList<Medication>(), new ArrayList<Appointment>(), new ArrayList<String>()));
        return eList;
    }

    private Handler mHandler = new Handler();

    private Runnable mRefreshPage = new Runnable() {
        public void run() {
            // do what you need to do here after the delay
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        }
    };

}
