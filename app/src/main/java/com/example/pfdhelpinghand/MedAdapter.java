package com.example.pfdhelpinghand;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MedAdapter extends RecyclerView.Adapter<MedAdapter.MedViewHolder>{


    List mMeds;
    public MedAdapter(List mMeds){
        this.mMeds = mMeds;
    }

    @NonNull
    @Override
    public MedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

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
        }else{
            String temp = simpleDateFormat.format(calendar.getTime());
            holder.txtMedDate.setText(temp);
        }
        holder.txtMedName.setText(currentMed.medName);
        holder.txtMedInstruct.setText(currentMed.medDescription);
    }

    @Override
    public int getItemCount() {
        return mMeds.size();
    }

    public class MedViewHolder extends RecyclerView.ViewHolder{
        TextView txtMedName;
        TextView txtMedDate;
        TextView txtMedInstruct;
        public MedViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMedName = itemView.findViewById(R.id.txtMedName);
            txtMedDate = itemView.findViewById(R.id.txtMedDate);
            txtMedInstruct = itemView.findViewById(R.id.txtMedInstruct);

        }
    }

}


