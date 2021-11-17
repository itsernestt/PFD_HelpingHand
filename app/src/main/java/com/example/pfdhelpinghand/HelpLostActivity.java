package com.example.pfdhelpinghand;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HelpLostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_lost);

        Button returnBack = findViewById(R.id.helpLostBackButton);
        returnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent navigateToPreviousPage = new Intent(HelpLostActivity.this, com.example.pfdhelpinghand.MainActivity.class);
                startActivity(navigateToPreviousPage);
            }
        });
    }
}