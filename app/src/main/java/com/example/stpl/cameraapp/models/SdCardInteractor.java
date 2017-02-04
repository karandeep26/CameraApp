package com.example.stpl.cameraapp.models;


import java.util.ArrayList;

import rx.Observable;


public interface SdCardInteractor {
    Observable<MediaDetails> getFromSdCard();

    boolean deleteFromSdCard(MediaDetails mediaDetails);

    int getMediaCount(String type);

    MediaDetails savePhoto(byte[] data);

    MediaDetails getSavedVideo(String fileName);


    interface OnFinishedListener {
        void onFinished();
    }

    interface GetMediaList {
        ArrayList<MediaDetails> getMedia(String type);
    }


}
