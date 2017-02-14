package com.example.stpl.cameraapp.models;


import android.util.Log;

import com.example.stpl.cameraapp.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.schedulers.Schedulers;

import static com.example.stpl.cameraapp.Utils.mediaStorageDir;


public class SdCardInteractorImpl implements SdCardInteractor, SdCardInteractor.GetMediaList,
        SdCardInteractor.Selection {
    private ArrayList<MediaDetails> images, videos;
    private ArrayList<MediaDetails> selected;

    public SdCardInteractorImpl() {
        this.images = new ArrayList<>();
        this.videos = new ArrayList<>();
        selected = new ArrayList<>();
    }

    @Override
    public Observable<List<MediaDetails>> getFromSdCard(String type) {
        File file = new File(mediaStorageDir.getPath());
        File[] listFile;

        if (type.equals("video")) {
            listFile = file.listFiles(pathname -> pathname.getAbsolutePath().contains("mp4"));
        } else if (type.equalsIgnoreCase("image")) {
            listFile = file.listFiles(pathname -> pathname.getAbsolutePath().contains("jpg"));
        } else {
            listFile = file.listFiles(pathname -> pathname.getAbsolutePath().contains("mp4")
                    || pathname.getAbsolutePath().contains("jpg"));
        }
        if (listFile == null || listFile.length == 0) {
            listFile = new File[0];
        }

        Arrays.sort(listFile, (o1, o2) -> Long.compare(o1.lastModified(), o2.lastModified()));
        Observable<File> fileObservable = Observable.from(listFile);
        return fileObservable.flatMap(file1 -> Observable.just(file1)
                .subscribeOn(Schedulers.io())
                .map(this::getMediaDetails)).toList();

    }

    @Override
    public boolean deleteFromSdCard(MediaDetails mediaDetails) {
        File deleteFile = new File(mediaDetails.getFilePath());
        boolean isDeleted = deleteFile.delete();
        if (isDeleted) {
            if (mediaDetails.getMediaType().equals("image")) {
                images.remove(mediaDetails);
            } else {
                videos.remove(mediaDetails);
            }
        }
        return isDeleted;

    }


    @Override
    public int getMediaCount(String type) {
        File file = new File(mediaStorageDir.getPath());
        File[] imageFile = file.listFiles(pathname -> {
            return pathname.getAbsolutePath().contains(type);
        });
        return imageFile.length;
    }


    private MediaDetails getMediaDetails(File file) {
        MediaDetails mediaDetails;
        String path = file.getAbsolutePath();

        if (path.contains("jpg")) {
            mediaDetails = new MediaDetails(path, "image");
            images.add(mediaDetails);
        } else {
            mediaDetails = new MediaDetails(path, "video");
            videos.add(mediaDetails);
        }
        return mediaDetails;
    }

    private File getOutputMediaFile() {
        File mediaStorageDir = Utils.mediaStorageDir;
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                .format(new Date());
        String fileName = mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";

        File mediaFile;
        mediaFile = new File(fileName);
        return mediaFile;
    }

    @Override
    public MediaDetails savePhoto(byte[] data) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            return null;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            images.add(0, getMediaDetails(pictureFile));

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return images.get(0);
    }

    @Override
    public MediaDetails getSavedVideo(String fileName) {
        return getMediaDetails(new File(fileName));
    }

    @Override
    public ArrayList<MediaDetails> getMedia(String type) {
        if (type.equals("image")) {
            return images;
        } else if (type.equals("video")) {
            return videos;
        }
        return null;
    }

    @Override
    public void removeFromSelection(MediaDetails mediaDetails) {
        selected.remove(mediaDetails);
    }

    @Override
    public void addToSelection(MediaDetails mediaDetails) {
        selected.add(mediaDetails);
    }

    @Override
    public boolean isSelectionMode() {
        return selected.size() > 0;
    }

    @Override
    public void clearAll() {
        selected.clear();

    }

    @Override
    public ArrayList<MediaDetails> getSelectedItems() {
        return selected;
    }
}

