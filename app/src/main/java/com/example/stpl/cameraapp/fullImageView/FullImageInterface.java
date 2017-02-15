package com.example.stpl.cameraapp.fullImageView;

import com.example.stpl.cameraapp.models.MediaDetails;

/**
 * Created by karan on 5/2/17.
 */

public interface FullImageInterface {
    void fetchImages();

    void deleteFile(MediaDetails mediaDetails);
}
