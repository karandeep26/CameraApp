package com.example.stpl.cameraapp.login;

import com.google.firebase.auth.FirebaseAuth;


public class FirebaseLoginImpl implements FirebaseLoginPresenter {
    private FirebaseAuth firebaseAuth;
    private FirebaseLoginView firebaseLoginView;

    public FirebaseLoginImpl(FirebaseLoginView firebaseLoginView) {
        firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseLoginView = firebaseLoginView;
    }

    @Override
    public void checkLoginBeforeProceed() {
        if (firebaseAuth.getCurrentUser() == null) {
            firebaseLoginView.moveToLogin();
        } else {
            firebaseLoginView.loggedIn(firebaseAuth.getCurrentUser().getEmail());
        }

    }
}
