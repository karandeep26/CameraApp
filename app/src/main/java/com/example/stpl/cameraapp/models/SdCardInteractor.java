package com.example.stpl.cameraapp.models;


import java.util.ArrayList;
import java.util.List;

import rx.Observable;


public interface SdCardInteractor {
    Observable<List<MediaDetails>> getFromSdCard(String type);

    boolean deleteFromSdCard(MediaDetails mediaDetails);

    int getMediaCount(String type);

    MediaDetails savePhoto(byte[] data);

    MediaDetails getSavedVideo(String fileName);


    interface OnFinishedListener {
        void onFinished();
    }
    interface Selection{
        void remove(MediaDetails mediaDetails);
        void add(MediaDetails mediaDetails);
        boolean isSelectionMode();
        void clearAll();
    }

    interface GetMediaList {
        ArrayList<MediaDetails> getMedia(String type);

    }


}
