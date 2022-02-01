package com.example.pfdhelpinghand;


import static android.content.ContentValues.TAG;

import static androidx.core.content.ContextCompat.getSystemService;
import static androidx.core.content.ContextCompat.startActivity;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class ElderlyRecyclerAdapter extends RecyclerView.Adapter<ElderlyRecyclerAdapter.MyViewHolder>
        implements ActivityCompat.OnRequestPermissionsResultCallback

{
    private ArrayList<Elderly> elderlyArrayList;
    Caretaker caretaker;
    SharedPreferences sharedPreferences;
    FirebaseAuth fAuth= FirebaseAuth.getInstance();;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseUser user= fAuth.getCurrentUser();
    String userID = user.getUid();
    DocumentReference docRef = fStore.collection("Caregiver").document(userID);
    List elderlyList;
    Integer elderlyPScore;
    Integer timeCounter;
    Boolean isLastRecordCancel;
    Context context;
    Integer requestCodeInt = 1;
    String phoneNumber;


    public ElderlyRecyclerAdapter(ArrayList<Elderly> eList)
    {
        this.elderlyArrayList = eList;
    }

    public void makePhonecall(String phoneNum)
    {
        if (phoneNum != null)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions((Activity) context, new String[] {Manifest.permission.CALL_PHONE} , requestCodeInt);
            }
            else
            {

                String s = "tel:" + phoneNum;
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(s));
                context.startActivity(intent);
            }
        }
        else {
            Toast.makeText(context, "NO phone number set!", Toast.LENGTH_SHORT).show();
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



    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView elderlyIndex, elderlyApptLoc, eldelyName,
                elderlyPhone, elderlyMedName, elderlyMedTime,
                elderlyApptName, elderlyApptTime, elderlyLoc,
                elderly30s, elderlyAlert, elderlyAlertDesc;

        ImageButton viewMed, viewAppt, viewLoc, phonecall, viewMore;



        public MyViewHolder(final View view)
        {
            super(view);
            //Text views
            isLastRecordCancel = false;
            elderlyIndex = view.findViewById(R.id.elderlyItem_index);
            eldelyName = view.findViewById(R.id.elderlyItem_name);
            elderlyPhone = view.findViewById(R.id.elderlyItem_phone);

            elderlyMedName = view.findViewById(R.id.elderlyItem_medName);
            elderlyMedTime = view.findViewById(R.id.elderlyItem_medTime);

            elderlyApptName = view.findViewById(R.id.elderlyItem_apptName);
            elderlyApptLoc = view.findViewById(R.id.elderlyItem_appt_loc);
            elderlyApptTime = view.findViewById(R.id.elderlyItem_apptTime);


            elderlyLoc = view.findViewById(R.id.elderlyItem_locName);

            elderly30s = view.findViewById(R.id.elderlyItem_med_countdown);
            elderlyAlert = view.findViewById(R.id.elderlyItem_med_pvalue);
            elderlyAlertDesc = view.findViewById(R.id.elderlyItem_med_alertText);


            //Image Buttons

            viewMed = view.findViewById(R.id.elderlyItem_medBut);
            viewAppt = view.findViewById(R.id.elderlyItem_apptBut);
            viewLoc = view.findViewById(R.id.elderlyItem_locationBut);
            phonecall = view.findViewById(R.id.elderlyItem_phonecall);
            viewMore = view.findViewById(R.id.elderlyItem_viewMore);




        }
    }


    @NonNull
    @Override
    public ElderlyRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                caretaker = task.getResult().toObject(Caretaker.class);
                elderlyList = caretaker.getElderlyList();
            }
        });


        sharedPreferences = parent.getContext().getSharedPreferences("CaretakerValues", Context.MODE_PRIVATE);
        context = parent.getContext();

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.elderly_item, parent, false);

        return new MyViewHolder(itemView);
    }

    //Here can change the text of the text holder
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ElderlyRecyclerAdapter.MyViewHolder holder, int position)
    {




        holder.elderlyIndex.setText(String.valueOf(position + 1));

        String eName = elderlyArrayList.get(position).getFullName();
        holder.eldelyName.setText(eName);



        String ePhone = elderlyArrayList.get(position).getPhoneNumber();
        holder.elderlyPhone.setText(ePhone);

        elderlyPScore = elderlyArrayList.get(position).getP_score();


        ArrayList<Medication> mList = elderlyArrayList.get(position).getMedList();


        phoneNumber = elderlyArrayList.get(position).getPhoneNumber();



        holder.phonecall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhonecall(phoneNumber);
            }
        });



        // FOR MEDICATION //
        if (mList.size() == 0)
        {
            holder.elderlyMedName.setText("No record found");
            holder.viewMed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String temp = elderlyArrayList.get(holder.getAdapterPosition()).ID;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("elderlyID", temp);
                    editor.commit();
                    Intent navigateTo = new Intent(v.getContext(), WeeklyMedicationActivity.class);
                    v.getContext().startActivity(navigateTo);
                }
            });
        }
        else {

            Collections.sort(mList, new Comparator<Medication>() {
                public int compare(Medication m1, Medication m2) {
                    return m1.getDay().compareTo(m2.getDay());
                };
            });

            String medName = mList.get(0).medName;

            holder.elderlyMedName.setText(medName);
            holder.elderlyMedTime.setTextColor(Color.RED);
            holder.elderlyMedTime.setTypeface(null, Typeface.BOLD);


            Calendar start_calendar = Calendar.getInstance();

            Timestamp timestamp = mList.get(0).getDay();
            Date end_date = timestamp.toDate();

            long start_millis = Math.abs(start_calendar.getTimeInMillis());
            long end_millis = Math.abs(end_date.getTime());

            long total_millis = (end_millis - start_millis);

            CountDownTimer timer = new CountDownTimer(total_millis, 1000) {

                public void onTick(long millisUntilFinished) {
                    long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                    millisUntilFinished -= TimeUnit.DAYS.toMillis(days);

                    long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                    millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);

                    long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                    millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);

                    long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);


                    holder.elderlyMedTime.setText(days + ":" + hours + ":" + minutes + ":" + seconds); //You can compute the millisUntilFinished on hours/minutes/seconds
                }

                public void onFinish() {
                    holder.elderlyMedTime.setText("done!");


                    CountDownTimer timer2 = new CountDownTimer(30 * 1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                            holder.elderly30s.setVisibility(View.VISIBLE);
                            holder.elderlyAlertDesc.setVisibility(View.VISIBLE);
                            holder.elderlyAlertDesc.setTextColor(Color.BLACK);

                            Elderly elderly1 = elderlyArrayList.get(position);

                            if (elderly1.getLevelOfAlert() != 0)
                            {

                                holder.elderlyAlert.setVisibility(View.VISIBLE);
                                holder.elderlyAlert.setText("Alert level: " + elderly1.getLevelOfAlert());
                            }
                            holder.elderlyAlertDesc.setText(elderly1.getAlertMessage());


                            long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                            holder.elderly30s.setText("< " + seconds + " > ");
                            fStore.collection("ElderlyMedicationML")
                                    .whereEqualTo("elderlyID", elderlyArrayList.get(position).getID() )
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                            if (e != null) {
                                                Log.w(TAG, "listen:error", e);
                                                return;
                                            }
                                            for(DocumentChange dc: queryDocumentSnapshots.getDocumentChanges())
                                            {
                                                switch (dc.getType()) {
                                                    case ADDED:
                                                    case REMOVED:
                                                        break;
                                                    case MODIFIED:
                                                        holder.elderly30s.setVisibility(View.INVISIBLE);
                                                        holder.elderlyAlert.setVisibility(View.INVISIBLE);
                                                        holder.elderlyAlertDesc.setVisibility(View.INVISIBLE);
                                                        return;
                                                };
                                            }

                                        }
                                    });


                        }

                        @Override
                        public void onFinish() {
                            holder.elderlyAlert.setVisibility(View.INVISIBLE);
                            holder.elderly30s.setVisibility(View.INVISIBLE);
                            holder.elderlyAlertDesc.setVisibility(View.INVISIBLE);


                            String message = "Elderly has just missed taking ( " + medName + " )";



                            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "NotifyElderlyMed");
                            builder.setContentTitle("Alert! Elderly Has Missed Medication! ");
                            builder.setContentText(message);
                            builder.setContentInfo("Please follow up!");
                            builder.setSmallIcon(R.drawable.app_icon);
                            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                            builder.setDefaults(NotificationCompat.DEFAULT_ALL);
                            builder.setAutoCancel(true);
                            Intent intent = new Intent(context, CaregiverMainActivity.class);

                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("message", message);
                            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            builder.setContentIntent(pendingIntent);


                            NotificationManagerCompat managerCompat =  NotificationManagerCompat.from(context);
                            managerCompat.notify(0, builder.build());




                        }
                    };
                    timer2.start();

                }
            };

            timer.start();



            holder.viewMed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String temp = elderlyArrayList.get(holder.getAdapterPosition()).ID;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("elderlyID", temp);
                    editor.commit();
                    Intent navigateTo = new Intent(v.getContext(), WeeklyMedicationActivity.class);
                    v.getContext().startActivity(navigateTo);
                }
            });
        }



        ArrayList<Appointment> apptList = elderlyArrayList.get(position).getApptList();
        // ---To do: same as above task---
        if (apptList.size() == 0)
        {
            holder.elderlyApptName.setText("No record found");
            holder.viewAppt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String temp = elderlyArrayList.get(holder.getAdapterPosition()).ID;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("elderlyID", temp);
                    editor.commit();
                    Intent navigateTo = new Intent(v.getContext(), WeeklyAppointmentActivity.class);
                    v.getContext().startActivity(navigateTo);
                }
            });
        }
        else{


            Collections.sort(apptList, new Comparator<Appointment>() {
                public int compare(Appointment a1, Appointment a2) {
                    return a1.getTime().compareTo(a2.getTime());
                };
            });
            
            holder.elderlyApptName.setText(apptList.get(0).apptName);
            holder.elderlyApptLoc.setText(apptList.get(0).location);

            holder.elderlyApptTime.setTextColor(Color.RED);
            holder.elderlyApptTime.setTypeface(null, Typeface.BOLD);


            Calendar start_calendar = Calendar.getInstance();

            Timestamp timestamp = apptList.get(0).getTime();
            Date end_date = timestamp.toDate();

            long start_millis = Math.abs(start_calendar.getTimeInMillis());
            long end_millis = Math.abs(end_date.getTime());

            long total_millis = (end_millis - start_millis);


            CountDownTimer timer = new CountDownTimer(total_millis, 1000) {

                public void onTick(long millisUntilFinished) {
                    long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                    millisUntilFinished -= TimeUnit.DAYS.toMillis(days);

                    long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                    millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);

                    long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                    millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);

                    long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                    holder.elderlyApptTime.setText(days + ":" + hours + ":" + minutes + ":" + seconds); //You can compute the millisUntilFinished on hours/minutes/seconds
                }

                public void onFinish() {
                    holder.elderlyApptTime.setText("done!");


                }
            };

            timer.start();






            holder.viewAppt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String temp = elderlyArrayList.get(holder.getAdapterPosition()).ID;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("elderlyID", temp);
                    editor.commit();
                    Intent navigateTo = new Intent(v.getContext(), WeeklyAppointmentActivity.class);
                    v.getContext().startActivity(navigateTo);
                }
            });
        }


        String eLocation = elderlyArrayList.get(position).getCurrentLocation();
        holder.elderlyLoc.setText(eLocation);


        holder.viewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String temp = elderlyArrayList.get(holder.getAdapterPosition()).ID;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("elderlyID", temp);
                editor.commit();
                Intent navigateTo = new Intent(v.getContext(), ElderlyViewMore.class);
                v.getContext().startActivity(navigateTo);
            }
        });







    }

    @Override
    public int getItemCount() {
        return elderlyArrayList.size();
    }


}
