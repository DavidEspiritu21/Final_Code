package com.example.firebaseauthapp.models;

public class User {
    private String userId;
    private String email;
    private String role; // "patient" or "guardian"
    private String patientId; // Only for patients
    private String displayName;
    private long createdAt;

    public User() {
        // Required empty constructor for Firestore
    }

    public User(String userId, String email, String role, String displayName) {
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.displayName = displayName;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
