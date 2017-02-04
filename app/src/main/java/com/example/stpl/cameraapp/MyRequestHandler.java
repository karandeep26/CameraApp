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

    @Override
    public boolean canHandleRequest(Request data) {
        return VIDEO_SCHEME.equals(data.uri.getScheme());
    }

    @Override
    public Result load(Request request, int networkPolicy) throws IOException {
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(request.uri.getPath()
                , MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);

        return new Result(bitmap, Picasso.LoadedFrom.DISK);
    }
}
