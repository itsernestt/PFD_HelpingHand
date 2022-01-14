package com.example.pfdhelpinghand;


import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;

public class ElderlyRecyclerAdapter extends RecyclerView.Adapter<ElderlyRecyclerAdapter.MyViewHolder>
{
    private ArrayList<Elderly> elderlyArrayList;
    public ElderlyRecyclerAdapter(ArrayList<Elderly> eList)
    {
        this.elderlyArrayList = eList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView eldelyName, elderlyPhone, elderlyMed, elderlyAppt, elderlyLoc;
        Button viewMed, viewAppt, viewLoc, phonecall;

        public MyViewHolder(final View view)
        {
            super(view);
            eldelyName = view.findViewById(R.id.elderlyItem_name);
            elderlyPhone = view.findViewById(R.id.elderlyItem_phone);
            elderlyMed = view.findViewById(R.id.elderlyItem_med);
            elderlyAppt = view.findViewById(R.id.elderlyItem_appt);
            elderlyLoc = view.findViewById(R.id.elderlyItem_location);

            viewMed = view.findViewById(R.id.elderlyItem_medBut);
            viewAppt = view.findViewById(R.id.elderlyItem_apptBut);
            viewLoc = view.findViewById(R.id.elderlyItem_locationBut);
            phonecall = view.findViewById(R.id.elderlyItem_phonecall);


        }
    }


    @NonNull
    @Override
    public ElderlyRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.elderly_item, parent, false);
        return new MyViewHolder(itemView);


    }

    //Here can change the text of the text holder
    @Override
    public void onBindViewHolder(@NonNull ElderlyRecyclerAdapter.MyViewHolder holder, int position) {
        String eName = elderlyArrayList.get(position).getFullName();
        holder.eldelyName.setText(eName);

        String ePhone = elderlyArrayList.get(position).getPhoneNumber();
        holder.elderlyPhone.setText(ePhone);

        ArrayList<Medication> mList = elderlyArrayList.get(position).getMedList();

        // ---To do: get the lastest med record according to the current date time---
        String eMed = mList.get(0).medName + mList.get(0).medDescription;
        holder.elderlyMed.setText(eMed);

        ArrayList<Appointment> apptList = elderlyArrayList.get(position).getApptList();
        // ---To do: same as above task---
        String eAppt = apptList.get(0).apptName + apptList.get(0).location;
        holder.elderlyAppt.setText(eAppt);

        String eLocation = elderlyArrayList.get(position).getCurrentLocation();
        holder.elderlyLoc.setText(eLocation);


    }

    @Override
    public int getItemCount() {
        return elderlyArrayList.size();
    }
}
