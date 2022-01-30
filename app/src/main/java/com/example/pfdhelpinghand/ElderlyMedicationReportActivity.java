package com.example.pfdhelpinghand;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ElderlyMedicationReportActivity extends AppCompatActivity {

    String elderlyID;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    RecyclerView reportsRV;
    ArrayList<Medication> mList;
    Button backBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_medication_report);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        ArrayList<MedicationReport> allReports = new ArrayList<MedicationReport>();
        reportsRV = findViewById(R.id.medReportRV);
        backBtn = findViewById(R.id.medReportBack);

        Intent intent = getIntent();
        elderlyID = intent.getStringExtra("elderlyID");
        DocumentReference docRef = fStore.collection("ElderlyMedicationMl").document(elderlyID);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<Medication> sList = (ArrayList<Medication>) documentSnapshot.get("alarmFailed");
                ArrayList<Medication> fList = (ArrayList<Medication>) documentSnapshot.get("alarmSuccess");
                if (!(sList == null) || !(fList == null)){
                    fList.addAll(sList);
                    mList = fList;
                }else{
                    mList = new ArrayList<>();
                }

                if (mList.isEmpty()){
                    MedicationReport mRpt = new MedicationReport("No reports available yet!", 0, 0);
                    allReports.add(mRpt);
                }else{
                    for (Medication m:mList) {
                        String name = m.medName;
                        int count = 0;
                        int suc = 0;
                        for (Medication z : mList){
                            if (z.medName.equals(name)){
                                count++;
                                if (sList.contains(z)){
                                    suc++;
                                }
                                mList.remove(z);
                            }
                        }
                        MedicationReport mRpt = new MedicationReport(name, count, suc);
                        allReports.add(mRpt);
                    }
                }

                MedReportAdapter adapter = new MedReportAdapter(allReports);
                reportsRV.setAdapter(adapter);
                reportsRV.setLayoutManager(new LinearLayoutManager(ElderlyMedicationReportActivity.this));
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navigateToPreviousPage = new Intent(ElderlyMedicationReportActivity.this, com.example.pfdhelpinghand.ElderlyViewMore.class);
                startActivity(navigateToPreviousPage);
            }
        });

    }
}