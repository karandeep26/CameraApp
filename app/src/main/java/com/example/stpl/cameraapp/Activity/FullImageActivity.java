package com.example.stpl.cameraapp.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.stpl.cameraapp.Adapters.CustomPagerAdapter;
import com.example.stpl.cameraapp.Models.MediaDetails;
import com.example.stpl.cameraapp.Network.KinveyClient;
import com.example.stpl.cameraapp.R;
import com.example.stpl.cameraapp.loginInterface;
import com.kinvey.android.Client;
import com.kinvey.java.core.DownloaderProgressListener;
import com.kinvey.java.core.MediaHttpDownloader;
import com.kinvey.java.core.MediaHttpUploader;
import com.kinvey.java.core.UploaderProgressListener;
import com.kinvey.java.model.FileMetaData;
import com.kinvey.java.model.KinveyMetaData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static com.example.stpl.cameraapp.Utils.mediaStorageDir;

public class FullImageActivity extends AppCompatActivity implements View.OnClickListener,loginInterface {
    int position;
    ViewPager mViewPager;
    ArrayList<MediaDetails> mediaDetails;
    KinveyClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_full_image);
        bindViews();
        Intent intent = getIntent();
        mClient=new KinveyClient(this,this);
        position = intent.getIntExtra("position",-1);
        mediaDetails=intent.getParcelableArrayListExtra("model");
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(new CustomPagerAdapter(this,mediaDetails));
        mViewPager.setCurrentItem(position);
        mClient.login();
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
//        switch (v.getId()) {
//            case R.id.imageButton://UPLOAD
//                try {
//                    File file = new File(mediaStorageDir + "/" + fileName);
//                    fileMetaData.setFileName(fileName);
//                    fileMetaData.setPublic(true);
//                    KinveyMetaData.AccessControlList accessControlList = new KinveyMetaData.AccessControlList();
//                    accessControlList.setGloballyReadable(true);
//                    accessControlList.setGloballyWriteable(true);
//                    fileMetaData.setAcl(accessControlList);
//                    fileMetaData.setId(fileName);
//                    kinveyClient.file().upload(fileMetaData, file, new UploaderProgressListener() {
//                        @Override
//                        public void progressChanged(MediaHttpUploader mediaHttpUploader) throws IOException {
//                            Log.d("progress ", "changed");
//                        }
//
//                        @Override
//                        public void onSuccess(FileMetaData fileMetaData) {
//                            Log.d("Success", "true");
//                        }
//
//                        @Override
//                        public void onFailure(Throwable throwable) {
//                            Log.d("Failure", "true");
//                        }
//                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;
//            case R.id.imageButton2://DOWNLOAD
//                try {
//                    mediaStorageDir = new File(
//                            Environment
//                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//                            "MyCameraApp");
//                    final File file = new File(mediaStorageDir.getPath() + File.separator + "123456" + ".jpg");
//                    Log.d("file created", file.createNewFile() + "");
//                    final FileOutputStream fileOutputStream = new FileOutputStream(file);
//                    fileMetaData = new FileMetaData();
//                    fileMetaData.setFileName(fileName);
//                    fileMetaData.setId(fileName);
//                    kinveyClient.file().download(fileMetaData, fileOutputStream, new DownloaderProgressListener() {
//                        @Override
//                        public void progressChanged(MediaHttpDownloader mediaHttpDownloader) throws IOException {
//                            Log.d("download progress", "true");
//                        }
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Log.d("success download", file.length() + "");
//                            upload.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
//                        }
//
//                        @Override
//                        public void onFailure(Throwable throwable) {
//
//                        }
//                    });
//
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//
//        }
    }

    private void bindViews() {
        mViewPager= (ViewPager) findViewById(R.id.pager);

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


    @Override
    public void onSuccess() {

    }

    @Override
    public void onFailure() {

    }
}
