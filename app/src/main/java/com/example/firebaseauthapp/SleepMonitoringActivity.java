package com.example.firebaseauthapp;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SleepMonitoringActivity extends AppCompatActivity {

    private TextView statusIndicator, timeSleeping, riskLevel, heartRate, oxygenLevel, movement, riskScore, weeklyOverview, parasomniaEpisodes;
    private ImageButton profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_monitoring);

        // Initialize views
        statusIndicator = findViewById(R.id.statusIndicator);
        timeSleeping = findViewById(R.id.timeSleeping);
        riskLevel = findViewById(R.id.riskLevel);
        heartRate = findViewById(R.id.heartRate);
        oxygenLevel = findViewById(R.id.oxygenLevel);
        movement = findViewById(R.id.movement);
        riskScore = findViewById(R.id.riskScore);
        weeklyOverview = findViewById(R.id.weeklyOverview);
        parasomniaEpisodes = findViewById(R.id.parasomniaEpisodes);
        profileButton = findViewById(R.id.profileButton);

        // Set up profile button listener
        profileButton.setOnClickListener(v -> openProfileActivity());

        // TODO: Fetch data from Firebase and update UI
    }

    private void openProfileActivity() {
        // Logic to open the profile activity
    }

    // TODO: Add methods to fetch data from Firebase
}
