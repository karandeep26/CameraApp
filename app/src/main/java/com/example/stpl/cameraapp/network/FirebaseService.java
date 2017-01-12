package com.example.stpl.cameraapp.network;

import android.support.annotation.NonNull;

import com.example.stpl.cameraapp.LoginInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by stpl on 1/12/2017.
 */

public class FirebaseService implements FirebaseAuth.AuthStateListener, OnCompleteListener {
    private static FirebaseService mFirebaseService;
    private FirebaseAuth mAuth;
    private LoginInterface mLoginInterface;

    public static FirebaseService getInstance() {
        if (mFirebaseService == null)
            mFirebaseService = new FirebaseService();
        return mFirebaseService;
    }

    public void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void setmLoginInterface(LoginInterface mLoginInterface) {
        this.mLoginInterface = mLoginInterface;
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public void setAuthListener() {
        mAuth.addAuthStateListener(this);
    }

    public void removeAuthListener() {
        mAuth.removeAuthStateListener(this);
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

    }

    @Override
    public void onComplete(@NonNull Task task) {

        if (task.isSuccessful()) {
            mLoginInterface.onSuccess();
        } else {
            mLoginInterface.onFailure();
        }


    }
}


