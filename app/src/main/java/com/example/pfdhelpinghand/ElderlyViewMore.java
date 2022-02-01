package com.example.pfdhelpinghand;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ElderlyViewMore extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    Elderly elderly;
    Caretaker caretaker;

    SharedPreferences sharedPreferences;
    String userID;
    String elderlyID;

    ArrayList<EmergencyPerson> emerList;
    ArrayList<String> elderlyArrayList;
    ArrayList<String> caretakerList;

    TextView elderlyName, elderlyPhone, elderlyEmail,
            elderlyAddress, emergencyName, emergencyPhone,
            elderlyPScore;
    Button medReport, apptReport, delete;
    ImageButton callBut;
    Integer requestCodeInt = 2;
    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_view_more);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        userID = user.getUid();



        sharedPreferences = getSharedPreferences("CaretakerValues", Context.MODE_PRIVATE);
        elderlyID = sharedPreferences.getString("elderlyID", "Elderly");

        //Set textviews
        elderlyName = findViewById(R.id.viewMore_elderlyName);
        elderlyPhone = findViewById(R.id.viewMore_elderlyPhone);
        elderlyEmail = findViewById(R.id.viewMore_elderlyEmail);
        elderlyAddress = findViewById(R.id.viewMore_elderlyAddress);
        emergencyName = findViewById(R.id.viewMore_emergencyName);
        emergencyPhone = findViewById(R.id.viewMore_emergencyPhone);
        elderlyPScore = findViewById(R.id.viewMore_elderlyPScore);

        //Set buttons
        medReport = findViewById(R.id.viewMore_medReport);
        apptReport = findViewById(R.id.viewMore_apptReport);
        delete = findViewById(R.id.viewMore_deleteButton);

        //Set image button
        callBut = findViewById(R.id.viewMore_emergencyPersonCall);

        callBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhonecall(phoneNumber);
            }
        });

        //Get elderly object
        fStore.collection("Elderly").document(elderlyID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                elderly = documentSnapshot.toObject(Elderly.class);
                elderlyName.setText(elderly.getFullName());
                elderlyPhone.setText(elderly.getPhoneNumber());
                elderlyEmail.setText(elderly.getEmail());
                elderlyPScore.setText(String.valueOf(elderly.getP_score()));

                String address = elderly.getAddress();

                String address2 = address.replaceAll("\\s+","");
                String[] str = address2.split("[,]", 0);
                String latString = str[0];
                String lngString = str[1];
                double lat = Double.parseDouble(latString);
                double lng = Double.parseDouble(lngString);

                Geocoder geocoder = new Geocoder(ElderlyViewMore.this);
                try {
                    List<Address> addressList = geocoder.getFromLocation(lat, lng, 1);
                    String addressLine = addressList.get(0).getAddressLine(0);
                    elderlyAddress.setText(addressLine);
                }
                catch (Exception e)
                {
                    elderlyAddress.setText(elderly.getAddress());
                }



                caretakerList = elderly.getCaretakerList();

                emerList = elderly.getEmergencyPerson();
                emergencyName.setText(emerList.get(0).getFullName());
                emergencyPhone.setText(emerList.get(0).getPhoneNumber());

                phoneNumber = emerList.get(0).getPhoneNumber();

            }
        });

        //Get caretaker object
        fStore.collection("Caregiver").document(userID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                caretaker = documentSnapshot.toObject(Caretaker.class);
                elderlyArrayList = caretaker.getElderlyList();

            }
        });




        Button backBtn = findViewById(R.id.viewMore_backBut);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent navigate = new Intent(ElderlyViewMore.this, CaregiverMainActivity.class);
                    startActivity(navigate);

            }
        });

        medReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navigate = new Intent(ElderlyViewMore.this, ElderlyMedicationReportActivity.class);
                navigate.putExtra("elderlyID", elderlyID);
                startActivity(navigate);
            }
        });

        apptReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navigate = new Intent(ElderlyViewMore.this, ElderlyAppointmentReportActivity.class);
                navigate.putExtra("elderlyID", elderlyID);
                startActivity(navigate);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(v.getContext())
                        .setTitle("Delete")
                        .setMessage("Do you want to delete this elderly account paired??")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                caretakerList.remove(userID);
                                elderlyArrayList.remove(elderlyID);

                                DocumentReference docRef = fStore.collection("Elderly").document(elderlyID);
                                DocumentReference docRef2 = fStore.collection("Caregiver").document(userID);
                                docRef.update("caretakerList", caretakerList)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                docRef2.update("elderlyList", elderlyArrayList)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Log.d("TAG","onSuccess: Elderly properly removed");
                                                                v.getContext().startActivity(new Intent(v.getContext(), CaregiverMainActivity.class));
                                                            }

                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.e("TAG", "onFailure: " + e.toString());
                                                    }
                                                });
                                            }

                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("TAG", "onFailure: " + e.toString());
                                    }
                                });


                            }})
                        .setNegativeButton(android.R.string.no, null).show();

            }
        });

    } // end of onCreate



    public void makePhonecall(String phoneNum)
    {
        if (phoneNum != null)
        {
            if (ContextCompat.checkSelfPermission(ElderlyViewMore.this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions((Activity) ElderlyViewMore.this, new String[] {Manifest.permission.CALL_PHONE} , requestCodeInt);
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
            Toast.makeText(ElderlyViewMore.this, "NO phone number set!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == requestCodeInt)
        {
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                makePhonecall(phoneNumber);
            }
        }
    }
}