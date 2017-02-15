package com.example.stpl.cameraapp;

import com.example.stpl.cameraapp.models.MediaDetails;

/**
 * Created by stpl on 2/15/2017.
 */

public interface FileListener {
    void onFileDeleted(MediaDetails mediaDetails);

    void onErrorOccurred();

    void onFileAdded(MediaDetails mediaDetails);
}
