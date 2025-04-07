package com.example.autosilent_madapp;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class TimeHome extends AppCompatActivity {

    private Button btnAddTime, btnViewTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_home);

        btnAddTime = findViewById(R.id.btnAdd);
        btnViewTimes = findViewById(R.id.btnView);

        // Add Time Button Click -> Open Date & Time Picker
        btnAddTime.setOnClickListener(v -> {
            Intent intent = new Intent(TimeHome.this, TimerActivity.class);
            startActivity(intent);
        });

        // View Saved Times Button Click -> Open Saved Times List
        btnViewTimes.setOnClickListener(v -> {
            Intent intent = new Intent(TimeHome.this, SavedTimeData.class);
            startActivity(intent);
        });
    }
}
