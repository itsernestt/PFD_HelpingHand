package com.example.pfdhelpinghand;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ViewCaregivers extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_caregivers);

        Button backButton = findViewById(R.id.viewCGBackBtn);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent navigateBack = new Intent(ViewCaregivers.this, ElderlySettingsActivity.class);
                startActivity(navigateBack);
            }
        });
    }
}