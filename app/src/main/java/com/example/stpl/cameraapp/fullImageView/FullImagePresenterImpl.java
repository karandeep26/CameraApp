package com.example.stpl.cameraapp.fullImageView;

import com.example.stpl.cameraapp.models.MediaDetails;
import com.example.stpl.cameraapp.models.SdCardInteractor;

import java.util.ArrayList;

/**
 * Created by karan on 5/2/17.
 */

public class FullImagePresenterImpl implements FullImageInterface  {
    private FullImageView fullImageView;
    private SdCardInteractor sdCardInteractor;

    FullImagePresenterImpl(FullImageView fullImageView, SdCardInteractor sdCardInteractor) {
        this.fullImageView = fullImageView;
        this.sdCardInteractor = sdCardInteractor;
    }

    @Override
    public void fetchImages() {
        sdCardInteractor.getFromSdCard("image").subscribe(mediaDetails -> {
            fullImageView.updateAdapter((ArrayList<MediaDetails>) mediaDetails);
        });
    }
}
