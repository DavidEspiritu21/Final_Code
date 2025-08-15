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

    private EditText displayNameEditText;
    private Button completeRegistrationButton;
    private ProgressBar progressBar;
    private TextView patientIdTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_registration);

        displayNameEditText = findViewById(R.id.displayNameEditText);
        completeRegistrationButton = findViewById(R.id.completeRegistrationButton);
        progressBar = findViewById(R.id.progressBar);
        patientIdTextView = findViewById(R.id.patientIdTextView);

        // Generate patient ID
        String patientId = FirebaseFirestoreHelper.getInstance().generatePatientId();
        patientIdTextView.setText("Your Patient ID: " + patientId);

        completeRegistrationButton.setOnClickListener(v -> completePatientRegistration(patientId));
    }

    private void completePatientRegistration(String patientId) {
        String displayName = displayNameEditText.getText().toString().trim();

        if (displayName.isEmpty()) {
            displayNameEditText.setError("Please enter your name");
            return;
        }

        progressBar.setVisibility(android.view.View.VISIBLE);
        completeRegistrationButton.setEnabled(false);

        FirebaseUser currentUser = FirebaseAuthHelper.getInstance().getCurrentUser();
        if (currentUser != null) {
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
