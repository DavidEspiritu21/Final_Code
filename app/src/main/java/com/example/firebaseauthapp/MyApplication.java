package com.example.firebaseauthapp;

import android.app.Application;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseAuthHelper.initialize(getApplicationContext());
    }
}
