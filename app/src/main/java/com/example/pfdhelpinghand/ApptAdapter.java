package com.example.pfdhelpinghand;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ApptAdapter extends RecyclerView.Adapter<ApptAdapter.ApptViewHolder> {


    List appts;
    public ApptAdapter(List appts){ this.appts = appts;}

    @NonNull
    @Override
    public ApptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

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
    }

    @Override
    public int getItemCount() {
        return appts.size();
    }

    public class ApptViewHolder extends RecyclerView.ViewHolder{

        TextView txtApptName;
        TextView txtApptDay;
        TextView txtApptLocation;
        public ApptViewHolder(@NonNull View itemView) {
            super(itemView);

            txtApptName = itemView.findViewById(R.id.txtApptName);
            txtApptDay = itemView.findViewById(R.id.txtApptDay);
            txtApptLocation = itemView.findViewById(R.id.txtApptLocation);
        }
    }
}
