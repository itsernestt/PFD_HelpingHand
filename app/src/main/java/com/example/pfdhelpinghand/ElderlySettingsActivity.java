package com.example.pfdhelpinghand;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ElderlySettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_settings);

        Button eldSettingsBackBtn = findViewById(R.id.eldSettingsBack);
        eldSettingsBackBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent goBack = new Intent(ElderlySettingsActivity.this, MainActivity.class);
                startActivity(goBack);
            }
        });

        TextView viewCaretakers = findViewById(R.id.caregiversTV);
        viewCaretakers.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent navigateNext = new Intent(ElderlySettingsActivity.this, ViewCaregivers.class);
                startActivity(navigateNext);
            }
        });
    }
}