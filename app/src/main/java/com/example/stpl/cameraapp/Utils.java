package com.example.stpl.cameraapp;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.util.DisplayMetrics;

import java.io.File;

/**
 * Created by stpl on 1/11/2017.
 */

public class Utils {
    static public File mediaStorageDir = new File(
            Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "MyCameraApp");

    public static float convertDpToPixel(Context context, float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
