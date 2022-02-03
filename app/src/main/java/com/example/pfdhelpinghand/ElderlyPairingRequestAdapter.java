package com.example.pfdhelpinghand;
import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;

import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class ElderlyPairingRequestAdapter extends RecyclerView.Adapter<ElderlyPairingRequestAdapter.MyViewHolder> {

    private ArrayList<PairUpRequest> pairUpRequests;

    FirebaseAuth fAuth= FirebaseAuth.getInstance();;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseUser user= fAuth.getCurrentUser();
    String userID = user.getUid();
    Elderly elderly;
    ArrayList<String> caretakerList;
    DocumentReference docRef = fStore.collection("Elderly").document(userID);

    PairUpRequest pairUpRequest;

    Boolean elderlyUpdated = false;
    Boolean caregiverUpdated = false;

    Context context;


    public ElderlyPairingRequestAdapter(ArrayList<PairUpRequest> requests)
    {
        this.pairUpRequests = requests;
    }




    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView email;
        Button accept, reject;


        public MyViewHolder(final View view) {
            super(view);

            email = view.findViewById(R.id.pairupAdapter_caregiverEmail);
            accept = view.findViewById(R.id.pairupAdapter_accept);
            reject = view.findViewById(R.id.pairupAdapter_reject);


        }
    }



    @NonNull
    @Override
    public ElderlyPairingRequestAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                elderly = task.getResult().toObject(Elderly.class);
                caretakerList = elderly.getCaretakerList();
            }
        });

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pair_up_adapter, parent, false);
        context = parent.getContext();
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull ElderlyPairingRequestAdapter.MyViewHolder holder, int position) {

        pairUpRequest = pairUpRequests.get(position);


        holder.email.setText(pairUpRequests.get(position).getSenderEmail());


        holder.accept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
               DocumentReference r =  fStore.collection("PairingRequest").document(pairUpRequest.getDocumentID());

               r.update("isPairUpSuccess", true)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");

                                fStore.collection("Caregiver")
                                        .whereEqualTo("email", pairUpRequest.getSenderEmail())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                        Caretaker caretaker = snapshot.toObject(Caretaker.class);
                                                        ArrayList<String> elderlyList = caretaker.getElderlyList();


                                                        if (!caretakerList.contains(caretaker.getID()))
                                                        {
                                                            caretakerList.add(caretaker.getID());
                                                        }
                                                        if (!elderlyList.contains(elderly.getID()))
                                                        {
                                                            elderlyList.add(elderly.getID());
                                                        }

                                                        // 4. Updates the elderly on the fire store
                                                        fStore.collection("Elderly").document(userID)
                                                                .update("caretakerList", caretakerList)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Log.d(TAG, "onSuccess: Elderly user updated for" + userID);
                                                                        elderlyUpdated = true;

                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.d(TAG, "Failed Updating Elderly document");

                                                            }
                                                        });
                                                        
                                                        
                                                        fStore.collection("Caregiver").document(caretaker.getID())
                                                                .update("elderlyList", elderlyList)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Log.d(TAG, "onSuccess: Caregiver user updated for" + userID);
                                                                        caregiverUpdated = true;
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.d(TAG, "onFailure: Failed updating Caregiver document");
                                                            }
                                                        });


                                                        if (caregiverUpdated && elderlyUpdated)
                                                        {
                                                            Toast.makeText(context, "Elderly and caretaker updated!", Toast.LENGTH_SHORT).show();
                                                        }

                                                    }


                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                    Toast.makeText(view.getContext(), "Error getting document", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });





                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });
            }
        });

        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fStore.collection("PairingRequest")
                        .document(pairUpRequests.get(position).getDocumentID())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });
            }
        });







    }

    @Override
    public int getItemCount() {
        return pairUpRequests.size();
    }


}
