package com.example.stpl.cameraapp.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.LoginFilter;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.stpl.cameraapp.R;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyPingCallback;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.Query;
import com.kinvey.java.User;
import com.kinvey.java.core.DownloaderProgressListener;
import com.kinvey.java.core.KinveyCancellableCallback;
import com.kinvey.java.core.MediaHttpDownloader;
import com.kinvey.java.core.MediaHttpUploader;
import com.kinvey.java.core.UploaderProgressListener;
import com.kinvey.java.model.FileMetaData;
import com.kinvey.java.model.KinveyMetaData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FullImageActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView imageview;
    FileMetaData fileMetaData = new FileMetaData();
    String fileName;
    Client kinveyClient;
    Bitmap bitmap;
    ImageButton upload, download;
    static String APPKEY = "kid_BJH4mr6me";
    static String APPSECRET = "bf1e29b625dc4537819d284c6a73869b";
    Matrix matrix;

    File mediaStorageDir = new File(
            Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "MyCameraApp");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_full_image);
        bindViews();
        setClickListener();
        Intent intent = getIntent();
        fileName = intent.getStringExtra("fileName");
        matrix = new Matrix();
        matrix.postRotate(270);
        if (fileName != null) {
            new BitmapDecode().execute();
        }
        kinveyClient = new Client.Builder(APPKEY, APPSECRET, this).build();
        kinveyClient.user().login(new KinveyUserCallback() {
            @Override
            public void onSuccess(User user) {
                Log.i("success", "Logged in a new implicit user with id: " + user.getId());
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d("failed", "sign in");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("On", "pause");
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
        Log.d("on", "resume");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButton://UPLOAD
                try {
                    File file = new File(mediaStorageDir + "/" + fileName);
                    fileMetaData.setFileName(fileName);
                    fileMetaData.setPublic(true);
                    KinveyMetaData.AccessControlList accessControlList = new KinveyMetaData.AccessControlList();
                    accessControlList.setGloballyReadable(true);
                    accessControlList.setGloballyWriteable(true);
                    fileMetaData.setAcl(accessControlList);
                    fileMetaData.setId(fileName);
                    kinveyClient.file().upload(fileMetaData, file, new UploaderProgressListener() {
                        @Override
                        public void progressChanged(MediaHttpUploader mediaHttpUploader) throws IOException {
                            Log.d("progress ", "changed");
                        }

                        @Override
                        public void onSuccess(FileMetaData fileMetaData) {
                            Log.d("Success", "true");
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Log.d("Failure", "true");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.imageButton2://DOWNLOAD
                try {
                    mediaStorageDir = new File(
                            Environment
                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                            "MyCameraApp");
                    final File file = new File(mediaStorageDir.getPath() + File.separator + "123456" + ".jpg");
                    Log.d("file created", file.createNewFile() + "");
                    final FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileMetaData = new FileMetaData();
                    fileMetaData.setFileName(fileName);
                    fileMetaData.setId(fileName);
                    kinveyClient.file().download(fileMetaData, fileOutputStream, new DownloaderProgressListener() {
                        @Override
                        public void progressChanged(MediaHttpDownloader mediaHttpDownloader) throws IOException {
                            Log.d("download progress", "true");
                        }
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("success download", file.length() + "");
                            upload.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Log.d("filemetadata", fileMetaData.toString());
                            Log.d("failure download", "true");
                        }
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                }


        }
    }

    class BitmapDecode extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            bitmap = BitmapFactory.decodeFile(mediaStorageDir + "/" + fileName);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            imageview.setImageBitmap(bitmap);
        }
    }

    private void bindViews() {
        imageview = (ImageView) findViewById(R.id.image);
        upload = (ImageButton) findViewById(R.id.imageButton);
        download = (ImageButton) findViewById(R.id.imageButton2);
    }

    private void setClickListener() {
        upload.setOnClickListener(this);
        download.setOnClickListener(this);
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
