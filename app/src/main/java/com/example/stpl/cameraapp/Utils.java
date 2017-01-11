package com.example.stpl.cameraapp;

import android.os.Environment;

import java.io.File;

/**
 * Created by stpl on 1/11/2017.
 */

public class Utils {
    static public File mediaStorageDir = new File(
            Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "MyCameraApp");
}
