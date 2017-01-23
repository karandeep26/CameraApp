package com.example.stpl.cameraapp.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;


public class MediaDetails implements Parcelable {
    public static final Creator<MediaDetails> CREATOR = new Creator<MediaDetails>() {
        @Override
        public MediaDetails createFromParcel(Parcel in) {
            return new MediaDetails(in);
        }

        @Override
        public MediaDetails[] newArray(int size) {
            return new MediaDetails[size];
        }
    };
    private Bitmap image;
    private String filePath;
    private String mediaType;
    private boolean isChecked = false;

    public MediaDetails(Bitmap image, String filePath, String type) {
        this.image = image;
        this.filePath = filePath;
        mediaType = type;
    }

    private MediaDetails(Parcel in) {
        filePath = in.readString();

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

    public void toggleChecked() {
        isChecked = !isChecked;
    }

    public String getMediaType() {
        return mediaType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(filePath);
    }
}
