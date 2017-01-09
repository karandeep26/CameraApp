package com.example.stpl.cameraapp.Models;

import android.graphics.Bitmap;

/**
 * Created by stpl on 12/15/2016.
 */

public class ImageDetails {
    private Bitmap image;
    private String filePath;
    private boolean isChecked=false;

    public ImageDetails(Bitmap image, String filePath) {
        this.image = image;
        this.filePath = filePath;
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
}
