package com.example.stpl.cameraapp.main;

import com.google.firebase.auth.FirebaseAuth;


class FirebaseLoginImpl implements FirebaseLoginPresenter {
    private FirebaseAuth firebaseAuth;
    private FirebaseLoginView firebaseLoginView;

    FirebaseLoginImpl(FirebaseLoginView firebaseLoginView) {
        firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseLoginView = firebaseLoginView;
    }

    @Override
    public void checkLoginBeforeProceed() {
        if (firebaseAuth.getCurrentUser() == null) {
            firebaseLoginView.moveToLogin();
        } else {
            firebaseLoginView.loggedIn();
        }

    }
}
