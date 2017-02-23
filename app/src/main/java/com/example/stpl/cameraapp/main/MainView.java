package com.example.stpl.cameraapp.main;


import com.example.stpl.cameraapp.FileListener;
import com.example.stpl.cameraapp.models.MediaDetails;

import java.util.ArrayList;
import java.util.List;


interface MainView extends FileListener {
    interface UpdateView {
        void setTimerValue(String timer);

        void updateAdapter(List<MediaDetails> mediaDetails);


    }
    void permissionAvailable();

    boolean addPermission(List<String> permissionsList, String permission);

    void permissionNotAvailable(ArrayList<String> permissionNeeded, ArrayList<String>
            permissionList);

    void itemAdd(MediaDetails mediaDetails);


    //    interface FileListener {
//        void onFileDeleted(MediaDetails mediaDetails);
//        void onErrorOccurred();
//        void onFileAdded(MediaDetails mediaDetails);
//    }


}
