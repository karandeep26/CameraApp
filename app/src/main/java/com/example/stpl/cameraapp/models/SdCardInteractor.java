package com.example.stpl.cameraapp.models;


import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;


public interface SdCardInteractor {
    Single<List<MediaDetails>> getFromSdCard(String type);

    boolean deleteFromSdCard(MediaDetails mediaDetails);

    int getMediaCount(String type);

    MediaDetails savePhoto(byte[] data);

    MediaDetails getSavedVideo(String fileName);


    interface OnFinishedListener {
        void onFinished();
    }

    interface Selection {
        void removeFromSelection(MediaDetails mediaDetails);

        void addToSelection(MediaDetails mediaDetails);

        boolean isSelectionMode();

        void clearAll();

        ArrayList<MediaDetails> getSelectedItems();
    }

    interface GetMediaList {
        ArrayList<MediaDetails> getMedia(String type);

    }


}
