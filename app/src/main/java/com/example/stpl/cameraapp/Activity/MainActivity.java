package com.example.stpl.cameraapp.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stpl.cameraapp.Adapters.ImageGridViewAdapter;
import com.example.stpl.cameraapp.Adapters.VideosGridViewAdapter;
import com.example.stpl.cameraapp.CustomViews.ExpandableHeightGridView;
import com.example.stpl.cameraapp.GestureDetector;
import com.example.stpl.cameraapp.Models.ImageDetails;
import com.example.stpl.cameraapp.Models.VideoDetails;
import com.example.stpl.cameraapp.OnPictureTaken;
import com.example.stpl.cameraapp.Preview;
import com.example.stpl.cameraapp.R;
import com.example.stpl.cameraapp.ReadFromDb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnPictureTaken, AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener {
    private FrameLayout frameLayout;
    private Preview preview;
    private ImageButton captureButton;
    private ImageGridViewAdapter imageGridViewAdapter;
    private VideosGridViewAdapter videosGridViewAdapter;
    private ExpandableHeightGridView imageGridView;
    private ExpandableHeightGridView videoGridView;
    ImageButton videoCapture;
    ArrayList<String> permissionNeeded;
    ArrayList<String> permissionList;
    final int MULTIPLE_PERMISSIONS = 123;
    ExifInterface exifInterface;
    boolean recording = false;
    TextView time;
    Timer timer;
    public int seconds = 0;
    public int minutes = 0;
    private BottomSheetBehavior bottomSheetBehavior;
    GestureDetectorCompat imageGestureDetector;
    GestureDetectorCompat videoGestureDetector;
    int height;
    Button pictures, video;
    private ArrayList<ImageDetails> imageDetails = new ArrayList<>();
    private ArrayList<VideoDetails> videoDetails = new ArrayList<>();
    BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;
    private HashMap<Integer,ImageDetails> selectedItems=new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        makeFullScreen();
        bindViews();
        initVariables();
        setClickListeners();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        askPermissions();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.capture:
                preview.takePicture();
                break;
            case R.id.record_video:
                recordVideo();
                break;
            case R.id.videos:
                Log.d("size of videos", videoDetails.size() + "");
                imageGridView.setVisibility(View.GONE);
                videoGridView.setVisibility(View.VISIBLE);
                break;
            case R.id.pictures:
                Log.d("size of images", imageDetails.size() + "");
                imageGridView.setVisibility(View.VISIBLE);
                videoGridView.setVisibility(View.GONE);


        }
    }

    @Override
    public void pictureTaken(String fileName) {

        File file = new File(fileName);
        String newFileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        Matrix matrix = new Matrix();
        matrix.postRotate(270);
        if (!fileName.contains("IMG")) {
            videoDetails.add(new VideoDetails(ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND), newFileName));
            videosGridViewAdapter.notifyDataSetChanged();
        } else {
            Bitmap image = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageDetails.add(new ImageDetails(ThumbnailUtils.extractThumbnail(image, 500, 500), newFileName));
            imageGridViewAdapter.notifyDataSetChanged();
            findViewById(R.id.design_bottom_sheet).requestLayout();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                {
                    Map<String, Integer> perms = new HashMap<>();
                    // Initial
                    perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                    perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                    perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                    // Fill with results
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        setUpScreen();
                    }
                    // All Permissions Granted
                    else {
                        // Permission Denied
                        Toast.makeText(MainActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void askPermissions() {
        permissionNeeded = new ArrayList<>();
        permissionList = new ArrayList<>();
        if (!addPermission(permissionList, Manifest.permission.CAMERA))
            permissionNeeded.add("camera");
        if (!addPermission(permissionList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionNeeded.add("write");
        if (!addPermission(permissionList, Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionNeeded.add("read");
        if (!addPermission(permissionList, Manifest.permission.RECORD_AUDIO))
            permissionNeeded.add("record audio");
        if (permissionList.size() > 0) {
            if (permissionNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionNeeded.get(0);
                for (int i = 1; i < permissionNeeded.size(); i++)
                    message = message + ", " + permissionNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this, permissionList.toArray(new String[permissionList.size()]),
                                        MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MULTIPLE_PERMISSIONS);
            return;
        }
        setUpScreen();

    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission))
                return false;
        }


        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void setUpScreen() {
        preview = new Preview(this, this);
        preview.setWillNotDraw(false);
        frameLayout.addView(preview);
        pictures.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageGridView.getLayoutParams().height = height - pictures.getHeight() - (int) convertDpToPixel(((float) 20));

            }
        });
        video.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                videoGridView.getLayoutParams().height = height - video.getHeight() - (int) convertDpToPixel(((float) 20));
            }
        });


        imageGestureDetector = new GestureDetectorCompat(this, new GestureDetector(imageGridView, bottomSheetBehavior));
        videoGestureDetector = new GestureDetectorCompat(this, new GestureDetector(videoGridView, bottomSheetBehavior));
        videoGridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                if (videoGridView.getFirstVisiblePosition() == 0)
                    videoGestureDetector.onTouchEvent(event);
                return false;
            }
        });
        videoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, PlayVideoActivity.class);
                intent.putExtra("fileName", videoDetails.get(position).getFilePath());
                startActivity(intent);

            }
        });

        imageGridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                if (imageGridView.getFirstVisiblePosition() == 0)
                    imageGestureDetector.onTouchEvent(event);

                return false;
            }
        });
        imageGridView.setExpanded(false);
        imageGridView.setAdapter(imageGridViewAdapter);
        imageGridView.setOnItemClickListener(this);
        videoGridView.setExpanded(false);
        videoGridView.setAdapter(videosGridViewAdapter);
        bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback);
        new ReadFromDb(this).execute();
    }

    public void getFromSdcard() throws IOException {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyCameraApp");
        File file = new File(mediaStorageDir.getPath());

        if (file.isDirectory()) {
            File[] listFile = file.listFiles();
            for (File aListFile : listFile) {
                String path = aListFile.getAbsolutePath();
                String newFileName = path.substring(path.lastIndexOf("/") + 1);

                if (aListFile.getAbsolutePath().contains("jpg")) {
                    exifInterface = new ExifInterface(aListFile.getAbsolutePath());
                    Matrix matrix = new Matrix();
                    matrix.postRotate(270);
                    Bitmap image = BitmapFactory.decodeFile(aListFile.getAbsolutePath());
                    imageDetails.add(new ImageDetails(ThumbnailUtils.extractThumbnail(image, 500, 500), newFileName));
                } else if (aListFile.getAbsolutePath().contains("mp4"))
                    videoDetails.add(new VideoDetails(ThumbnailUtils.createVideoThumbnail(aListFile.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND), newFileName));

            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
//        preview.releaseCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//       if(preview.getCamera()==null)
//            preview.setCamera();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, FullImageActivity.class);
        intent.putExtra("fileName", imageDetails.get(position).getFilePath());
        startActivity(intent);
    }

    private void bindViews() {
        imageGridView = (ExpandableHeightGridView) findViewById(R.id.image_grid_view);
        videoGridView = (ExpandableHeightGridView) findViewById(R.id.video_grid_view);
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        captureButton = (ImageButton) findViewById(R.id.capture);
        time = (TextView) findViewById(R.id.timer);
        videoCapture = (ImageButton) findViewById(R.id.record_video);
        pictures = (Button) findViewById(R.id.pictures);
        video = (Button) findViewById(R.id.videos);
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.design_bottom_sheet));

    }

    private void initVariables() {
        imageGridViewAdapter = new ImageGridViewAdapter(this, imageDetails);
        videosGridViewAdapter = new VideosGridViewAdapter(this, videoDetails);
        imageGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        videoGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        imageGridView.setOnItemLongClickListener(this);
        bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    Log.d("state changed ", "called");
                    bottomSheet.requestLayout();
                    bottomSheet.invalidate();
                    imageGridView.smoothScrollToPosition(0);
                    imageGridView.requestLayout();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        };
    }

    private void setClickListeners() {
        videoCapture.setOnClickListener(this);
        captureButton.setOnClickListener(this);
        pictures.setOnClickListener(this);
        video.setOnClickListener(this);
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
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else
            finish();
    }

    private void recordVideo() {
        if (!recording) {
            recording = true;
            seconds = minutes = 0;
            time.setVisibility(View.VISIBLE);
            preview.recordVideo();
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            time.setText(String.valueOf(minutes) + ":" + String.valueOf(seconds));
                            seconds += 1;

                            if (seconds == 0) {
                                time.setText(String.valueOf(minutes) + ":" + String.valueOf(seconds));

                                seconds = 60;
                                minutes = minutes + 1;

                            }
                        }
                    });

                }
            }, 0, 1000);


        } else {
            time.setVisibility(View.GONE);
            preview.stopVideo();
            recording = false;
            timer.cancel();
        }
    }

    public float convertDpToPixel(float dp) {
        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ImageDetails image= (ImageDetails) parent.getItemAtPosition(position);
        ImageView tick = (ImageView) view.findViewById(R.id.tick);
        image.toggleChecked();
        if(image.isChecked()) {
            tick.setVisibility(View.VISIBLE);
            selectedItems.put(position,image);
        }
        else{
            tick.setVisibility(View.GONE);
            selectedItems.remove(position);
        }

        return true;
    }
}

