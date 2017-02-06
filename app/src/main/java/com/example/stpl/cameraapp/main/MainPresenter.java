package com.example.stpl.cameraapp.main;

import com.example.stpl.cameraapp.models.MediaDetails;

import java.util.ArrayList;

import rx.Subscription;


public interface MainPresenter {
    void checkForPermissions();

    void fetchFromSdCard(String mediaType);

    void deleteFromSdCard(ArrayList<MediaDetails> mediaDetails);

    void onDestroy();

    Subscription startTimer();

    int getMediaSize(String mediaType);

    void savePhotoSdCard(byte[] data);

    void getCurrentSavedVideo(String fileName);

    void removeSelectedItems();
    boolean modifySelection(MediaDetails mediaDetail);
    interface OnItemClick{
        boolean isSelectionMode();
    }
    interface  Adapter{
        void updateAdapter(String mediaType);

    }


}
