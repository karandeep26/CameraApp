package com.example.stpl.cameraapp.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.example.stpl.cameraapp.Utils;

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
        File deleteFile = new File(Utils.mediaStorageDir + "/" + mediaDetails.getFilePath());
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
        Bitmap bitmap;
        String path = file.getAbsolutePath();
        String newFileName = path.substring(path.lastIndexOf("/") + 1);
        if (newFileName.contains("jpg")) {
            Bitmap image = Utils.decodeSampledBitmapFromFile
                    (file.getAbsolutePath(), 500, 500);
            bitmap = (ThumbnailUtils.extractThumbnail(image, 500, 500));
            mediaDetails = new MediaDetails(bitmap, newFileName, "image");
        } else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.outWidth = 500;
            options.outHeight = 500;
            bitmap = ThumbnailUtils
                    .createVideoThumbnail
                            (file.getAbsolutePath(), MediaStore.Video
                                    .Thumbnails
                                    .MINI_KIND);
            mediaDetails = new MediaDetails(bitmap, newFileName, "video");
        }
        return mediaDetails;
    }
}

