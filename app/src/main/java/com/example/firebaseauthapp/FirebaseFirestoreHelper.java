package com.example.firebaseauthapp;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.firebaseauthapp.models.ConnectionRequest;
import com.example.firebaseauthapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FirebaseFirestoreHelper {
    private static final String TAG = "FirestoreHelper";
    private static FirebaseFirestoreHelper instance;
    private FirebaseFirestore db;

    private FirebaseFirestoreHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized FirebaseFirestoreHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseFirestoreHelper();
        }
        return instance;
    }

    // Generate unique patient ID
    public void generateUniquePatientId(OnSuccessListener<String> successListener, OnFailureListener failureListener) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        
        generatePatientIdRecursive(chars, random, successListener, failureListener);
    }

    private void generatePatientIdRecursive(String chars, Random random, OnSuccessListener<String> successListener, OnFailureListener failureListener) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        String patientId = "P" + sb.toString();
        
        checkPatientIdExists(patientId, exists -> {
            if (exists) {
                // ID exists, generate a new one
                generatePatientIdRecursive(chars, random, successListener, failureListener);
            } else {
                // ID is unique, return it
                successListener.onSuccess(patientId);
            }
        }, failureListener);
    }

    private void checkPatientIdExists(String patientId, OnSuccessListener<Boolean> resultListener, OnFailureListener failureListener) {
        db.collection("users")
                .whereEqualTo("patientId", patientId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean exists = !task.getResult().isEmpty();
                        resultListener.onSuccess(exists);
                    } else {
                        failureListener.onFailure(task.getException());
                    }
                })
                .addOnFailureListener(failureListener);
    }

    // Save user profile
    public void saveUser(User user, OnCompleteListener<Void> listener) {
        db.collection("users").document(user.getUserId())
                .set(user)
                .addOnCompleteListener(listener);
    }

    // Get user profile
    public void getUser(String userId, OnCompleteListener<DocumentSnapshot> listener) {
        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(listener);
    }

    // Check if patient ID exists
    public void checkPatientIdExists(String patientId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("users")
                .whereEqualTo("patientId", patientId)
                .whereEqualTo("role", "patient")
                .get()
                .addOnCompleteListener(listener);
    }

    // Send connection request
    public void sendConnectionRequest(String guardianId, String guardianName, String patientId, String patientName, 
                                    OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        ConnectionRequest request = new ConnectionRequest(guardianId, guardianName, patientId, patientName);
        
        db.collection("connectionRequests")
                .add(request)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Connection request sent successfully");
                    successListener.onSuccess(null);
                })
                .addOnFailureListener(failureListener);
    }

    // Get pending connection requests for a patient
    public void getPendingRequests(String patientId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("connectionRequests")
                .whereEqualTo("patientId", patientId)
                .whereEqualTo("status", "pending")
                .get()
                .addOnCompleteListener(listener);
    }

    // Update connection request status
    public void updateConnectionRequestStatus(String requestId, String status, OnSuccessListener<Void> successListener) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);
        updates.put("respondedAt", System.currentTimeMillis());

        db.collection("connectionRequests").document(requestId)
                .update(updates)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(e -> Log.e(TAG, "Error updating connection request", e));
    }

    // Get connected guardians for a patient
    public void getConnectedGuardians(String patientId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("connectionRequests")
                .whereEqualTo("patientId", patientId)
                .whereEqualTo("status", "accepted")
                .get()
                .addOnCompleteListener(listener);
    }

    // Get connected patients for a guardian
    public void getConnectedPatients(String guardianId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("connectionRequests")
                .whereEqualTo("guardianId", guardianId)
                .whereEqualTo("status", "accepted")
                .get()
                .addOnCompleteListener(listener);
    }
}
