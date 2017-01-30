package com.example.stpl.cameraapp.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.example.stpl.cameraapp.Utils;

import java.io.File;

import rx.Observable;
import rx.Subscriber;

import static com.example.stpl.cameraapp.Utils.mediaStorageDir;

/**
 * Created by stpl on 1/23/2017.
 */

public class SdCardInteractorImpl implements SdCardInteractor {
    @Override
    public Observable<MediaDetails> getFromSdCard() {

        return Observable.create(new Observable.OnSubscribe<MediaDetails>() {
            @Override
            public void call(Subscriber<? super MediaDetails> subscriber) {
                MediaDetails mediaDetails = null;
                File file = new File(mediaStorageDir.getPath());
                Matrix matrix = new Matrix();
                matrix.postRotate(270);
                if (file.isDirectory()) {
                    File[] listFile = file.listFiles();
                    for (File aListFile : listFile) {
                        String path = aListFile.getAbsolutePath();
                        String newFileName = path.substring(path.lastIndexOf("/") + 1);
                        if (newFileName.contains("jpg")) {
                            Bitmap image = Utils.decodeSampledBitmapFromFile
                                    (aListFile.getAbsolutePath(), 500, 500);
                            mediaDetails = new MediaDetails(ThumbnailUtils
                                    .extractThumbnail
                                            (image, 500, 500), newFileName, "image");
                        } else if (newFileName.contains("mp4")) {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.outWidth = 500;
                            options.outHeight = 500;
                            mediaDetails = new MediaDetails(ThumbnailUtils
                                    .createVideoThumbnail
                                            (aListFile.getAbsolutePath(), MediaStore.Video
                                                    .Thumbnails
                                                    .MINI_KIND), newFileName, "video");
                        }
                        subscriber.onNext(mediaDetails);
                    }
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                }
            }


        });
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
}

