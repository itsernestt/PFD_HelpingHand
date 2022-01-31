package com.example.pfdhelpinghand;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ApptReportAdapter extends RecyclerView.Adapter<ApptReportAdapter.ApptReportHolder> {

    ArrayList<Appointment> aList;

    public ApptReportAdapter(ArrayList<Appointment> aList){this.aList = aList;}


    @NonNull
    @Override
    public ApptReportAdapter.ApptReportHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View reportView = inflater.inflate(R.layout.appt_report_item, parent, false);
        ApptReportAdapter.ApptReportHolder viewHolder = new ApptReportAdapter.ApptReportHolder(reportView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ApptReportAdapter.ApptReportHolder holder, int position) {
        Appointment a = (Appointment) aList.get(position);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a, dd MMM yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(a.getTime().toDate());
        if (a.apptName.equals("No appointments yet!")){
            holder.apptName.setText(a.apptName);
            holder.apptLocation.setVisibility(View.GONE);
            holder.apptTime.setVisibility(View.GONE);
        }else{
            holder.apptName.setText(a.apptName);
            holder.apptLocation.setText(a.location);
            holder.apptTime.setText(simpleDateFormat.format(a.getTime()));
        }
    }

    @Override
    public int getItemCount() {
        return aList.size();
    }

    public class ApptReportHolder extends RecyclerView.ViewHolder{
        TextView apptName;
        TextView apptLocation;
        TextView apptTime;
        public ApptReportHolder(@NonNull View itemView) {
            super(itemView);
            apptName = itemView.findViewById(R.id.apptReportNameTV);
            apptLocation = itemView.findViewById(R.id.apptReportLocationTV);
            apptTime = itemView.findViewById(R.id.apptReportTimeTV);
        }
    }
}
