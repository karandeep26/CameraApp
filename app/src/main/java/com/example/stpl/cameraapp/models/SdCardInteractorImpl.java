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

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.example.stpl.cameraapp.Utils.mediaStorageDir;


public class SdCardInteractorImpl implements SdCardInteractor, SdCardInteractor.GetMediaList,
        SdCardInteractor.Selection {
    private ArrayList<MediaDetails> selected;

    public SdCardInteractorImpl() {

        selected = new ArrayList<>();
    }

    @Override
    public Single<List<MediaDetails>> getFromSdCard(String type) {
        File file = new File(mediaStorageDir.getPath());
        File[] listFile;


        if (type.equals(Utils.VIDEO)) {
            listFile = file.listFiles(pathname -> pathname.getAbsolutePath().contains(Utils.MP4));
        } else if (type.equalsIgnoreCase(Utils.IMAGE)) {
            listFile = file.listFiles(pathname -> pathname.getAbsolutePath().contains(Utils.JPG));
        } else {
            listFile = file.listFiles(pathname -> pathname.getAbsolutePath().contains(Utils.MP4)
                    || pathname.getAbsolutePath().contains(Utils.JPG));
        }
        if (listFile == null || listFile.length == 0) {
            listFile = new File[0];
        }
        Arrays.sort(listFile, (o1, o2) -> o2.getName().compareTo(o1.getName()));
        Observable<File> fileObservable = Observable.fromArray(listFile);
        return fileObservable.flatMap(new Function<File, ObservableSource<File>>() {
            @Override
            public ObservableSource<File> apply(File file) throws Exception {

                return Observable.just(file);
            }
        }).subscribeOn(Schedulers.io()).map(this::getMediaDetails).toList();
    }

    @Override
    public boolean deleteFromSdCard(MediaDetails mediaDetails) {
        File deleteFile = new File(mediaDetails.getFilePath());
        if (selected.size() > 0) {
            removeFromSelection(mediaDetails);
        }
        return deleteFile.delete();

    }


    @Override
    public int getMediaCount(String type) {
        File file = new File(mediaStorageDir.getPath());
        File[] imageFile = file.listFiles(pathname -> pathname.getAbsolutePath().contains(type));
        return imageFile.length;
    }


    private MediaDetails getMediaDetails(File file) {
        MediaDetails mediaDetails;
        String path = file.getAbsolutePath();

        if (path.contains("jpg")) {
            mediaDetails = new MediaDetails(path, "image");

        } else {
            mediaDetails = new MediaDetails(path, "video");
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
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return getMediaDetails(pictureFile);
    }

    @Override
    public MediaDetails getSavedVideo(String fileName) {
        return getMediaDetails(new File(fileName));
    }

    @Override
    public ArrayList<MediaDetails> getMedia(String type) {
        if (type.equals("image")) {
            return null;
        } else if (type.equals("video")) {
            return null;
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

