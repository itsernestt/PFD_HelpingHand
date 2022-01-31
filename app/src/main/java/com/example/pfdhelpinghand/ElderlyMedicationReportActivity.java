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
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ElderlyMedicationReportActivity extends AppCompatActivity {

    String elderlyID;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    RecyclerView reportsRV;
    ArrayList<Medication> mList = new ArrayList<>();
    Button backBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_medication_report);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        reportsRV = findViewById(R.id.medReportRV);
        backBtn = findViewById(R.id.medReportBack);

        Intent intent = getIntent();
        elderlyID = intent.getStringExtra("elderlyID");
        DocumentReference docRef = fStore.collection("ElderlyMedicationML").document(elderlyID);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<Medication>fList = new ArrayList<>();
                ArrayList<Medication>sList=new ArrayList<>();
                MedReportElderly current = documentSnapshot.toObject(MedReportElderly.class);
                fList = current.alarmFailed;
                sList = current.alarmSuccess;

                ArrayList<MedicationReport> allReports = new ArrayList<>();


                if (!(sList == null) || !(fList == null)){
                    mList.addAll(sList);
                    mList.addAll(fList);
                }else{
                    mList = new ArrayList<>();
                }

                if (mList.isEmpty()){
                    MedicationReport mRpt = new MedicationReport("No reports available yet!", 0, 0);
                    allReports.add(mRpt);
                }else{
                    ArrayList<String> names = new ArrayList<>();
                    for (Medication m:mList) {
                        if (names.contains(m.medName)){
                            int x = names.indexOf(m.medName);
                            allReports.get(x).totalNumber += 1;
                            if (sList.contains(m)){
                                allReports.get(x).numSucceed += 1;
                            }
                        }else{
                            names.add(m.medName);
                            if (fList.contains(m)){
                                MedicationReport mRpt = new MedicationReport(m.medName, 1, 0);
                                allReports.add(mRpt);
                            }else{
                                MedicationReport mRpt = new MedicationReport(m.medName, 1, 1);
                                allReports.add(mRpt);
                            }
                        }
                    }
                }

                Collections.sort(allReports, (o1, o2) -> o1.medName.compareTo(o2.medName));

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