package com.example.stpl.cameraapp.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.example.stpl.cameraapp.Utils;

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

    public SdCardInteractorImpl(ArrayList<MediaDetails> imageDetails, ArrayList<MediaDetails>
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
                        BitmapFactory.Options options = new BitmapFactory.Options();

                        if (aListFile.getAbsolutePath().contains("jpg")) {
//                            Bitmap image = BitmapFactory.decodeFile(aListFile.getAbsolutePath()
// ,options);
                            Bitmap image = Utils.decodeSampledBitmapFromResource
                                    (aListFile.getAbsolutePath(), 500, 500);
                            MediaDetails mediaDetails = new MediaDetails(ThumbnailUtils
                                    .extractThumbnail
                                    (image, 500, 500), newFileName, "image");
                            imageDetails.add(mediaDetails);
                            subscriber.onNext(mediaDetails);
                        } else if (aListFile.getAbsolutePath().contains("mp4")) {
                            MediaDetails mediaDetails = new MediaDetails(ThumbnailUtils
                                    .createVideoThumbnail
                                    (aListFile.getAbsolutePath(), MediaStore.Video.Thumbnails
                                            .FULL_SCREEN_KIND), newFileName, "video");
                            videoDetails.add(mediaDetails);
                        }
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
}

