package com.example.pfdhelpinghand;
//ernest

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    Elderly elderly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button cancelButton = findViewById(R.id.cancelButton);

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

                Toast.makeText(getApplicationContext(), elderly.getFullName(), Toast.LENGTH_LONG).show();
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
                Intent navigateToMedAlarmPage = new Intent(MainActivity.this, MedicationAlarmActivity.class);
                startActivity(navigateToMedAlarmPage);
            }

        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.cancelButton:
                        Toast.makeText(getApplicationContext(), "Restarting to cancel SOS functions", Toast.LENGTH_SHORT).show();
                        MagicAppRestart.doRestart(MainActivity.this);
                }
            }
        });


        Button sosButton = findViewById(R.id.sosButton);
        TextView sosText = findViewById(R.id.sosText);
        sosButton.setOnClickListener(new View.OnClickListener() {
                                         public void onClick(View view) {
                                             sosButton.setClickable(false);
                                             new CountDownTimer(9000, 1000) {

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
                                                     Toast.makeText(getApplicationContext(), "Calling person, please wait", Toast.LENGTH_SHORT).show();

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
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
                try {
                    if (ActivityCompat.checkSelfPermission( getApplicationContext(), mPermission)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{mPermission},
                                2);
                        // If any permission above not allowed by user, this condition will
                        // execute every time, else your else part will work
                    } else {
                        Intent navigateToLocationPage = new Intent(MainActivity.this, MapsActivity.class);
                        startActivity(navigateToLocationPage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }



/*
               if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                   return;
                }
                LocationManager locationManager = (LocationManager)
                        getSystemService(Context.LOCATION_SERVICE);

                Criteria criteria = new Criteria();
                criteria.setPowerRequirement(Criteria.POWER_LOW);
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setSpeedRequired(true);
                criteria.setAltitudeRequired(false);
                criteria.setBearingRequired(false);
                criteria.setCostAllowed(false);
                String provider = locationManager.getBestProvider(criteria, true);

                Location location = locationManager.getLastKnownLocation(provider);
                String longitude = "Longitude: " + location.getLongitude();
                Log.v("longitude", longitude);
                String latitude = "Latitude: " + location.getLatitude();
                Log.v("latitude", latitude); */

               /* LocationManager locationManager = (LocationManager)
                        getSystemService(Context.LOCATION_SERVICE);

                LocationListener locationListener = new MyLocationListener(getApplicationContext());
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions((Activity) v.getContext(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);



                   /// return;
                } else {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
                }*/




//
            }
        });



    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    private class MyLocationListener extends Service implements LocationListener {

        private final Context mContext;
        // flag for GPS status
        boolean isGPSEnabled = false;

        // flag for network status
        boolean isNetworkEnabled = false;

        // flag for GPS status
        boolean canGetLocation = false;

        Location location; // location
        double latitude; // latitude
        double longitude; // longitude

        // The minimum distance to change Updates in meters
        private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

        // The minimum time between updates in milliseconds
        private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

        // Declaring a Location Manager
        protected LocationManager locationManager;

        private static final int REQUEST_CODE_PERMISSION = 2;
        String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;


        public MyLocationListener(Context context) {
            this.mContext = context;
            getLocation();
        }

        @SuppressLint("MissingPermission")
        public void getLocation() {
            try {
                locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

                // getting GPS status
                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                // getting network status
                isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGPSEnabled && !isNetworkEnabled) {
                    // no network provider is enabled
                } else {
                    this.canGetLocation = true;
                    // First get location from Network Provider
                    if (isNetworkEnabled) {
                   /* if (ActivityCompat.checkSelfPermission(this,
                   //         Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    // && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        //return ;
                         Already check in the launching
                    } */

                        try {
                            if (ActivityCompat.checkSelfPermission(this, mPermission)
                                    != PackageManager.PERMISSION_GRANTED) {

                                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                                // If any permission above not allowed by user, this condition will
                                // execute every time, else your else part will work
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Log.d("Network", "Network");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }

                    // if GPS Enabled get lat/long using GPS Services
                    if (isGPSEnabled) {
                        if (location == null) {
                            assert locationManager != null;
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                            Log.d("GPS Enabled", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onLocationChanged(@NonNull Location location) {

            /*String longitude = "Longitude: " + location.getLongitude();
            Log.v("longitude", longitude);
            String latitude = "Latitude: " + location.getLatitude();
            Log.v("latitude", latitude);

            //String uri = "geo:"+location.getLatitude()+","+location.getLongitude()+"?z=14.75";
            String uri = "geo:0,0?q=Singapore 669614"; //placeholder
            Log.v("uri", uri);
            Uri gmmIntentUri =
                    Uri.parse(uri);

            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);*/
        }

        /**
         * Stop using GPS listener
         * Calling this function will stop using GPS in your app
         * */

        public void stopUsingGPS() {
            if (locationManager != null) {
          /*  if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                //return;
                // Already check in the launching
            }*/
                locationManager.removeUpdates(MyLocationListener.this);
            }
        }

        /**
         * Function to get latitude
         * */

        public double getLatitude(){
            if(location != null){
                latitude = location.getLatitude();
            }

            // return latitude
            return latitude;
        }

        /**
         * Function to get longitude
         * */

        public double getLongitude(){
            if(location != null){
                longitude = location.getLongitude();
            }

            // return longitude
            return longitude;
        }

        /**
         * Function to check GPS/wifi enabled
         * @return boolean
         * */

        public boolean canGetLocation() {
            return this.canGetLocation;
        }

        @Override
        public void onLocationChanged(@NonNull List<Location> locations) {

        }

        @Override
        public void onFlushComplete(int requestCode) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {

        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }
}
