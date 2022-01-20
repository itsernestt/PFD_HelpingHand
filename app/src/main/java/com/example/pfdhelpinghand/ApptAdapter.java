package com.example.pfdhelpinghand;

import android.app.AlertDialog;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ApptAdapter extends RecyclerView.Adapter<ApptAdapter.ApptViewHolder> {


    List appts;
    Elderly elderly;
    ArrayList<Appointment> appointments;
    SharedPreferences sharedPreferences;
    String userID;
    String usertype;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseUser user = fAuth.getCurrentUser();
    public ApptAdapter(List appts){ this.appts = appts;}

    @NonNull
    @Override
    public ApptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        appointments = new ArrayList<Appointment>();

        sharedPreferences = context.getSharedPreferences("usertype", Context.MODE_PRIVATE);
        usertype = sharedPreferences.getString("type", "Elderly");

        DocumentReference docRef;
        if (usertype.equals("Elderly")){
            userID = user.getUid();
            docRef = fStore.collection("Elderly").document(userID);
        }else{
            sharedPreferences = context.getSharedPreferences("CaretakerValues", Context.MODE_PRIVATE);
            userID = sharedPreferences.getString("elderlyID", "");
            docRef = fStore.collection("Elderly").document(userID);
        }

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                elderly = task.getResult().toObject(Elderly.class);
                appointments = elderly.getApptList();
            }
        });

        View apptView = inflater.inflate(R.layout.appointment_item, parent, false);
        ApptViewHolder viewHolder = new ApptViewHolder(apptView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ApptViewHolder holder, int position) {
        Appointment currentAppt = (Appointment) appts.get(position);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentAppt.getTime().toDate());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a, dd MMM yyyy");
        SimpleDateFormat checker = new SimpleDateFormat("dd/MM/yyyy");
        String temps = checker.format(calendar.getTime());

        if (temps.equals("01/01/2003")){
            holder.txtApptDay.setText("");
        }else{
            String temp = simpleDateFormat.format(calendar.getTime());
            holder.txtApptDay.setText(temp);
        }

        holder.txtApptName.setText(currentAppt.apptName);
        holder.txtApptLocation.setText(currentAppt.location);

        if (!usertype.equals("Elderly")){
            holder.deleteBtn.setId(position+1);
            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Delete")
                            .setMessage("Do you want to delete this appointment?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    appointments.remove(holder.getAdapterPosition());

                                    DocumentReference docRef = fStore.collection("Elderly").document(userID);
                                    docRef.update("apptList", appointments)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Log.d("TAG","onSuccess: Medication properly removed");
                                                    v.getContext().startActivity(new Intent(v.getContext(), WeeklyMedicationActivity.class));
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("TAG", "onFailure: " + e.toString());
                                        }
                                    });
                                }
                            })
                    .setNegativeButton(android.R.string.no, null).show();
                }
            });
        }else{
            holder.deleteBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return appts.size();
    }

    public class ApptViewHolder extends RecyclerView.ViewHolder{

        TextView txtApptName;
        TextView txtApptDay;
        TextView txtApptLocation;
        Button deleteBtn;
        public ApptViewHolder(@NonNull View itemView) {
            super(itemView);

            txtApptName = itemView.findViewById(R.id.txtApptName);
            txtApptDay = itemView.findViewById(R.id.txtApptDay);
            txtApptLocation = itemView.findViewById(R.id.txtApptLocation);
            deleteBtn = itemView.findViewById(R.id.deleteApptBtn);
        }
    }
}
