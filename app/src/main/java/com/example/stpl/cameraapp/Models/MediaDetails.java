package com.example.stpl.cameraapp.Models;

import android.graphics.Bitmap;


public class MediaDetails {
    private Bitmap image;
    private String filePath;
    private String mediaType;
    private boolean isChecked=false;

    public MediaDetails(Bitmap image, String filePath, String type) {
        this.image = image;
        this.filePath = filePath;
        mediaType = type;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getFilePath() {
        return filePath;
    }

    public boolean isChecked() {
        return isChecked;
    }
    public void toggleChecked(){
        isChecked=!isChecked;
    }

    public String getMediaType() {
        return mediaType;
    }
}
