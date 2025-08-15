package com.example.firebaseauthapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firebaseauthapp.models.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

public class ConnectPatientActivity extends AppCompatActivity {

    private EditText patientIdEditText;
    private Button connectButton, skipButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_patient);

        patientIdEditText = findViewById(R.id.patientIdEditText);
        connectButton = findViewById(R.id.connectButton);
        skipButton = findViewById(R.id.skipButton);
        progressBar = findViewById(R.id.progressBar);

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

        progressBar.setVisibility(android.view.View.VISIBLE);
        connectButton.setEnabled(false);

        // Check if patient ID exists
        FirebaseFirestoreHelper.getInstance().checkPatientIdExists(patientId, task -> {
            if (task.isSuccessful()) {
                if (!task.getResult().isEmpty()) {
                    // Patient found, send connection request
                    DocumentSnapshot patientDoc = task.getResult().getDocuments().get(0);
                    User patient = patientDoc.toObject(User.class);
                    
                    FirebaseUser currentUser = FirebaseAuthHelper.getInstance().getCurrentUser();
                    if (currentUser != null && patient != null) {
                        FirebaseFirestoreHelper.getInstance().sendConnectionRequest(
                                currentUser.getUid(),
                                currentUser.getEmail(),
                                patient.getPatientId(),
                                patient.getDisplayName(),
                                aVoid -> {
                                    progressBar.setVisibility(android.view.View.GONE);
                                    connectButton.setEnabled(true);
                                    Toast.makeText(this, "Connection request sent to patient!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ConnectPatientActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                },
                                e -> {
                                    progressBar.setVisibility(android.view.View.GONE);
                                    connectButton.setEnabled(true);
                                    Toast.makeText(this, "Failed to send request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                        );
                    }
                } else {
                    progressBar.setVisibility(android.view.View.GONE);
                    connectButton.setEnabled(true);
                    Toast.makeText(this, "Patient ID not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                progressBar.setVisibility(android.view.View.GONE);
                connectButton.setEnabled(true);
                Toast.makeText(this, "Error checking patient ID", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
