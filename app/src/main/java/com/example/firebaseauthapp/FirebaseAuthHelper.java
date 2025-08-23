package com.example.firebaseauthapp;

import android.content.Context;
import android.util.Log; // Added for logging
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest; // Added for profile updates

public class FirebaseAuthHelper {
    private static FirebaseAuthHelper instance;
    private FirebaseAuth mAuth;
    private Context context; // Keep context if needed, though not directly used in the modified signUp
    private static final String TAG = "FirebaseAuthHelper"; // Added for logging

    private FirebaseAuthHelper(Context context) {
        this.context = context.getApplicationContext(); // Use application context
        mAuth = FirebaseAuth.getInstance();
    }

    public static synchronized FirebaseAuthHelper getInstance() {
        // Instance should be initialized via initialize() method first
        if (instance == null) {
            // This is a fallback, ideally initialize() is called from Application class
            Log.e(TAG, "FirebaseAuthHelper not initialized. Call initialize() first.");
            // Throw an exception or handle appropriately if context is critical before init
            // For now, let's assume it might be called late and proceed with caution
            // or rely on a subsequent initialize() call.
        }
        return instance;
    }

    // It's good practice to ensure context is passed for initialization
    public static synchronized void initialize(Context context) {
        if (instance == null) {
            instance = new FirebaseAuthHelper(context.getApplicationContext());
        }
    }

    public void signInWithEmail(String email, String password, AuthCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            callback.onSuccess(user);
                        } else {
                            callback.onFailure(task.getException().getMessage());
                        }
                    }
                });
    }

    // Modified to accept fullName
    public void signUpWithEmail(String email, String password, String fullName, AuthCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(fullName)
                                        .build();
                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(profileTask -> {
                                            if (profileTask.isSuccessful()) {
                                                Log.d(TAG, "User profile updated with fullName.");
                                            } else {
                                                Log.w(TAG, "Failed to update user profile.", profileTask.getException());
                                            }
                                            // Callback success regardless of profile update success for now
                                            // Or handle profile update failure more specifically if needed
                                            callback.onSuccess(user);
                                        });
                            } else {
                                // Should not happen if createUserWithEmailAndPassword was successful
                                callback.onFailure("User creation succeeded but user object is null.");
                            }
                        } else {
                            callback.onFailure(task.getException().getMessage());
                        }
                    }
                });
    }

    public void signOut() {
        mAuth.signOut();
    }

    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String error);
    }
}
