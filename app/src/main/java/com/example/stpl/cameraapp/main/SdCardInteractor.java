package com.example.stpl.cameraapp.main;

import com.example.stpl.cameraapp.models.MediaDetails;

import rx.Observable;

/**
 * Created by stpl on 1/20/2017.
 */

public interface SdCardInteractor {
    Observable<MediaDetails> getFromSdCard();

    interface OnFinishedListener {
        void onFinished();
    }


}
