package com.example.stpl.cameraapp.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.example.stpl.cameraapp.models.MediaDetails;

import java.io.File;
import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;

import static com.example.stpl.cameraapp.Utils.mediaStorageDir;

/**
 * Created by stpl on 1/23/2017.
 */

public class SdCardInteractorImpl implements SdCardInteractor {
    private ArrayList<MediaDetails> imageDetails, videoDetails;

    SdCardInteractorImpl(ArrayList<MediaDetails> imageDetails, ArrayList<MediaDetails>
            videoDetails) {
        this.imageDetails = imageDetails;
        this.videoDetails = videoDetails;
    }

    @Override
    public Observable<MediaDetails> getFromSdCard() {

        return Observable.create(new Observable.OnSubscribe<MediaDetails>() {
            @Override
            public void call(Subscriber<? super MediaDetails> subscriber) {
                File file = new File(mediaStorageDir.getPath());
                Matrix matrix = new Matrix();
                matrix.postRotate(270);
                if (file.isDirectory()) {
                    File[] listFile = file.listFiles();
                    for (File aListFile : listFile) {
                        String path = aListFile.getAbsolutePath();
                        String newFileName = path.substring(path.lastIndexOf("/") + 1);
                        if (aListFile.getAbsolutePath().contains("jpg")) {
                            Bitmap image = BitmapFactory.decodeFile(aListFile.getAbsolutePath());
                            imageDetails.add(new MediaDetails(ThumbnailUtils.extractThumbnail
                                    (image, 500, 500), newFileName, "image"));
                            subscriber.onNext(new MediaDetails(ThumbnailUtils.extractThumbnail
                                    (image, 500, 500), newFileName, "image"));
                        } else if (aListFile.getAbsolutePath().contains("mp4"))
                            videoDetails.add(new MediaDetails(ThumbnailUtils.createVideoThumbnail
                                    (aListFile.getAbsolutePath(), MediaStore.Video.Thumbnails
                                            .MINI_KIND), newFileName, "video"));
                    }
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                }
            }
        });
    }
}

