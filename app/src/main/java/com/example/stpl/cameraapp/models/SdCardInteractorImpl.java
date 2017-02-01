package com.example.stpl.cameraapp.models;


import java.io.File;

import rx.Observable;
import rx.schedulers.Schedulers;

import static com.example.stpl.cameraapp.Utils.mediaStorageDir;

/**
 * Created by stpl on 1/23/2017.
 */

public class SdCardInteractorImpl implements SdCardInteractor {


    @Override
    public Observable<MediaDetails> getFromSdCard() {
        File file = new File(mediaStorageDir.getPath());
        File[] listFile = file.listFiles();
        Observable<File> fileObservable = Observable.from(listFile);
        return fileObservable.flatMap(file1 -> Observable.just(file1)
                .subscribeOn(Schedulers.computation())
                .map(this::getMediaDetails));

    }

    @Override
    public boolean deleteFromSdCard(MediaDetails mediaDetails) {
        File deleteFile = new File(mediaDetails.getFilePath());
        return deleteFile.delete();

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
        } else {
            mediaDetails = new MediaDetails(path, "video");
        }
        return mediaDetails;
    }
}

