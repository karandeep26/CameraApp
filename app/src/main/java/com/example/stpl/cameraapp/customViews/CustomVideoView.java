package com.example.stpl.cameraapp.customViews;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.VideoView;


public class CustomVideoView extends VideoView {
    private int mVideoWidth;
    private int mVideoHeight;
    int orientation;

    public CustomVideoView(Context context) {
        super(context);
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setVideoURI(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(this.getContext(), uri);
        orientation = Integer.
                parseInt(retriever.extractMetadata(MediaMetadataRetriever
                        .METADATA_KEY_VIDEO_ROTATION));
        Log.d("orientation", "" + orientation);
        mVideoWidth = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever
                .METADATA_KEY_VIDEO_WIDTH));
        mVideoHeight = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever
                .METADATA_KEY_VIDEO_HEIGHT));
        super.setVideoURI(uri);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
        Log.d("**height", mVideoHeight + "width" + mVideoWidth);

        if (mVideoWidth > 0 && mVideoHeight > 0) {
            float temp = (float) mVideoHeight / mVideoWidth;
            if (orientation == 0 || orientation == 180)
                height = Math.round(temp * width);
            Log.d("**", "" + height);
        }


        Log.i("@@@", "setting size: " + width + 'x' + height);

//        setMeasuredDimension(width, getDefaultSize(mVideoHeight, heightMeasureSpec));
        setMeasuredDimension(width, height);

    }
}

