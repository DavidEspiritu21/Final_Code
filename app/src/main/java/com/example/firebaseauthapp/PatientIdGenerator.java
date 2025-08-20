package com.example.firebaseauthapp;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

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
        // Get the highest current patient ID
        db.collection("users")
                .whereGreaterThan("patientId", PREFIX + MIN_ID)
                .whereLessThan("patientId", PREFIX + MAX_ID)
                .orderBy("patientId", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int nextId = MIN_ID;
                    
                    if (!querySnapshot.isEmpty()) {
                        String lastId = querySnapshot.getDocuments().get(0).getString("patientId");
                        if (lastId != null && lastId.startsWith(PREFIX)) {
                            try {
                                int numericPart = Integer.parseInt(lastId.substring(1));
                                nextId = numericPart + 1;
                            } catch (NumberFormatException e) {
                                // Fallback to random generation
                                generateRandomPatientId(callback);
                                return;
                            }
                        }
                    }
                    
                    if (nextId > MAX_ID) {
                        // Fallback to random generation if we exceed max
                        generateRandomPatientId(callback);
                    } else {
                        String newPatientId = PREFIX + String.format("%06d", nextId);
                        verifyPatientIdUnique(newPatientId, callback);
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }
    
    private void generateRandomPatientId(PatientIdCallback callback) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < 6; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        
        String patientId = PREFIX + sb.toString();
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
                        // ID exists, try again
                        generateRandomPatientId(callback);
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }
}
