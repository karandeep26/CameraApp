package com.example.stpl.cameraapp.Activity;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.example.stpl.cameraapp.CustomViews.CustomVideoView;
import com.example.stpl.cameraapp.R;

import java.io.File;

public class PlayVideoActivity extends AppCompatActivity {
    File mediaStorageDir = new File(
            Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "MyCameraApp");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        Intent intent=getIntent();
        CustomVideoView videoView= (CustomVideoView) findViewById(R.id.videoView);
        String fileName=intent.getStringExtra("fileName");
        android.widget.MediaController mediaController=new android.widget.MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoPath((mediaStorageDir+"/"+fileName));

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int h = displaymetrics.heightPixels;
        int w = displaymetrics.widthPixels;

        videoView.start();

    }
}
