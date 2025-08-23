package com.example.firebaseauthapp;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
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
        try {
            // Use AtomicInteger for nextId
            AtomicInteger nextId = new AtomicInteger(MIN_ID);
            
            // Query for existing patient IDs
            db.collection("users")
                    .whereEqualTo("role", "patient")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        try {
                            int maxId = MIN_ID - 1;
                            
                            // Process all documents to find the highest ID
                            for (com.google.firebase.firestore.DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                Object patientIdObj = doc.get("patientId");
                                if (patientIdObj != null) {
                                    String patientId = patientIdObj.toString();
                                    if (patientId.startsWith(PREFIX) && patientId.length() == 7) {
                                        try {
                                            String numericStr = patientId.substring(1);
                                            int id = Integer.parseInt(numericStr);
                                            if (id >= MIN_ID && id <= MAX_ID && id > maxId) {
                                                maxId = id;
                                            }
                                        } catch (NumberFormatException e) {
                                            Log.w(TAG, "Invalid patient ID format: " + patientId);
                                        }
                                    }
                                }
                            }
                            
                            // Calculate next ID
                            if (maxId >= MIN_ID) {
                                nextId.set(maxId + 1); // Use .set() for AtomicInteger
                            }
                            
                            // Check if we've exceeded the range
                            if (nextId.get() > MAX_ID) { // Use .get() for AtomicInteger
                                // Use timestamp-based ID as fallback
                                long timestamp = System.currentTimeMillis();
                                nextId.set((int)(timestamp % 900000) + 100000); // Use .set()
                            }
                            
                            String newPatientId = PREFIX + String.format("%06d", nextId.get()); // Use .get()
                            verifyPatientIdUnique(newPatientId, callback);
                            
                        } catch (Exception e) {
                            Log.e(TAG, "Error processing patient IDs", e);
                            // Fallback to a safe ID
                            String fallbackId = PREFIX + String.format("%06d", MIN_ID);
                            verifyPatientIdUnique(fallbackId, callback);
                        }
                    })
                    .addOnFailureListener(callback::onFailure);
                    
        } catch (Exception e) {
            Log.e(TAG, "Critical error in generateSequentialPatientId", e);
            callback.onFailure(e);
        }
    }
    
    private void verifyPatientIdUnique(String patientId, PatientIdCallback callback) {
        try {
            db.collection("users")
                    .whereEqualTo("patientId", patientId)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (querySnapshot.isEmpty()) {
                            callback.onSuccess(patientId);
                        } else {
                            // ID exists, try next one
                            try {
                                String numericStr = patientId.substring(1);
                                int currentId = Integer.parseInt(numericStr);
                                int nextVal = currentId + 1; // Renamed to avoid confusion
                                
                                if (nextVal > MAX_ID) {
                                    nextVal = MIN_ID;
                                }
                                
                                String newPatientId = PREFIX + String.format("%06d", nextVal);
                                verifyPatientIdUnique(newPatientId, callback);
                            } catch (Exception e) {
                                // Final fallback
                                long timestamp = System.currentTimeMillis();
                                String fallbackId = PREFIX + String.format("%06d", 
                                    (int)((timestamp % 900000) + 100000));
                                callback.onSuccess(fallbackId);
                            }
                        }
                    })
                    .addOnFailureListener(callback::onFailure);
        } catch (Exception e) {
            Log.e(TAG, "Critical error in verifyPatientIdUnique", e);
            callback.onFailure(e);
        }
    }
}
