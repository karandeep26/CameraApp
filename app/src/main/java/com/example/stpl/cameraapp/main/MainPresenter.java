package com.example.stpl.cameraapp.main;

import com.example.stpl.cameraapp.models.MediaDetails;

import java.util.ArrayList;

import rx.Subscription;

/**
 * Created by stpl on 1/20/2017.
 */

public interface MainPresenter {
    void checkForPermissions();

    void fetchFromSdCard();

    void deleteFromSdCard(ArrayList<MediaDetails> mediaDetails);

    void onDestroy();

    Subscription startTimer();

    int getMediaSize(String mediaType);

    void savePhotoSdCard(byte[] data);

    void getCurrentSavedVideo(String fileName);


}
