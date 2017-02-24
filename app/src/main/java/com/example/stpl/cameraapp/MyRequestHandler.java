package com.example.stpl.cameraapp;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import java.io.IOException;


class MyRequestHandler extends RequestHandler {
    private static final String VIDEO_SCHEME = "video";
    private static final String IMAGE_SCHEME = "image";

    @Override
    public boolean canHandleRequest(Request data) {
        String scheme = data.uri.getScheme();
        return VIDEO_SCHEME.equals(scheme) || IMAGE_SCHEME.equals(scheme);
    }

    @Override
    public Result load(Request request, int networkPolicy) throws IOException {
        String scheme = request.uri.getScheme();
        Bitmap bitmap;
        if (VIDEO_SCHEME.equals(scheme)) {
            bitmap = ThumbnailUtils.createVideoThumbnail(request.uri.getPath()
                    , MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
        } else {
            bitmap = Utils.decodeSampledBitmapFromFile(request.uri.getPath(), 500, 500);
        }

        return new Result(bitmap, Picasso.LoadedFrom.DISK);
    }
}
