package com.example.stpl.cameraapp.models;


public class MediaDetails {
    private String filePath;
    private String mediaType;
    private String firebaseUrl;
    private boolean isAvailableOffline;
    private boolean isAvailableOnline;
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

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getFirebaseUrl() {
        return firebaseUrl;
    }

    public void setFirebaseUrl(String firebaseUrl) {
        this.firebaseUrl = firebaseUrl;
    }

    public boolean getIsAvailableOffline() {
        return isAvailableOffline;
    }

    public void setIsAvailableOffline(boolean isAvailableOffline) {
        this.isAvailableOffline = isAvailableOffline;
    }

    public boolean getIsAvailableOnline() {
        return isAvailableOnline;
    }

    public void setIsAvailableOnline(boolean isAvailableOnline) {
        this.isAvailableOnline = isAvailableOnline;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
