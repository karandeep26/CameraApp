package com.example.stpl.cameraapp.fullImageView;

import com.example.stpl.cameraapp.FileListener;
import com.example.stpl.cameraapp.models.MediaDetails;

import java.util.ArrayList;

/**
 * Created by karan on 5/2/17.
 */

interface FullImageView extends FileListener {
    void updateAdapter(ArrayList<MediaDetails> mediaDetails);
}
