package com.example.pfdhelpinghand;
//ernest

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity  {
    public static final int DEFAULT_UPDATE_INTERVAL = 5;
    private static final int PERMISSIONS_FINE_LOCATION = 99;

    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    Elderly elderly;
    Dialog pairUpDialog;

    ArrayList<PairUpRequest> requests = new ArrayList<PairUpRequest>();
    ArrayList<EmergencyPerson> emergencyPeople;
    String currentLocation;

    TextView test1, test2, test3, test4;

    String soundUrl = "https://www.youtube.com/watch?v=4YKpBYo61Cs";

    //Main component for location tracking
    FusedLocationProviderClient fusedLocationProviderClient;

    //A config file for all setting related to FuredLocationProviderClient
    LocationRequest locationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button cancelButton = findViewById(R.id.cancelButton);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();

        //Testing location tracking
        test1 = findViewById(R.id.elderlyTest1);
        test2 = findViewById(R.id.elderlyTest2);
        test3 = findViewById(R.id.elderlyTest3);
        test4 = findViewById(R.id.elderlyTest4);

        //Set all properties for LocationRequest
        locationRequest = new LocationRequest();

        //How long do we want to update the location?
        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(5000);

        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);



        String userID = user.getUid();

        FloatingActionButton viewPairUp = findViewById(R.id.floatingButtonElderly);
        viewPairUp.setVisibility(View.INVISIBLE);


        DocumentReference docRef = fStore.collection("Elderly").document(userID);

        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_MEDIA).build());
        try {
            mediaPlayer.setDataSource(soundUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
            Toast.makeText(getApplicationContext(), "Prepare completed", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }


        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                elderly = documentSnapshot.toObject(Elderly.class);
                emergencyPeople =  elderly.getEmergencyPerson();

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
                        Toast.makeText(getApplicationContext(), "Cancelling SOS", Toast.LENGTH_SHORT).show();
                        //MagicAppRestart.doRestart(MainActivity.this);
                        mHandler.postDelayed(mRefreshPage, 0);
                }
            }});
        TextView sosOverlay = findViewById(R.id.sosButtonOverlay);
        ToggleButton sosToggle = findViewById(R.id.toggleButton);
        Button sosButton = findViewById(R.id.sosButton);
        TextView sosText = findViewById(R.id.sosText);
        int[] seconds = {0};
        sosToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    seconds[0] = 0;
                    //sosOverlay.setVisibility(View.INVISIBLE);
                    mHandler.postDelayed(mRefreshPage, 0);
                }
                else
                    seconds[0] = 5000;
                    sosOverlay.setVisibility(View.VISIBLE);
                    sosText.setTextColor(Color.BLACK);
                }
        });
        sosButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sosButton.setClickable(false);
                mediaPlayer.start();
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
                        //Toast.makeText(getApplicationContext(), "Calling "+number+", please wait", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), emergencyPeople.get(0).getPhoneNumber(), Toast.LENGTH_SHORT).show();



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





    updateGPS();


    }// end of on create method

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    updateGPS();
                }
                else
                {
                    Toast.makeText(this, "This app required permission to be granted for location", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    //Location tracking
    private void updateGPS()
    {
        //https://www.youtube.com/watch?v=_CdZ3xURK-c&ab_channel=ProgrammingwithProfessorSluiter
        //get permission from the user to track GPS
        // get the current location from the fused client
        //update the UI

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
          //user provides the permission
              fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                  @Override
                  public void onSuccess(Location location) {
                    // we got permission
                      test1.setText("Lat: " + String.valueOf(location.getLatitude()));
                      test2.setText("Long: " + location.getLongitude());
                      test3.setText("Accuracy: " + location.getAccuracy());




                  }
              });
        }
        else{
            // permission not given yet.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }

        }

    }




    // disable the backbutton after logged in
    @Override
    public void onBackPressed() {
    }

    // logout function moved to settings menu
    //public void logout(View view) {
        //FirebaseAuth.getInstance().signOut();
        //startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        //finish();
    //}

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
