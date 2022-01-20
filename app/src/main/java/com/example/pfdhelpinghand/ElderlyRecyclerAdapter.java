package com.example.pfdhelpinghand;


import androidx.annotation.NonNull;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ElderlyRecyclerAdapter extends RecyclerView.Adapter<ElderlyRecyclerAdapter.MyViewHolder>
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



    public ElderlyRecyclerAdapter(ArrayList<Elderly> eList)
    {
        this.elderlyArrayList = eList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView elderlyIndex, eldelyName, elderlyPhone, elderlyMedName, elderlyMedInfo, elderlyApptName, elderlyApptInfo, elderlyLoc;
        ImageButton viewMed, viewAppt, viewLoc, phonecall;



        public MyViewHolder(final View view)
        {
            super(view);
            //Text views
            elderlyIndex = view.findViewById(R.id.elderlyItem_index);
            eldelyName = view.findViewById(R.id.elderlyItem_name);
            elderlyPhone = view.findViewById(R.id.elderlyItem_phone);

            elderlyMedName = view.findViewById(R.id.elderlyItem_medName);
            elderlyMedInfo = view.findViewById(R.id.elderlyItem_med);

            elderlyApptName = view.findViewById(R.id.elderlyItem_apptName);
            elderlyApptInfo = view.findViewById(R.id.elderlyItem_appt);

            elderlyLoc = view.findViewById(R.id.elderlyItem_locName);

            //Image Buttons

            viewMed = view.findViewById(R.id.elderlyItem_medBut);
            viewAppt = view.findViewById(R.id.elderlyItem_apptBut);
            viewLoc = view.findViewById(R.id.elderlyItem_locationBut);
            phonecall = view.findViewById(R.id.elderlyItem_phonecall);




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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.elderly_item, parent, false);
        return new MyViewHolder(itemView);
    }

    //Here can change the text of the text holder
    @Override
    public void onBindViewHolder(@NonNull ElderlyRecyclerAdapter.MyViewHolder holder, int position) {

        holder.elderlyIndex.setText(String.valueOf(position + 1));

        String eName = elderlyArrayList.get(position).getFullName();
        holder.eldelyName.setText(eName);


        String ePhone = elderlyArrayList.get(position).getPhoneNumber();
        holder.elderlyPhone.setText(ePhone);


        ArrayList<Medication> mList = elderlyArrayList.get(position).getMedList();


        // ---To do: get the lastest med record according to the current date time---
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
            holder.elderlyMedName.setText(mList.get(0).medName + mList.get(0).medDescription);
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
            holder.elderlyApptName.setText(apptList.get(0).apptName + apptList.get(0).location);
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







    }

    @Override
    public int getItemCount() {
        return elderlyArrayList.size();
    }
}
