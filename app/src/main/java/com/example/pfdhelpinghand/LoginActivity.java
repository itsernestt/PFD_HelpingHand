package com.example.pfdhelpinghand;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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