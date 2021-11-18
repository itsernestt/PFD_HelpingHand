package com.example.pfdhelpinghand;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpMainActivity extends AppCompatActivity {
    EditText mFullName, mEmail, mPassword, mPassword2, mPhone;
    Button mRegisterButton;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_main);

        mFullName = findViewById(R.id.caregiverName);
        mEmail = findViewById(R.id.caregiverEmail);
        mPassword = findViewById(R.id.caregiverPassword);
        mPassword2 = findViewById(R.id.caregiverPassword2);
        mPhone = findViewById(R.id.caregiverPhone);
        mRegisterButton = findViewById(R.id.caregiverRegisterButton);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        /*
        if (fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
        */


        mRegisterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required!");
                    return;
                }


                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required!");
                    return;
                }


                if (password.length() < 6) {
                    mPassword.setError("Password must be >= 6 characters");
                    return;
                }

                //register the user in the firebase
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(SignUpMainActivity.this, "Caregiver User Created", Toast.LENGTH_SHORT).show();

/*
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("Caregivers").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("fName", fullname);
                            user.put("email", email);
                            user.put("phone", phone);
*/

                            startActivity(new Intent(getApplicationContext(), CaregiverMainActivity.class));
                        } else {
                            Toast.makeText(SignUpMainActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });



    }
}