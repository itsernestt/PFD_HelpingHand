package com.example.pfdhelpinghand;
//ernest

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

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
                Intent navigateToMedAlarmPage = new Intent(MainActivity.this,MedicationAlarmActivity.class);
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



        Button sosButton = findViewById(R.id.sosButton);
        TextView sosText = findViewById(R.id.sosText);
        sosButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sosButton.setClickable(false);
                new CountDownTimer(9000, 1000) {

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

    }
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
}
