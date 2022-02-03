package com.example.pfdhelpinghand;
//ernest

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements IBaseGpsListener {
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    private static final int PERMISSION_PHONE_CALL = 10;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    Elderly elderly;
    Dialog pairUpDialog;

    ArrayList<PairUpRequest> requests = new ArrayList<PairUpRequest>();
    ArrayList<EmergencyPerson> emergencyPeople;
    ElderlyLocation currentLocation;
    String locationAddress;
    String locLat;
    String locLng;

    String locationURL;
    String addressURL;

    String locationInCoords;
    String addressLat;
    String addressLng;

    String phoneNumber;

    TextView test4;

    Switch trackingSwitch;

    Calendar currentTime = Calendar.getInstance();

    String soundUrl = "https://www.youtube.com/watch?v=4YKpBYo61Cs";

    //Main component for location tracking
    FusedLocationProviderClient fusedLocationProviderClient;

    //A config file for all setting related to FuredLocationProviderClient
    LocationRequest locationRequest;
    LocationCallback locationCallBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        Button cancelButton = findViewById(R.id.cancelButton);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        getAlarms();

        //Testing location tracking
        test4 = findViewById(R.id.elderlyTest4);

        //Set all properties for LocationRequest
        locationRequest = new LocationRequest();

        //How long do we want to update the location?
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(1000);

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        currentLocation = new ElderlyLocation();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


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

        fStore.collection("Elderly").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                getAlarms();
            }
        });

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                elderly = documentSnapshot.toObject(Elderly.class);
                emergencyPeople = elderly.getEmergencyPerson();

                String address = elderly.getAddress();
                String address2 = address.replaceAll("\\s+","");
                String[] str = address2.split("[,]", 0);
                String latString = str[0];
                String lngString = str[1];
                double lat = Double.parseDouble(latString);
                double lng = Double.parseDouble(lngString);


                addressLat = String.valueOf(lat);
                addressLng = String.valueOf(lng);


                addressURL = String.format("geo:%1$f,%2$f", lat, lng);




                //Set up title action bar
                getSupportActionBar().setTitle("Welcome, " + elderly.getFullName());

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


                                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                                    DocumentSnapshot snapshot = dc.getDocument();
                                    String documentID = snapshot.getId();
                                    String senderEmail = snapshot.getString("senderEmail");
                                    String receiverEmail = snapshot.getString("receiverEmail");
                                    Boolean isPaired = snapshot.getBoolean("isPairUpSuccess");


                                    PairUpRequest request = new PairUpRequest(documentID, senderEmail, receiverEmail, isPaired);

                                    switch (dc.getType()) {
                                        case ADDED:
                                        case MODIFIED:
                                            if (!request.getPairUpSuccess()) {
                                                requests.add(request);
                                            } else {
                                                Iterator<PairUpRequest> itr = requests.iterator();
                                                while (itr.hasNext()) {
                                                    PairUpRequest r = itr.next();
                                                    if (request.getDocumentID().equals(r.getDocumentID())) {
                                                        itr.remove();
                                                    }
                                                }
                                            }

                                            if (requests.size() > 0) {
                                                viewPairUp.setVisibility(View.VISIBLE);

                                            } else {
                                                viewPairUp.setVisibility(View.INVISIBLE);
                                            }

                                            return;
                                        case REMOVED:
                                            Iterator<PairUpRequest> itr = requests.iterator();
                                            while (itr.hasNext()) {
                                                PairUpRequest r = itr.next();
                                                if (request.getDocumentID().equals(r.getDocumentID())) {
                                                    itr.remove();
                                                }
                                            }

                                            if (requests.size() > 0) {
                                                viewPairUp.setVisibility(View.VISIBLE);

                                            } else {
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
        settingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent navigateToSettings = new Intent(MainActivity.this, ElderlySettingsActivity.class);
                startActivity(navigateToSettings);
            }
        });

        Button lostButton = findViewById(R.id.lostButton);
        lostButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent navigateToLostPage = new Intent(MainActivity.this, HelpLostActivity.class);
                startActivity(navigateToLostPage);
            }

        });

        Button medAlarmButton = findViewById(R.id.medAlarmButton);
        medAlarmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent navigateToMedAlarmPage = new Intent(MainActivity.this, MedicationAppointmentActivity.class);
                startActivity(navigateToMedAlarmPage);
            }

        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.cancelButton:
                        Toast.makeText(getApplicationContext(), "Cancelling SOS", Toast.LENGTH_SHORT).show();
                        //MagicAppRestart.doRestart(MainActivity.this);
                        mHandler.postDelayed(mRefreshPage, 0);
                }
            }
        });
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
                } else
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

                                                 public void onClick(View view) {
                                                     cancel();
                                                 }

                                                 public void onFinish() {
                                                     sosText.setText("SOS");
                                                     sosButton.setClickable(true);
                                                     Intent callIntent = new Intent(Intent.ACTION_CALL);

                                                     //startActivity(callIntent);
                                                     //Toast.makeText(getApplicationContext(), "Calling "+number+", please wait", Toast.LENGTH_SHORT).show();
                                                     phoneNumber = emergencyPeople.get(0).getPhoneNumber();
                                                     if (sosToggle.isChecked() == true)
                                                     {
                                                         makePhonecall(phoneNumber);
                                                         Toast.makeText(getApplicationContext(), phoneNumber, Toast.LENGTH_SHORT).show();
                                                     }
                                                     else
                                                     {
                                                         sendSMS(phoneNumber);
                                                     }




                                                 }

                                             }.start();


                                             switch (view.getId()) {
                                                 case R.id.sosButton:
                                                     Toast.makeText(getApplicationContext(), "SOS Button activated", Toast.LENGTH_SHORT).show();

                                             }
                                         }

                                         public void buttonEffect(View button) {
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


        Button locationButton = findViewById(R.id.locationButton);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = String.format("http://maps.google.com/maps?saddr=%1$s,%2$s&daddr=%3$s,%4$s&dir_action=navigate", locLat, locLng, addressLat, addressLng);

                Uri gmmIntentUri = Uri.parse(url);


                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);


            }
        });


        pairUpDialog = new Dialog(this);

        viewPairUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowPairUpRequest(view);
            }

        });


/*
        //update the elderly current location every 1 minute
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                UpdateLocation();
            }
        }, 0, 60000*3);//put here time 1000 milliseconds=1 second

 */
//
//
//        new Timer().scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                getAlarms();
//            }
//        }, 0, 1000 * 60 * 5); // every 5 mins

        // Set alarm every 30 seconds...?
//        new Timer().scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        getAlarms();
//                    }
//                });
//            }
//        }, 0, 30000);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);

        } else {
            showLocation();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                !Settings.canDrawOverlays(this)) {
            askForOverlay();
        }


    }// end of on create method

    public void askForOverlay() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Request permission")
                .setMessage("Our application requires your permission to send alerts anywhere. Grant permission?")
                .setIcon(android.R.drawable.star_big_off)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "idk just give us permission pls", Toast.LENGTH_SHORT).show();
                        try {
                            Thread.sleep(3 * 1000);
                            askForOverlay();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showLocation();
            } else {
                Toast.makeText(this, "Permission not granted!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        if (requestCode == PERMISSION_PHONE_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhonecall(phoneNumber);
            }
        }

        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSMS(phoneNumber);
                Toast.makeText(getApplicationContext(), "SMS Sent to " + phoneNumber, Toast.LENGTH_SHORT).show();
            }
        }
    }


    // -----------------FOR LOCATION --------------//
    private void showLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_FINE_LOCATION);
            }
            getLocation();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        } else {
            Toast.makeText(this, "Enable GPS!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onLocationChanged(Location location) {
        //update location

        UpdateLocation(location);

        Geocoder geocoder = new Geocoder(MainActivity.this);
        try {
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            test4.setText(addressList.get(0).getAddressLine(0));
            locationAddress = addressList.get(0).getAddressLine(0);

        } catch (Exception e) {
            test4.setText("Not available");

        }


    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onGpsStatusChanged(int event) {

    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_FINE_LOCATION);
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location loc = task.getResult();
                if (loc != null) {
                    UpdateLocation(loc);
                }
            }
        });
    }



    // Updates the database of the location
    public void UpdateLocation(Location location) {



        LocalDateTime date = LocalDateTime.now();
        int seconds = date.toLocalTime().toSecondOfDay();

        double lat = location.getLatitude();
        double lng = location.getLongitude();

        currentLocation.setLat(lat);
        currentLocation.setLng(lng);

        locLat = String.valueOf(lat);
        locLng = String.valueOf(lng);

        currentLocation.setTime(seconds);
        locationURL = String.format("geo:%1$f,%2$f", lat, lng);

        locationInCoords = String.valueOf(location.getLatitude()) + " , " + String.valueOf(location.getLongitude());


        fStore.collection("Elderly").document(user.getUid())
                .update("currentLocation", locationInCoords)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("TAG", "onSuccess: Location updated");

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TAG", "onFailure: " + e.toString());
            }
        });

        fStore.collection("ElderlyLocationML").document(user.getUid())
                .update("locationList", FieldValue.arrayUnion(currentLocation))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("TAG", "onSuccess: Location updated");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.e("TAG", "onFailure: " + e.toString());
            }
        });
    }


//------------FOR MED AND APPT ALARM ---------------------//

    private void getAlarms(){
        DocumentReference docRef =  fStore.collection("Elderly").document(user.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                elderly = documentSnapshot.toObject(Elderly.class);
                ArrayList<Medication> mList = elderly.getMedList();
                ArrayList<Appointment> aList = elderly.getApptList();
                int counter = 1;
                int counter2 = 100;

                for (Medication m: mList) {
                    Calendar tempCal = Calendar.getInstance();
                    tempCal.setTimeInMillis(m.getDay().toDate().getTime());
                    Calendar currentTime = Calendar.getInstance();
                    float x = currentTime.compareTo(tempCal);
                    if (x<0){
                        setAlarm(counter, tempCal, m.medName);
                    }else if (x>0){
                        mList.remove(m);
                        Collections.sort(mList);
                        docRef.update("medList", mList).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Fail to delete medication!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        cancelAlarm(counter);
                        DocumentReference addtoML = fStore.collection("ElderlyMedicationML").document(user.getUid());
                        addtoML.update("alarmFailed", FieldValue.arrayUnion(m)).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Fail to add ML record!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    counter++;
                }

                for (Appointment a: aList){
                    Calendar tempCal = Calendar.getInstance();
                    tempCal.setTimeInMillis(a.getTime().toDate().getTime());

                    Calendar tempCal3 = Calendar.getInstance();
                    tempCal3.setTimeInMillis(a.getTime().toDate().getTime());
                    tempCal3.add(Calendar.HOUR_OF_DAY, -1);

                    Calendar tempCal2 = Calendar.getInstance();
                    float y = tempCal2.compareTo(tempCal3);
                    float x = tempCal2.compareTo(tempCal);
                    if (x<0 && y<0){
                        setAlarm(counter2, tempCal, a.apptName);
                    }else if (x>0){
                        aList.remove(a);
                        Collections.sort(aList);
                        docRef.update("apptList", aList).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Fail to delete appointment!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        cancelAlarm(counter2);
                    }
                    counter2--;
                }
            }
        });
    }

    private void cancelAlarm(int counter) {
        Intent intent = new Intent(this, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, counter, intent, PendingIntent.FLAG_NO_CREATE);
        AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
        am.cancel(pendingIntent);
    }

    private void setAlarm(int counter, Calendar tempCal, String itemName) {
        if (counter<50){
            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra("alarmid", counter);
            intent.putExtra("medname", itemName);
            PendingIntent pi = PendingIntent.getBroadcast(this, counter, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
            am.setExact(AlarmManager.RTC_WAKEUP, tempCal.getTimeInMillis(), pi);
        }else if (counter>=50){
            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra("alarmid", counter);
            intent.putExtra("apptname", itemName);
            PendingIntent pi = PendingIntent.getBroadcast(this, counter, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            tempCal.add(Calendar.HOUR_OF_DAY, -1);
            AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
            am.setExact(AlarmManager.RTC_WAKEUP, tempCal.getTimeInMillis(), pi);
        }
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "HHAlarmChannel";
            String description = "Channel for Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("HelpingHand", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
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

    private Handler mHandler = new Handler();

    private Runnable mRefreshPage = new Runnable() {
        public void run() {
            // do what you need to do here after the delay
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        }
    };

    //for phone call
    public void makePhonecall(String phoneNum)
    {
        if (phoneNum != null)
        {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions((Activity) MainActivity.this, new String[] {Manifest.permission.CALL_PHONE} , PERMISSION_PHONE_CALL);
            }
            else
            {

                String s = "tel:" + phoneNum;
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(s));
                startActivity(intent);
            }
        }
        else {
            Toast.makeText(MainActivity.this, "NO phone number set!", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendSMS(String phoneNum)
    {
        if (phoneNum != null)
        {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED)
            {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.SEND_SMS))
                {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS},MY_PERMISSIONS_REQUEST_SEND_SMS);
                }
                else
                {
                    //SmsManager smsManager = SmsManager.getDefault();
                    //smsManager.sendTextMessage(phoneNum, null, "Your elderly has activated SOS Button!", null, null);
                    Toast.makeText(getApplicationContext(), "SOS SMS has been sent.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}









