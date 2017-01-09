package com.example.stpl.cameraapp.Models;

import android.graphics.Bitmap;

/**
 * Created by stpl on 12/16/2016.
 */
public class VideoDetails {
    private Bitmap image;
    private String filePath;

    public VideoDetails(Bitmap image, String filePath) {
        this.image = image;
        this.filePath = filePath;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getFilePath() {
        return filePath;
    }
}


