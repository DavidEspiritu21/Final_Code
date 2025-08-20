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
import com.google.firebase.firestore.DocumentSnapshot;

public class ConnectPatientActivity extends AppCompatActivity {

    private EditText patientIdEditText;
    private Button connectButton, skipButton;
    private ProgressBar progressBar;
    private TextView patientInfoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_patient);

        patientIdEditText = findViewById(R.id.patientIdEditText);
        connectButton = findViewById(R.id.connectButton);
        skipButton = findViewById(R.id.skipButton);
        progressBar = findViewById(R.id.progressBar);
        patientInfoTextView = findViewById(R.id.patientInfoTextView);

        connectButton.setOnClickListener(v -> connectToPatient());
        skipButton.setOnClickListener(v -> {
            Intent intent = new Intent(ConnectPatientActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void connectToPatient() {
        String patientId = patientIdEditText.getText().toString().trim().toUpperCase();

        if (patientId.isEmpty()) {
            patientIdEditText.setError("Please enter patient ID");
            return;
        }

        // Validate patient ID format
        if (!isValidPatientIdFormat(patientId)) {
            patientIdEditText.setError("Invalid patient ID format");
            return;
        }

        progressBar.setVisibility(android.view.View.VISIBLE);
        connectButton.setEnabled(false);
        patientInfoTextView.setText("");

        // Check if patient ID exists
        FirebaseFirestoreHelper.getInstance().checkPatientIdExists(patientId, task -> {
            if (task.isSuccessful()) {
                if (!task.getResult().isEmpty()) {
                    // Patient found
                    DocumentSnapshot patientDoc = task.getResult().getDocuments().get(0);
                    User patient = patientDoc.toObject(User.class);
                    
                    if (patient != null) {
                        String patientName = patient.getDisplayName();
                        String patientEmail = patient.getEmail();
                        
                        runOnUiThread(() -> {
                            progressBar.setVisibility(android.view.View.GONE);
                            connectButton.setEnabled(true);
                            
                            patientInfoTextView.setText(String.format("Patient Found:\nName: %s\nEmail: %s", 
                                    patientName, patientEmail));
                            
                            showConnectionConfirmation(patient);
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(android.view.View.GONE);
                        connectButton.setEnabled(true);
                        patientInfoTextView.setText("");
                        Toast.makeText(this, "Patient ID not found. Please check the ID and try again.", Toast.LENGTH_LONG).show();
                    });
                }
            } else {
                runOnUiThread(() -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    connectButton.setEnabled(true);
                    patientInfoTextView.setText("");
                    Toast.makeText(this, "Error checking patient ID. Please try again.", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private boolean isValidPatientIdFormat(String patientId) {
        // Check if it starts with P and has 6-8 characters
        return patientId.matches("^P[A-Z0-9]{5,7}$");
    }

    private void showConnectionConfirmation(User patient) {
        FirebaseUser currentUser = FirebaseAuthHelper.getInstance().getCurrentUser();
        if (currentUser == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Send Connection Request")
                .setMessage(String.format("Send connection request to %s (%s)?\n\nOnce connected, you'll be able to monitor their activities.", 
                        patient.getDisplayName(), patient.getEmail()))
                .setPositiveButton("Send Request", (dialog, which) -> sendConnectionRequest(patient))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void sendConnectionRequest(User patient) {
        FirebaseUser currentUser = FirebaseAuthHelper.getInstance().getCurrentUser();
        if (currentUser == null) return;

        progressBar.setVisibility(android.view.View.VISIBLE);
        connectButton.setEnabled(false);

        FirebaseFirestoreHelper.getInstance().sendConnectionRequest(
                currentUser.getUid(),
                currentUser.getDisplayName() != null ? currentUser.getDisplayName() : currentUser.getEmail(),
                patient.getPatientId(),
                patient.getDisplayName(),
                aVoid -> {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(android.view.View.GONE);
                        connectButton.setEnabled(true);
                        Toast.makeText(this, "Connection request sent to " + patient.getDisplayName() + "!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ConnectPatientActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    });
                },
                e -> {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(android.view.View.GONE);
                        connectButton.setEnabled(true);
                        Toast.makeText(this, "Failed to send request: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
        );
    }
}
