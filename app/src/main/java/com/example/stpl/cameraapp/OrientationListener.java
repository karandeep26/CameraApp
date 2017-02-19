package com.example.stpl.cameraapp;

import android.content.Context;
import android.view.OrientationEventListener;

import rx.Observable;
import rx.subjects.PublishSubject;

import static com.example.stpl.cameraapp.Utils.ROTATION_180;
import static com.example.stpl.cameraapp.Utils.ROTATION_270;
import static com.example.stpl.cameraapp.Utils.ROTATION_90;
import static com.example.stpl.cameraapp.Utils.ROTATION_O;

/**
 * Created by karan on 18/2/17.
 */

class OrientationListener extends OrientationEventListener {
    private int rotation = 0;
    PublishSubject<Integer> rotationSubject = PublishSubject.create();

    OrientationListener(Context context) {
        super(context);
    }
    public Observable<Integer> _getRotation() {
        return rotationSubject;
    }


    @Override
    public void onOrientationChanged(int orientation) {

        if ((orientation < 35 || orientation > 325) && rotation != ROTATION_O) { // PORTRAIT
            rotation = ROTATION_O;
        } else if (orientation > 145 && orientation < 215 && rotation != ROTATION_180) { //
            // REVERSE PORTRAIT
            rotation = ROTATION_180;
        } else if (orientation > 55 && orientation < 125 && rotation != ROTATION_270) { //
            // REVERSE LANDSCAPE
            rotation = ROTATION_270;

        } else if (orientation > 235 && orientation < 305 && rotation != ROTATION_90) {
            //LANDSCAPE
            rotation = ROTATION_90;
        }
        rotationSubject.onNext(rotation);

    }

    int getRotation() {
        return rotation;
    }
}

