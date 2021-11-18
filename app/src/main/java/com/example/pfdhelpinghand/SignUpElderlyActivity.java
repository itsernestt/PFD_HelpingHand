package com.example.pfdhelpinghand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class SignUpElderlyActivity extends AppCompatActivity {
    EditText mFullName, mEmail, mPhone, mPassword, mPassword2, mContactName, mContactPhone, mAddress;
    Button mRegisterButton;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_elderly);

        mFullName = findViewById(R.id.elderlyName);
        mEmail = findViewById(R.id.elderlyEmail);
        mPhone = findViewById(R.id.elderlyPhone);
        mPassword = findViewById(R.id.elderlyPassword);
        mPassword2 = findViewById(R.id.elderlyPassword2);
        mContactName = findViewById(R.id.elderlyContactName);
        mContactPhone = findViewById(R.id.elderlyContactPhone);

        mRegisterButton = findViewById(R.id.elderlyRegisterButton);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        if (fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        mRegisterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                String userFullName = mFullName.getText().toString();
                String email = mEmail.getText().toString().trim();
                String phone = mPhone.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String password2 = mPassword2.getText().toString().trim();
                String contactName = mContactName.getText().toString();
                String contactPhone = mContactPhone.getText().toString().trim();
                String address = mAddress.getText().toString();

                if (TextUtils.isEmpty(userFullName)) {
                    mFullName.setError("Your full name is required!");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required!");
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    mPhone.setError("Your phone number is required!");
                    return;
                }


                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required!");
                    return;
                }


                if (password.length() < 8) {
                    mPassword.setError("Password must be >= 8 characters");
                    return;
                }

                if (TextUtils.isEmpty(password2))
                {
                    mPassword2.setError("Please enter your password again");
                    return;
                }

                if (!password.equals(password2))
                {
                    mPassword2.setError("Password does not match! ");
                    mPassword2.setText("");
                    return;
                }

                if (TextUtils.isEmpty(contactName))
                {
                    mPassword2.setError("Contact person name is required!");
                    return;
                }
                if (TextUtils.isEmpty(contactPhone))
                {
                    mPassword2.setError("Contact person phone number is required!");
                    return;
                }
                if (TextUtils.isEmpty(address))
                {
                    mPassword2.setError("Address is required!");
                    return;
                }


                //register the user in the firebase
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(SignUpElderlyActivity.this, "Elderly User Created", Toast.LENGTH_SHORT).show();

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String userID = user.getUid();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(userFullName + "-elderly").build();

                            user.updateProfile(profileUpdates);

                            Map<String, Object> userData = new HashMap<>();

                            userData.put("FullName", userFullName);
                            userData.put("Email", email);
                            userData.put("Password", password);
                            userData.put("Phone", phone);
                            userData.put("ContactPersonName0",contactName);
                            userData.put("ContactPersonPhone0", contactPhone);
                            userData.put("Address", address);

                            fStore.collection("Elderly")
                                    .add(userData)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {

                                            Log.d("TAG","onSuccess: Elderly user profile created for " + userID);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("TAG", "onFailure: " + e.toString());
                                }
                            });

                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(SignUpElderlyActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }
}
