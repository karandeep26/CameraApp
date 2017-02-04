package com.example.stpl.cameraapp.main;

import com.example.stpl.cameraapp.models.MediaDetails;

import java.util.ArrayList;


interface MainView {
    void permissionAvailable();

    void permissionNotAvailable(ArrayList<String> permissionNeeded, ArrayList<String>
            permissionList);

    void itemAdd(MediaDetails mediaDetails);

    void setTimerValue(String timer);

    interface FileListener {
        void onFileDeleted(MediaDetails mediaDetails);
        void onErrorOccurred();

        void onFileAdded(MediaDetails mediaDetails);
    }

    void updateAdapter(ArrayList<MediaDetails> mediaDetails);


}
