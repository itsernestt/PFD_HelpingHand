package com.example.pfdhelpinghand;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MedAdapter extends RecyclerView.Adapter<MedAdapter.MedViewHolder>{


    Elderly elderly;
    List mMeds;
    ArrayList<Medication> meds;
    SharedPreferences sharedPreferences;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseUser user = fAuth.getCurrentUser();
    String userID = user.getUid();
    DocumentReference docRef = fStore.collection("Elderly").document(userID);
    public MedAdapter(List mMeds){
        this.mMeds = mMeds;
    }

    @NonNull
    @Override
    public MedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        sharedPreferences = context.getSharedPreferences("mainView", Context.MODE_PRIVATE);


        meds = new ArrayList<Medication>();


        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                elderly = documentSnapshot.toObject(Elderly.class);

                meds = elderly.getMedList();
            }
        });

        View medView = inflater.inflate(R.layout.medication_item, parent, false);

        MedViewHolder viewHolder = new MedViewHolder(medView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MedViewHolder holder, int position) {
        Medication currentMed = (Medication) mMeds.get(position);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentMed.getDay().toDate());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a, EEE");
        SimpleDateFormat checker = new SimpleDateFormat("dd/MM/yyyy");
        String temps = checker.format(calendar.getTime());

        if (temps.equals("01/01/2003")){
            holder.txtMedDate.setText("");
            holder.deleteBtn.setVisibility(View.GONE);
        }else{
            String temp = simpleDateFormat.format(calendar.getTime());
            holder.txtMedDate.setText(temp);
        }
        String isWeekly = sharedPreferences.getString("main", "false");
        if (isWeekly == "false"){
            holder.deleteBtn.setVisibility(View.GONE);
        }
        holder.txtMedName.setText(currentMed.medName);
        holder.txtMedInstruct.setText(currentMed.medDescription);
        holder.deleteBtn.setId(position+1);
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meds.remove(holder.getAdapterPosition());
                docRef.update("medList", meds)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("TAG","onSuccess: Medication properly removed");
                                notifyItemChanged(holder.getAdapterPosition());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "onFailure: " + e.toString());
                    }
                });
            }
        });
    }



    @Override
    public int getItemCount() {
        return mMeds.size();
    }

    public class MedViewHolder extends RecyclerView.ViewHolder{
        TextView txtMedName;
        TextView txtMedDate;
        TextView txtMedInstruct;
        Button deleteBtn;
        public MedViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMedName = itemView.findViewById(R.id.txtMedName);
            txtMedDate = itemView.findViewById(R.id.txtMedDate);
            txtMedInstruct = itemView.findViewById(R.id.txtMedInstruct);
            deleteBtn = itemView.findViewById(R.id.deleteMedBtn);

        }
    }

}


