package com.example.stpl.cameraapp.models;

public class MediaDetails {
    private String filePath;
    private String mediaType;
    private boolean isChecked = false;

    public MediaDetails(String filePath, String type) {
        this.filePath = filePath;
        mediaType = type;
    }

    public String getFilePath() {
        return filePath;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void toggleChecked() {
        isChecked = !isChecked;
    }

    public String getMediaType() {
        return mediaType;
    }


}
