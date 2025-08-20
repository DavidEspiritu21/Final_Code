package com.example.firebaseauthapp;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.concurrent.atomic.AtomicInteger;

public class PatientIdGenerator {
    private static final String TAG = "PatientIdGenerator";
    private static final String PREFIX = "P";
    private static final int MIN_ID = 100000;
    private static final int MAX_ID = 999999;
    
    private final FirebaseFirestore db;
    
    public PatientIdGenerator() {
        this.db = FirebaseFirestore.getInstance();
    }
    
    public interface PatientIdCallback {
        void onSuccess(String patientId);
        void onFailure(Exception e);
    }
    
    public void generateSequentialPatientId(PatientIdCallback callback) {
        // Get the highest current patient ID by querying all patients and finding the max
        db.collection("users")
                .whereEqualTo("role", "patient")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int maxId = MIN_ID - 1;
                    
                    for (com.google.firebase.firestore.DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String patientId = doc.getString("patientId");
                        if (patientId != null && patientId.startsWith(PREFIX)) {
                            try {
                                String numericPart = patientId.substring(1);
                                int id = Integer.parseInt(numericPart);
                                if (id > maxId && id <= MAX_ID) {
                                    maxId = id;
                                }
                            } catch (NumberFormatException e) {
                                // Skip invalid IDs
                            }
                        }
                    }
                    
                    int nextId = maxId + 1;
                    
                    if (nextId > MAX_ID) {
                        // Fallback to timestamp-based ID if we exceed max
                        generateTimestampBasedPatientId(callback);
                    } else {
                        String newPatientId = PREFIX + String.format("%06d", nextId);
                        verifyPatientIdUnique(newPatientId, callback);
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }
    
    private void generateTimestampBasedPatientId(PatientIdCallback callback) {
        // Use timestamp as fallback for unique ID
        long timestamp = System.currentTimeMillis();
        String patientId = PREFIX + (timestamp % 1000000);
        verifyPatientIdUnique(patientId, callback);
    }
    
    private void verifyPatientIdUnique(String patientId, PatientIdCallback callback) {
        db.collection("users")
                .whereEqualTo("patientId", patientId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        callback.onSuccess(patientId);
                    } else {
                        // ID exists, generate a new one with offset
                        String newPatientId = patientId + "1";
                        if (newPatientId.length() > 7) {
                            newPatientId = PREFIX + ((Integer.parseInt(patientId.substring(1)) + 1) % 1000000);
                        }
                        verifyPatientIdUnique(newPatientId, callback);
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }
}
