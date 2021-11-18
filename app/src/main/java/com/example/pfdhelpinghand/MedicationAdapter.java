package com.example.pfdhelpinghand;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.ViewHolder> {


    @Override
    public MedicationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View medView = inflater.inflate(R.layout.activity_medication_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(medView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MedicationAdapter.ViewHolder holder, int position) {
        Medication meds = mList.get(position);

        TextView textView = holder.nameTV;
        textView.setText(meds.medName);
        TextView descript = holder.descriptionTV;
        descript.setText(meds.medDescription);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTV;
        public TextView descriptionTV;


        public ViewHolder(View itemView) {

            super(itemView);

            nameTV = (TextView) itemView.findViewById(R.id.medName);
            descriptionTV = (TextView) itemView.findViewById(R.id.medDescription);
        }
    }

    private List<Medication> mList;

    public MedicationAdapter(List<Medication> medications){
        mList = medications;
    }
}
