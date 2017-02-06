package com.example.stpl.cameraapp.fullImageView;

import com.example.stpl.cameraapp.models.SdCardInteractor;

/**
 * Created by karan on 5/2/17.
 */

public class FullImagePresenterImpl implements FullImageInterface  {
    private FullImageView fullImageView;
    private SdCardInteractor.GetMediaList sdCardInteractor;

    public FullImagePresenterImpl(FullImageView fullImageView, SdCardInteractor.GetMediaList sdCardInteractor) {
        this.fullImageView = fullImageView;
        this.sdCardInteractor = sdCardInteractor;
    }

    @Override
    public void fetchImages() {
        fullImageView.updateAdapter(sdCardInteractor.getMedia("image"));
    }
}
