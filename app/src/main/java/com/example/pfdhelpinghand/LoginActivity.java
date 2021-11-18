package com.example.pfdhelpinghand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    EditText mEmail, mPassword;
    Button mLoginButton;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.loginEmail);
        mPassword = findViewById(R.id.loginPassword);
        mLoginButton = findViewById(R.id.loginButton);

        mProgressBar = findViewById(R.id.progressBar);
        fAuth = FirebaseAuth.getInstance();


        mLoginButton.setOnClickListener(new View.OnClickListener() {
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


                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(LoginActivity.this, "Log in successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), CaregiverMainActivity.class));
                        } else {
                            Toast.makeText(LoginActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    });
               }
            });



        Button caregiverSignUpPage = findViewById(R.id.signUpCaregiverButton);
        caregiverSignUpPage.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {

                Intent navigateToCaregiverSignUpPage = new Intent(LoginActivity.this, SignUpMainActivity.class);
                startActivity(navigateToCaregiverSignUpPage);
            }
        });

        Button elderlySignUpPage = findViewById(R.id.signUpElderlyButton);
        elderlySignUpPage.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                Intent navigateToElderlySignUpPage = new Intent(LoginActivity.this, SignUpElderlyActivity.class);
                startActivity(navigateToElderlySignUpPage);
            }
        });


        Button navToCaregiverButton = findViewById(R.id.caregiverMainPage);
        navToCaregiverButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                Intent navigateToCaregiverPage = new Intent(LoginActivity.this, com.example.pfdhelpinghand.CaregiverMainActivity.class);
                startActivity(navigateToCaregiverPage);
            }
        });

        Button navToElderlyButton = findViewById(R.id.elderlyMainPageButton);
        navToElderlyButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                Intent navigateToElderlyPage = new Intent(LoginActivity.this, com.example.pfdhelpinghand.MainActivity.class);
                startActivity(navigateToElderlyPage);
            }
        });






    }
}