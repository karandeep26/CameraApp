package com.example.stpl.cameraapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.stpl.cameraapp.R;
import com.example.stpl.cameraapp.Utils;

import java.io.File;

public class PlayVideoActivity extends AppCompatActivity {
    File mediaStorageDir = Utils.mediaStorageDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        Intent intent = getIntent();
//        CustomVideoView videoView = (CustomVideoView) findViewById(R.id.videoView);
//        String fileName = intent.getStringExtra("fileName");
//        android.widget.MediaController mediaController = new android.widget.MediaController(this);
//        mediaController.setAnchorView(videoView);
//        videoView.setMediaController(mediaController);
//        videoView.setVideoPath((fileName));
//        DisplayMetrics displaymetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//        videoView.start();
    }
}
