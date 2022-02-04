package com.example.pfdhelpinghand;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MedReportAdapter extends RecyclerView.Adapter<MedReportAdapter.MedReportHolder> {

    ArrayList<MedicationReport> mList;

    public MedReportAdapter(ArrayList<MedicationReport> mList){this.mList = mList;}


    @NonNull
    @Override
    public MedReportHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View reportView = inflater.inflate(R.layout.med_report_item, parent, false);
        MedReportHolder viewHolder = new MedReportHolder(reportView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MedReportHolder holder, int position) {
        MedicationReport currentMed = (MedicationReport) mList.get(position);
        if (currentMed.medName.equals("No reports available yet!")){
            holder.medName.setText(currentMed.medName);
            holder.medPB.setVisibility(View.GONE);
            holder.medRatio.setVisibility(View.GONE);
            holder.medDes.setVisibility(View.GONE);
        }else{
            holder.medName.setText(currentMed.medName);
            holder.medRatio.setText(currentMed.numSucceed + "/" + currentMed.totalNumber);
            if (currentMed.totalNumber ==currentMed.numSucceed){
                holder.medRatio.setTextColor(Color.parseColor("#17D7A0"));
            }else if (currentMed.numSucceed == 0){
                holder.medRatio.setTextColor(Color.parseColor("#FC4F4F"));
            }else{
                holder.medRatio.setTextColor(Color.parseColor("#FFCE45"));
            }
            holder.medPB.setMax(currentMed.totalNumber);
            holder.medPB.setProgress(currentMed.numSucceed);
            holder.medDes.setText(currentMed.medDes);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MedReportHolder extends RecyclerView.ViewHolder{
        TextView medName;
        TextView medRatio;
        TextView medDes;
        ProgressBar medPB;
        public MedReportHolder(@NonNull View itemView) {
            super(itemView);
            medName = itemView.findViewById(R.id.medReportNameTV);
            medRatio = itemView.findViewById(R.id.medRatioTV);
            medPB = itemView.findViewById(R.id.medReportProgressBar);
            medDes = itemView.findViewById(R.id.medReportDescriptionTV);
        }
    }
}
