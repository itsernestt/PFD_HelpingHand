package com.example.pfdhelpinghand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ViewCaregiverAdapter extends RecyclerView.Adapter<ViewCaregiverAdapter.MyViewHolder>
{
    private ArrayList<Caretaker> caretakerArrayList;
    Elderly elderly;
    FirebaseAuth fAuth= FirebaseAuth.getInstance();;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseUser user= fAuth.getCurrentUser();
    String userID = user.getUid();
    DocumentReference docRef = fStore.collection("Elderly").document(userID);
    ArrayList<String> caregiverStringList;
    Context context;



    public ViewCaregiverAdapter(ArrayList<Caretaker> cList)
    {
        this.caretakerArrayList = cList;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView caregiverName, caregiverPhoneNum, caregiverIndex;
        ImageButton caregiverPhoneCall;

        public MyViewHolder(final View view) {
            super(view);
            caregiverIndex = view.findViewById(R.id.viewCaregivers_index);
            caregiverName = view.findViewById(R.id.viewCaregivers_name);
            caregiverPhoneNum = view.findViewById(R.id.viewCaregivers_phone);
            caregiverPhoneCall = view.findViewById(R.id.viewCaregivers_phonecall);

        }
    }


    @NonNull
    @Override
    public ViewCaregiverAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                elderly = task.getResult().toObject(Elderly.class);
                caregiverStringList = elderly.getCaretakerList();
            }
        });

        context = parent.getContext();

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_view_caregiver_adapter, parent, false);

        return new ViewCaregiverAdapter.MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewCaregiverAdapter.MyViewHolder holder, int position) {

        holder.caregiverIndex.setText(String.valueOf(position+1));
        String caregiverName = caretakerArrayList.get(position).getFullName();
        String caregiverPhone = caretakerArrayList.get(position).getPhoneNumber();

        holder.caregiverName.setText(caregiverName);
        holder.caregiverPhoneNum.setText(caregiverPhone);





    }

    @Override
    public int getItemCount() {
        return caretakerArrayList.size();
    }

}





















