package com.example.firebaseauthapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firebaseauthapp.models.User;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    private TextView welcomeTextView, emailTextView, roleTextView, patientIdTextView;
    private Button logoutButton, viewConnectionsButton;
    private ImageButton profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        welcomeTextView = findViewById(R.id.welcomeTextView);
        emailTextView = findViewById(R.id.emailTextView);
        roleTextView = findViewById(R.id.roleTextView);
        patientIdTextView = findViewById(R.id.patientIdTextView);
        logoutButton = findViewById(R.id.logoutButton);
        viewConnectionsButton = findViewById(R.id.viewConnectionsButton);
        profileButton = findViewById(R.id.profileButton);

        loadUserProfile();

        // Set logout listener
        logoutButton.setOnClickListener(v -> logoutUser());
        viewConnectionsButton.setOnClickListener(v -> viewConnections());
        
        // Set profile button listener
        profileButton.setOnClickListener(v -> openProfileActivity());
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = FirebaseAuthHelper.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestoreHelper.getInstance().getUser(currentUser.getUid(), task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    User user = task.getResult().toObject(User.class);
                    if (user != null) {
                        welcomeTextView.setText("Welcome, " + user.getDisplayName() + "!");
                        emailTextView.setText("Email: " + user.getEmail());
                        roleTextView.setText("Role: " + user.getRole());
                        
                        if ("patient".equals(user.getRole())) {
                            patientIdTextView.setText("Patient ID: " + user.getPatientId());
                            viewConnectionsButton.setText("View Guardian Requests");
                        } else {
                            patientIdTextView.setText("Guardian Dashboard");
                            viewConnectionsButton.setText("View Connected Patients");
                        }
                    }
                }
            });
        }
    }

    private void viewConnections() {
        FirebaseUser currentUser = FirebaseAuthHelper.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestoreHelper.getInstance().getUser(currentUser.getUid(), task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    User user = task.getResult().toObject(User.class);
                    if (user != null) {
                        if ("patient".equals(user.getRole())) {
                            // Show connection requests for patients
                            Toast.makeText(this, "Connection requests feature coming soon!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Show connected patients for guardians
                            Toast.makeText(this, "Connected patients feature coming soon!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    private void logoutUser() {
        FirebaseAuthHelper.getInstance().signOut();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        
        // Navigate back to login
        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        finish();
    }
}
