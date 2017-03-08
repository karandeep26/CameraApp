package com.example.stpl.cameraapp.main;

import com.example.stpl.cameraapp.models.MediaDetails;

import io.reactivex.observers.DisposableObserver;


public interface MainPresenter {
    void checkForPermissions();

    void fetchFromSdCard(String mediaType);

    void deleteFromSdCard();

    public MediaDetails getSelected();

    void onDestroy();

    DisposableObserver<Integer> startTimer();

    int getMediaSize(String mediaType);

    String savePhotoSdCard(byte[] data);

    void getCurrentSavedVideo(String fileName);

    void removeSelectedItems();

    boolean modifySelection(MediaDetails mediaDetail);

    interface OnItemClick {
        boolean isSelectionMode();
    }

    interface Adapter {
        void updateAdapter(String mediaType);
    }


}
