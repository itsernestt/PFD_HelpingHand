package com.example.pfdhelpinghand;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
import java.util.Collections;
import java.util.List;

public class MedAdapter extends RecyclerView.Adapter<MedAdapter.MedViewHolder>{


    private Context context;
    Elderly elderly;
    List mMeds;
    ArrayList<Medication> meds;
    SharedPreferences sharedPreferences;


    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseUser user = fAuth.getCurrentUser();
    String userID;
    String usertype;
    public MedAdapter(List mMeds){
        this.mMeds = mMeds;
    }

    @NonNull
    @Override
    public MedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        meds = new ArrayList<Medication>();


        sharedPreferences = context.getSharedPreferences("usertype", Context.MODE_PRIVATE);
        usertype = sharedPreferences.getString("type", "Elderly");

        DocumentReference docRef;

        if (usertype.equals("Elderly")){
            docRef = fStore.collection("Elderly").document(user.getUid());
        }else{
            sharedPreferences = context.getSharedPreferences("CaretakerValues", Context.MODE_PRIVATE);
            userID = sharedPreferences.getString("elderlyID", "UfDuCIuLDqUo3niFg73o5jK3Jml2");
            docRef = fStore.collection("Elderly").document(userID);
        }

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                elderly = documentSnapshot.toObject(Elderly.class);

                meds = elderly.getMedList();
                Collections.sort(meds);
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

        holder.txtMedName.setText(currentMed.medName);
        holder.txtMedInstruct.setText(currentMed.medDescription);


        if (!usertype.equals("Elderly")){
            holder.deleteBtn.setId(position+1);
            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Delete")
                            .setMessage("Do you want to delete this medication?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    meds.remove(holder.getAdapterPosition());


                                    DocumentReference docRef = fStore.collection("Elderly").document(userID);
                                    docRef.update("medList", meds)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d("TAG","onSuccess: Medication properly removed");
                                                    v.getContext().startActivity(new Intent(v.getContext(), WeeklyMedicationActivity.class));
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
        } else{
            holder.deleteBtn.setVisibility(View.GONE);
        }
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
            deleteBtn = itemView.findViewById(R.id.xDialogButton);

        }
    }

}


