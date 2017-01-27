package com.example.stpl.cameraapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.stpl.cameraapp.R;
import com.example.stpl.cameraapp.Utils;
import com.example.stpl.cameraapp.ZoomOutPageTransformer;
import com.example.stpl.cameraapp.adapters.CustomViewPagerAdapter;
import com.example.stpl.cameraapp.main.MainActivity;
import com.example.stpl.cameraapp.models.MediaDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class FullImageActivity extends AppCompatActivity implements View.OnClickListener {
    int position;
    ViewPager mViewPager;
    ArrayList<MediaDetails> mediaDetails;
    FirebaseAuth firebaseAuth;
    LinearLayout topPanel;
    int visibility;
    ImageButton upload;
    StorageReference storageReference;
    StorageReference uploadReference;
    boolean isDragging = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_full_image);
        bindViews();
        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);
        mediaDetails = intent.getParcelableArrayListExtra("model");
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(new CustomViewPagerAdapter(this, mediaDetails));
        mViewPager.setCurrentItem(position);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());

    }

    @Override
    protected void onStart() {
        super.onStart();
        visibility = View.INVISIBLE;
        firebaseAuth = FirebaseAuth.getInstance();

        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl
                ("gs://selfie-geek.appspot.com");
        uploadReference = storageRef.child("upload.jpg");

        if (!MainActivity.isSignedIn) {
            firebaseAuth.signInAnonymously().addOnCompleteListener(task -> {

            });

        }

    }

    public void toggleTopPanelVisibility() {
        if (visibility == View.VISIBLE) {
            topPanel.setVisibility(View.INVISIBLE);
            visibility = View.INVISIBLE;
        } else {
            topPanel.setVisibility(View.VISIBLE);
            visibility = View.VISIBLE;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upload:
                try {
                    InputStream inputStream = new FileInputStream(new File(Utils.mediaStorageDir
                            + "/" + mediaDetails.
                            get(mViewPager.getCurrentItem()).getFilePath()));
                    UploadTask uploadTask = uploadReference.putStream(inputStream);
                    uploadTask.addOnSuccessListener(taskSnapshot -> Log.d("file uploaded", "true")).
                            addOnFailureListener(e -> Log.e("error", e.getMessage())).
                            addOnProgressListener(taskSnapshot -> Log.d("bytes", taskSnapshot
                                    .getBytesTransferred() / 1024 + ""));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
        }

    }

    private void bindViews() {
        mViewPager = (ViewPager) findViewById(R.id.pager);
        topPanel = (LinearLayout) findViewById(R.id.topPanel);
        upload = (ImageButton) findViewById(R.id.upload);
        upload.setOnClickListener(this);

    }


    private void makeFullScreen() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }


}
