package com.example.stpl.cameraapp;

import android.widget.ImageView;

/**
 * Created by stpl on 1/11/2017.
 */

 public interface ImageLoadingListener {
    abstract void loadImage(String path, ImageView imageView);
}
