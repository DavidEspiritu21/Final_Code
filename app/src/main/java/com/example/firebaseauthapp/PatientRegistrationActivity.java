package com.example.firebaseauthapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firebaseauthapp.models.User;
import com.google.firebase.auth.FirebaseUser;

public class PatientRegistrationActivity extends AppCompatActivity {

    private Button completeRegistrationButton;
    private ProgressBar progressBar;
    private TextView patientIdTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_registration);

        completeRegistrationButton = findViewById(R.id.completeRegistrationButton);
        progressBar = findViewById(R.id.progressBar);
        patientIdTextView = findViewById(R.id.patientIdTextView);

        // Generate unique patient ID
        FirebaseFirestoreHelper.getInstance().generateUniquePatientId(
            patientId -> {
                patientIdTextView.setText("Your Patient ID: " + patientId);
                completeRegistrationButton.setOnClickListener(v -> completePatientRegistration(patientId));
            },
            e -> {
                Toast.makeText(this, "Error generating patient ID: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        );
    }

    private void completePatientRegistration(String patientId) {
        progressBar.setVisibility(android.view.View.VISIBLE);
        completeRegistrationButton.setEnabled(false);

        FirebaseUser currentUser = FirebaseAuthHelper.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Get display name from the current user profile (set during signup)
            String displayName = currentUser.getDisplayName();
            User user = new User(currentUser.getUid(), currentUser.getEmail(), "patient", displayName);
            user.setPatientId(patientId);

            FirebaseFirestoreHelper.getInstance().saveUser(user, task -> {
                progressBar.setVisibility(android.view.View.GONE);
                completeRegistrationButton.setEnabled(true);

                if (task.isSuccessful()) {
                    Toast.makeText(this, "Patient registration completed!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PatientRegistrationActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), 
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
