package com.example.pfdhelpinghand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;

public class CountdownDialog extends AppCompatActivity{

    Button xBtn, goBtn;
    TextView countdownTimerTV, headerTV;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirebaseUser user;
    CountDownTimer cTimer = null;
    Uri notificationa = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    Uri notificationb= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
    MediaPlayer mp1;
    MediaPlayer mp2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_countdown_popup);
        this.setFinishOnTouchOutside(false);
        Intent intent = getIntent();
        goBtn = findViewById(R.id.takeMedBtn);
        countdownTimerTV = findViewById(R.id.countdownTimer);
        headerTV = findViewById(R.id.headerTV);

        mp1 = MediaPlayer.create(getApplicationContext(), notificationa);
        mp2 = MediaPlayer.create(getApplicationContext(), notificationb);

        if (intent.hasExtra("medname")){

            mp1.start();

            headerTV.setText("Time to take medication!");

            cTimer = new CountDownTimer(30000, 1000) {
                public void onTick(long millisUntilFinished) {
                    countdownTimerTV.setText((int) (millisUntilFinished/1000) + " seconds remaining");
                }
                public void onFinish() {
                    try{
                        if (mp1 !=null){
                            if (mp1.isPlaying()){
                                mp1.stop();
                            }
                            mp1.release();
                            mp1 = null;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    deleteMedRecord();
                    cancel();
                    finish();
                }
            };
            cTimer.start();

            goBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cTimer.cancel();

                    try{
                        if (mp1 !=null){
                            if (mp1.isPlaying()){
                                mp1.stop();
                            }
                            mp1.release();
                            mp1 = null;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Context context = CountdownDialog.this;
                    Intent i = new Intent(context, MedicationAppointmentActivity.class);
                    i.putExtra("medname", intent.getStringExtra("medname"));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
            });

        }else if (intent.hasExtra("apptname")){
            headerTV.setText("Appointment in 1 hour!");
            goBtn.setText("Check details");

            mp2.start();

            cTimer = new CountDownTimer(60000, 1000) {
                public void onTick(long millisUntilFinished) {
                    countdownTimerTV.setText((int) (millisUntilFinished/1000) + " seconds remaining");
                }
                public void onFinish() {
                    try{
                        if (mp2 !=null){
                            if (mp2.isPlaying()){
                                mp2.stop();
                            }
                            mp2.release();
                            mp2 = null;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    cTimer.cancel();
                    finish();
                }
            };
            cTimer.start();

            goBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        if (mp2 !=null){
                            if (mp2.isPlaying()){
                                mp2.stop();
                            }
                            mp2.release();
                            mp2 = null;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    cTimer.cancel();
                    Context context = CountdownDialog.this;
                    Intent i = new Intent(context, MedicationAppointmentActivity.class);
                    i.putExtra("apptname", intent.getStringExtra("apptname"));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cTimer = null;
    }



    private void deleteMedRecord(){
        Intent intent = getIntent();
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();



        DocumentReference docRef = fStore.collection("Elderly").document(user.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Elderly currentElderly = documentSnapshot.toObject(Elderly.class);
                ArrayList<Medication> mList = currentElderly.getMedList();
                for (Medication m: mList) {
                    if (m.medName.equals(intent.getStringExtra("medname"))){
                        mList.remove(m);
                        fStore.collection("ElderlyMedicationML").document(user.getUid())
                                .update("alarmFailed", FieldValue.arrayUnion(m));
                    }
                }
                Collections.sort(mList);
                // update the med list in the elderly
                docRef.update("medList", mList)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("TAG", "onFailure: " + e.toString());
                            }
                        });
                // updaste the p_value also
                Integer p = currentElderly.reducePScore();
                docRef.update("p_score", p)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
        });
        finish();
    }
}