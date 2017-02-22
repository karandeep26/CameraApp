package com.example.stpl.cameraapp;

import android.os.FileObserver;

/**
 * Created by stpl on 2/22/2017.
 */

public class MyFileObserver extends FileObserver {

    public MyFileObserver(String path) {
        super(path);
    }

    @Override
    public void onEvent(int event, String path) {

    }
}
