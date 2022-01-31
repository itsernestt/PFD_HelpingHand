package com.example.pfdhelpinghand;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

// Add an import statement for the client library.
import com.google.android.libraries.places.api.Places;



public class SignUpElderlyActivity extends AppCompatActivity {
    EditText eFullName, eEmail, ePhone, ePassword, eContactName, eContactPhone, eAddress;
    Button eRegisterButton, eBackBtn, ePickAddr;
    TextView ePickAddrText;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    ArrayList<EmergencyPerson> eList;
    Elderly elderly;
    String addressInCoords;

    private final static int PLACE_PICKER_REQUEST = 999;
    WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_elderly);

        eList = new ArrayList<EmergencyPerson>();



        eFullName = findViewById(R.id.elderlyName);
        eEmail = findViewById(R.id.elderlyEmail);
        ePhone = findViewById(R.id.elderlyPhone);
        ePassword = findViewById(R.id.elderlyPassword);
        eContactName = findViewById(R.id.elderlyContactName);
        eContactPhone = findViewById(R.id.elderlyContactPhone);

        eRegisterButton = findViewById(R.id.elderlyRegisterButton);
        ePickAddr = findViewById(R.id.elderlyAddressPicker);
        ePickAddrText = findViewById(R.id.elderlyAddressPickerText);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            wifiManager= (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        }

        // Initialize Places.
        Places.initialize(getApplicationContext(), "AIzaSyAmh_CHKyEBR4VW1tscBLu1Sm0BJeK0ipE");

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        ePickAddr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    startActivityForResult(builder.build(SignUpElderlyActivity.this), PLACE_PICKER_REQUEST);

                    //Enable Wifi
                    wifiManager.setWifiEnabled(true);

                } catch (GooglePlayServicesRepairableException e) {
                    Log.d("Exception",e.getMessage());

                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.d("Exception",e.getMessage());

                    e.printStackTrace();
                }



            }
        });


        // Set up the title bar
        getSupportActionBar().setTitle("Elderly Sign Up");

        if (fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }


        eRegisterButton.setOnClickListener(new View.OnClickListener(){
            @Override

            public void onClick(View v) {
                String userFullName = eFullName.getText().toString();
                String email = eEmail.getText().toString().trim();
                String phone = ePhone.getText().toString().trim();
                String password = ePassword.getText().toString().trim();



                String contactName = eContactName.getText().toString();

                String contactPhone = eContactPhone.getText().toString().trim();

                String address = ePickAddrText.getText().toString();


                if (TextUtils.isEmpty(userFullName)) {
                    eFullName.setError("Your full name is required!");
                    return;
                }



                if (TextUtils.isEmpty(email)) {
                    eEmail.setError("Email is required!");
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    ePhone.setError("Your phone number is required!");
                    return;
                }


                if (TextUtils.isEmpty(password)) {
                    ePassword.setError("Password is required!");
                    return;
                }


                if (password.length() < 8) {
                    ePassword.setError("Password must be >= 8 characters");
                    return;
                }



                if (TextUtils.isEmpty(contactName))
                {
                    eContactName.setError("Contact person name is required!");
                    return;
                }
                if (TextUtils.isEmpty(contactPhone))
                {
                    eContactPhone.setError("Contact person phone number is required!");
                    return;
                }
                if (address.equals("Pick your address below!"))
                {
                    ePickAddrText.setError("Address is required!");
                    return;
                }


                //register the user in the firebase
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(SignUpElderlyActivity.this, "Elderly User Created", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();

                            // set the user display name
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(userFullName + "-elderly").build();
                            user.updateProfile(profileUpdates);

                            eList.add(new EmergencyPerson(contactName, contactPhone));
                            Integer p = 100;

                            ArrayList<Medication> mList = new ArrayList<Medication>();

                            elderly = new Elderly(userID, userFullName, email, phone, password, p, addressInCoords,"", eList, mList, new ArrayList<Appointment>(), new ArrayList<String>() );




                            fStore.collection("Elderly").document(userID)
                                    .set(elderly)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("TAG","onSuccess: Elderly user profile created for " + userID);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("TAG", "onFailure: " + e.toString());
                                }
                            });

                            //Initialize ElderlyMedML

                            Map<String, Object> MedML = new HashMap<>();
                            MedML.put("elderlyID", userID);
                            MedML.put("elderlyName", elderly.getFullName());
                            MedML.put("alarmFailed", new ArrayList<Medication>());
                            MedML.put("alarmSuccess", new ArrayList<Medication>());


                            fStore.collection("ElderlyMedicationML").document(userID)
                                    .set(MedML)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("TAG","onSuccess: Elderly Medication ML profile created for " + userID);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("TAG", "onFailure: " + e.toString());
                                }
                            });

                            //Initialize ElderlyAppointmentHistory
                            Map<String, Object> ApptHistory = new HashMap<>();
                            ApptHistory.put("elderlyID", userID);
                            ApptHistory.put("elderlyName", elderly.getFullName());
                            ApptHistory.put("apptList", new ArrayList<Appointment>());

                            fStore.collection("ElderlyAppointmentHistory").document(userID)
                                    .set(ApptHistory)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("TAG","onSuccess: Elderly Appointment History profile created for " + userID);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("TAG", "onFailure: " + e.toString());
                                }
                            });




                            //Initialize ElderlyLocationML
                            Map<String, Object> LocML = new HashMap<>();
                            ArrayList<ElderlyLocation> elderlyLocationArrayList = new ArrayList<ElderlyLocation>();


                            LocML.put("elderlyID", userID);
                            LocML.put("elderlyName", elderly.getFullName());
                            LocML.put("elderlyAddress", addressInCoords);
                            LocML.put("locationList", elderlyLocationArrayList);

                            fStore.collection("ElderlyLocationML").document(userID)
                                    .set(LocML)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("TAG","onSuccess: Elderly Location ML profile created for " + userID);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("TAG", "onFailure: " + e.toString());
                                }
                            });


                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    }
                });


            }

        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PLACE_PICKER_REQUEST:
                    Place place = PlacePicker.getPlace(SignUpElderlyActivity.this, data);

                    double latitude = place.getLatLng().latitude;
                    double longitude = place.getLatLng().longitude;


                    addressInCoords = String.valueOf(latitude) + " , " + String.valueOf(longitude);

                    Geocoder geocoder = new Geocoder(SignUpElderlyActivity.this);
                    try {
                        List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                        ePickAddrText.setText(addressList.get(0).getAddressLine(0));
                    }
                    catch (Exception e)
                    {
                        ePickAddrText.setText("Not available");

                    }


            }
        }
    }

}
