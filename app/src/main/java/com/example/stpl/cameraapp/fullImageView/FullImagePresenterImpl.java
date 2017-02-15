package com.example.stpl.cameraapp.fullImageView;

import com.example.stpl.cameraapp.FileListener;
import com.example.stpl.cameraapp.Utils;
import com.example.stpl.cameraapp.models.MediaDetails;
import com.example.stpl.cameraapp.models.SdCardInteractor;

import java.util.ArrayList;
import java.util.Collections;


class FullImagePresenterImpl implements FullImageInterface {
    private FullImageView fullImageView;
    private SdCardInteractor sdCardInteractor;
    private FileListener fileListener;
    FullImagePresenterImpl(FullImageView fullImageView, SdCardInteractor sdCardInteractor) {
        this.fullImageView = fullImageView;
        this.sdCardInteractor = sdCardInteractor;
        this.fileListener = fullImageView;
    }

    @Override
    public void fetchImages() {
        sdCardInteractor.getFromSdCard(Utils.IMAGE).subscribe(mediaDetails -> {
            Collections.reverse(mediaDetails);
            fullImageView.updateAdapter((ArrayList<MediaDetails>) mediaDetails);
        });
    }

    @Override
    public void deleteFile(MediaDetails mediaDetails) {
        boolean isFileDeleted = sdCardInteractor.deleteFromSdCard(mediaDetails);
        if (isFileDeleted) {
            fileListener.onFileDeleted(mediaDetails);
        } else {
            fileListener.onErrorOccurred();
        }

    }
}
