package com.example.stpl.cameraapp.models;


import rx.Observable;

/**
 * Created by stpl on 1/20/2017.
 */

public interface SdCardInteractor {
    Observable<MediaDetails> getFromSdCard();

    boolean deleteFromSdCard(MediaDetails mediaDetails);

    interface OnFinishedListener {
        void onFinished();
    }


}