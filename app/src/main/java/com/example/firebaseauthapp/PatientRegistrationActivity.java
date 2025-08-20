package com.example.firebaseauthapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firebaseauthapp.models.User;
import com.google.firebase.auth.FirebaseUser;

public class PatientRegistrationActivity extends AppCompatActivity {

    private Button completeRegistrationButton;
    private ProgressBar progressBar;
    private TextView patientIdTextView;
    private String currentPatientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_registration);

        completeRegistrationButton = findViewById(R.id.completeRegistrationButton);
        progressBar = findViewById(R.id.progressBar);
        patientIdTextView = findViewById(R.id.patientIdTextView);

        generatePatientId();
    }

    private void generatePatientId() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        completeRegistrationButton.setEnabled(false);
        
        // Generate unique patient ID
        FirebaseFirestoreHelper.getInstance().generateUniquePatientId(
            patientId -> {
                currentPatientId = patientId;
                runOnUiThread(() -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    completeRegistrationButton.setEnabled(true);
                    patientIdTextView.setText("Your Patient ID: " + patientId);
                    completeRegistrationButton.setOnClickListener(v -> showConfirmationDialog());
                });
            },
            e -> {
                runOnUiThread(() -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    completeRegistrationButton.setEnabled(true);
                    showRetryDialog("Error generating patient ID: " + e.getMessage());
                });
            }
        );
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Registration")
                .setMessage("Your Patient ID is: " + currentPatientId + "\n\nThis ID will be used by guardians to connect with you. Please save this ID for future reference.")
                .setPositiveButton("Confirm", (dialog, which) -> completePatientRegistration())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showRetryDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Registration Error")
                .setMessage(message)
                .setPositiveButton("Retry", (dialog, which) -> generatePatientId())
                .setNegativeButton("Cancel", (dialog, which) -> finish())
                .show();
    }

    private void completePatientRegistration() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        completeRegistrationButton.setEnabled(false);

        FirebaseUser currentUser = FirebaseAuthHelper.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Get display name from the current user profile (set during signup)
            String displayName = currentUser.getDisplayName();
            User user = new User(currentUser.getUid(), currentUser.getEmail(), "patient", displayName);
            user.setPatientId(currentPatientId);

            FirebaseFirestoreHelper.getInstance().saveUser(user, task -> {
                progressBar.setVisibility(android.view.View.GONE);
                completeRegistrationButton.setEnabled(true);

                if (task.isSuccessful()) {
                    Toast.makeText(this, "Patient registration completed! Your ID: " + currentPatientId, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(PatientRegistrationActivity.this, HomeActivity.class);
                    intent.putExtra("patientId", currentPatientId);
                    startActivity(intent);
                    finish();
                } else {
                    showRetryDialog("Registration failed: " + task.getException().getMessage());
                }
            });
        }
    }
}
