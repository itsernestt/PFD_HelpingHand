package com.example.pfdhelpinghand;
//ernest

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String phoneNumber = user.getPhoneNumber();

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

        Button cancelButton = findViewById(R.id.cancelButton);
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
                        callIntent.setData(Uri.parse(phoneNumber));
                        //startActivity(callIntent);
                        Toast.makeText(getApplicationContext(), "Calling person, please wait", Toast.LENGTH_SHORT).show();

                    }

                }.start();


                switch (view.getId()){
                    case R.id.sosButton:
                        Toast.makeText(getApplicationContext(), "SOS Button activated", Toast.LENGTH_SHORT).show();

                }}});}}