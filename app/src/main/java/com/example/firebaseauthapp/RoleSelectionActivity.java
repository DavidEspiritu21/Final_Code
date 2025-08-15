package com.example.firebaseauthapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;

public class RoleSelectionActivity extends AppCompatActivity {

    private Button patientButton, guardianButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        patientButton = findViewById(R.id.patientButton);
        guardianButton = findViewById(R.id.guardianButton);

        patientButton.setOnClickListener(v -> {
            Intent intent = new Intent(RoleSelectionActivity.this, PatientRegistrationActivity.class);
            startActivity(intent);
            finish();
        });

        guardianButton.setOnClickListener(v -> {
            Intent intent = new Intent(RoleSelectionActivity.this, GuardianRegistrationActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
