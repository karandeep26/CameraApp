package com.example.stpl.cameraapp.main;

import android.widget.GridView;

import com.example.stpl.cameraapp.adapters.GridViewAdapter;
import com.example.stpl.cameraapp.models.MediaDetails;

import java.util.ArrayList;


public interface MainView {
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
    interface Adapter {
        void updateAdapter(ArrayList<MediaDetails> mediaDetails);
    }

}
