package com.example.firebaseauthapp.models;

public class ConnectionRequest {
    private String requestId;
    private String guardianId;
    private String guardianName;
    private String patientId;
    private String patientName;
    private String status; // "pending", "accepted", "rejected"
    private long createdAt;
    private long respondedAt;

    public ConnectionRequest() {
        // Required empty constructor for Firestore
    }

    public ConnectionRequest(String guardianId, String guardianName, String patientId, String patientName) {
        this.guardianId = guardianId;
        this.guardianName = guardianName;
        this.patientId = patientId;
        this.patientName = patientName;
        this.status = "pending";
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and setters
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getGuardianId() { return guardianId; }
    public void setGuardianId(String guardianId) { this.guardianId = guardianId; }

    public String getGuardianName() { return guardianName; }
    public void setGuardianName(String guardianName) { this.guardianName = guardianName; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getRespondedAt() { return respondedAt; }
    public void setRespondedAt(long respondedAt) { this.respondedAt = respondedAt; }
}
