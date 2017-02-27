package com.example.stpl.cameraapp.main;

/**
 * Created by stpl on 2/23/2017.
 */

public interface FirebaseMainPresenter {
    void uploadToCloud(String filename);

    void downloadFromCloud();
}
