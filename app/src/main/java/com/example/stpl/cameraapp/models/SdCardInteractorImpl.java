package com.example.stpl.cameraapp.models;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
                .subscribeOn(Schedulers.io())
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

    private File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
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
            Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
            Matrix matrix = new Matrix();
            matrix.postRotate(270);
            Bitmap rotatedImage = Bitmap.createBitmap(image, 0, 0, image.getWidth(),
                    image.getHeight(), matrix, true);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            rotatedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            fos.write(outputStream.toByteArray());
            fos.close();
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
}

