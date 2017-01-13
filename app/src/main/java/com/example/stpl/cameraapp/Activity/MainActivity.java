package com.example.stpl.cameraapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stpl.cameraapp.GestureDetector;
import com.example.stpl.cameraapp.OnPictureTaken;
import com.example.stpl.cameraapp.Preview;
import com.example.stpl.cameraapp.R;
import com.example.stpl.cameraapp.Utils;
import com.example.stpl.cameraapp.adapters.GridViewAdapter;
import com.example.stpl.cameraapp.customViews.ExpandableHeightGridView;
import com.example.stpl.cameraapp.models.MediaDetails;
import com.example.stpl.cameraapp.network.FirebaseService;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.example.stpl.cameraapp.Utils.mediaStorageDir;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnPictureTaken, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    public static boolean isSignedIn;
    final int MULTIPLE_PERMISSIONS = 123;
    public int seconds = 0;
    public int minutes = 0;
    boolean recording = false;
    TextView time;
    Timer timer;
    GestureDetectorCompat imageGestureDetector;
    int height;
    Subscription subscription;
    private FrameLayout frameLayout;
    private Preview preview;
    private ImageButton captureButton;
    private GridViewAdapter gridViewAdapter;
    private ExpandableHeightGridView imageGridView;
    private ImageButton videoCapture;
    private ArrayList<String> permissionList;
    private BottomSheetBehavior bottomSheetBehavior;
    private ImageButton pictures, video, delete, upload;
    private LinearLayout gridViewButton, menu;
    private ArrayList<MediaDetails> imageDetails = new ArrayList<>();
    private ArrayList<MediaDetails> videoDetails = new ArrayList<>();
    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;
    private ArrayList<MediaDetails> selectedItems = new ArrayList<>();
    private HashMap<Integer, View> tickView = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * Set Window Flags to make app full screen
         */
        makeFullScreen();
        bindViews();
        /**
         * Initialize GridViewAdapter
         * Set GridView
         * Set BottomSheetCallback
         */
        /**
         * Initialize GridViewAdapter
         * set GridView
         */
        initVariables();
        /**
         * Set button on click listeners
         */
        setClickListeners();
        /**
         * Calculate Screen Height
         */
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        /**
         * Request run time permissions
         */
        askPermissions();
        /**
         * Initialize Firebase instance
         */
        FirebaseService.getInstance().initializeFirebase();
        FirebaseService.getInstance().getmAuth().addAuthStateListener(firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            isSignedIn = user != null;
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**
             * Take photo
             */
            case R.id.capture:
                preview.takePicture();
                break;
            /**
             * Record Video
             */
            case R.id.record_video:
                recordVideo();
                break;
            /**
             * Load Videos thumbnails in GridView
             */
            case R.id.videos:
                video.setSelected(true);
                gridViewAdapter.setMediaDetails(videoDetails);
                gridViewAdapter.notifyDataSetChanged();
                if (pictures.isSelected()) {
                    pictures.setSelected(false);
                }
                break;
            case R.id.pictures:
                gridViewAdapter.setMediaDetails(imageDetails);
                pictures.setSelected(true);
                if (video.isSelected()) {
                    video.setSelected(false);
                }
                break;
            case R.id.upload:

                break;
            case R.id.delete:
                File deleteFile;
                gridViewAdapter.getCount();
                for (MediaDetails mediaDetails : selectedItems) {
//                    MediaDetails mediaDetails=gridViewAdapter.getItem(key);
                    deleteFile = new File(Utils.mediaStorageDir + "/" + mediaDetails.getFilePath());
                    Boolean fileDeleted = deleteFile.delete();
                    if (fileDeleted) {
                        if (mediaDetails.getMediaType().equals("image")) {
                            imageDetails.remove(mediaDetails);
                        } else {
                            videoDetails.remove(mediaDetails);
                        }
                        gridViewAdapter.notifyDataSetChanged();
                    }
                }
                imageGridView.requestLayout();
                imageGridView.clearChoices();
                selectedItems.clear();
                gridViewButton.setVisibility(View.VISIBLE);
                menu.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void pictureTaken(String fileName) {
        File file = new File(fileName);
        String newFileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        Matrix matrix = new Matrix();
        matrix.postRotate(270);
        if (!fileName.contains("IMG")) {
            imageDetails.add(new MediaDetails(ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND), newFileName, "video"));
            gridViewAdapter.add(new MediaDetails(ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND), newFileName, "video"));
        } else {
            Bitmap image = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageDetails.add(new MediaDetails(ThumbnailUtils.extractThumbnail(image, 500, 500), newFileName, "image"));
            gridViewAdapter.add(new MediaDetails(ThumbnailUtils.extractThumbnail(image, 500, 500), newFileName, "image"));
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
        ArrayList<String> permissionNeeded = new ArrayList<>();
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
                        (dialog, which) -> ActivityCompat.requestPermissions(MainActivity.this, permissionList.toArray(new String[permissionList.size()]),
                                MULTIPLE_PERMISSIONS));
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
        pictures.setSelected(true);
        preview = new Preview(this, this);
        preview.setWillNotDraw(false);/* to overlay capture button*/
        frameLayout.addView(preview);
        gridViewButton.getViewTreeObserver().addOnGlobalLayoutListener(() -> imageGridView.
                getLayoutParams().height = height - gridViewButton.getHeight());
        imageGestureDetector = new GestureDetectorCompat(this, new GestureDetector(imageGridView, bottomSheetBehavior));
        imageGridView.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            if (imageGridView.getFirstVisiblePosition() == 0)
                imageGestureDetector.onTouchEvent(event);
            return false;
        });

        imageGridView.setExpanded(false);
        imageGridView.setAdapter(gridViewAdapter);
        imageGridView.setOnItemClickListener(this);
        bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback);

        subscription = getFromSdCard().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                subscribe(mediaDetails -> gridViewAdapter.add(mediaDetails),
                        throwable -> Log.d("debug", throwable.getMessage()),
                        () -> Log.d("debug", "completed"));
    }

    @Override
    protected void onPause() {
        super.onPause();
//        preview.releaseCamera();
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
        MediaDetails details = (MediaDetails) parent.getItemAtPosition(position);
        ImageView tick = (ImageView) view.findViewById(R.id.tick);
        if (selectedItems.size() > 0) {
            details.toggleChecked();
            if (details.isChecked()) {
                selectedItems.add(details);
                tick.setVisibility(View.VISIBLE);
                tickView.put(position, view);
            } else {
                tick.setVisibility(View.INVISIBLE);
                selectedItems.remove(details);
                tickView.remove(position);
                if (selectedItems.size() == 0) {
                    imageGridView.requestLayout();
                    imageGridView.clearChoices();
                    gridViewButton.setVisibility(View.VISIBLE);
                    menu.setVisibility(View.GONE);
                }
            }
        } else {
            Intent intent;
            if (details.getMediaType().equals("image")) {
                intent = new Intent(MainActivity.this, FullImageActivity.class);
                intent.putParcelableArrayListExtra("model", imageDetails);
            } else {
                intent = new Intent(MainActivity.this, PlayVideoActivity.class);
                intent.putExtra("fileName", videoDetails.get(position).getFilePath());
            }
            intent.putExtra("position", position);
            startActivity(intent);
        }
    }

    private void bindViews() {
        gridViewButton = (LinearLayout) findViewById(R.id.gridViewButtons);
        menu = (LinearLayout) findViewById(R.id.menu);
        imageGridView = (ExpandableHeightGridView) findViewById(R.id.image_grid_view);
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        captureButton = (ImageButton) findViewById(R.id.capture);
        time = (TextView) findViewById(R.id.timer);
        videoCapture = (ImageButton) findViewById(R.id.record_video);
        pictures = (ImageButton) findViewById(R.id.pictures);
        video = (ImageButton) findViewById(R.id.videos);
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.design_bottom_sheet));
        delete = (ImageButton) findViewById(R.id.delete);
        upload = (ImageButton) findViewById(R.id.upload);

    }

    private void initVariables() {
        gridViewAdapter = new GridViewAdapter(this, imageDetails);
        imageGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        imageGridView.setOnItemLongClickListener(this);

        bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
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
        upload.setOnClickListener(this);
        delete.setOnClickListener(this);
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
        if (selectedItems.size() > 0) {
            for (MediaDetails mediaDetails : selectedItems) {
                mediaDetails.toggleChecked();
            }

            selectedItems.forEach(MediaDetails::toggleChecked);
            for (Integer key : tickView.keySet()) {
                tickView.get(key).findViewById(R.id.tick).setVisibility(View.GONE);
            }
            imageGridView.requestLayout();
            imageGridView.clearChoices();
            selectedItems.clear();
            gridViewButton.setVisibility(View.VISIBLE);
            menu.setVisibility(View.GONE);

        } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            finish();
            Log.d("debug", "finish");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        if (preview.getCamera() != null)
            preview.releaseCamera();
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
                    runOnUiThread(() -> {
                        time.setText(String.valueOf(minutes) + ":" + String.valueOf(seconds));
                        seconds += 1;
                        if (seconds == 0) {
                            time.setText(String.valueOf(minutes) + ":" + String.valueOf(seconds));
                            seconds = 60;
                            minutes = minutes + 1;
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


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        MediaDetails image = (MediaDetails) parent.getItemAtPosition(position);
        ImageView tick = (ImageView) view.findViewById(R.id.tick);
        image.toggleChecked();
        menu.setVisibility(View.VISIBLE);
        gridViewButton.setVisibility(View.INVISIBLE);
        tick.setVisibility(View.VISIBLE);
        selectedItems.add(image);
        tickView.put(position, view);
        return true;
    }

    private Observable<MediaDetails> getFromSdCard() {
        return Observable.create(new Observable.OnSubscribe<MediaDetails>() {
            @Override
            public void call(Subscriber<? super MediaDetails> subscriber) {
                File file = new File(mediaStorageDir.getPath());
                Matrix matrix = new Matrix();
                matrix.postRotate(270);
                if (file.isDirectory()) {
                    File[] listFile = file.listFiles();
                    for (File aListFile : listFile) {
                        String path = aListFile.getAbsolutePath();
                        String newFileName = path.substring(path.lastIndexOf("/") + 1);
                        if (aListFile.getAbsolutePath().contains("jpg")) {
                            Bitmap image = BitmapFactory.decodeFile(aListFile.getAbsolutePath());
                            imageDetails.add(new MediaDetails(ThumbnailUtils.extractThumbnail(image, 500, 500), newFileName, "image"));
                            subscriber.onNext(new MediaDetails(ThumbnailUtils.extractThumbnail(image, 500, 500), newFileName, "image"));
                        } else if (aListFile.getAbsolutePath().contains("mp4"))
                            videoDetails.add(new MediaDetails(ThumbnailUtils.createVideoThumbnail(aListFile.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND), newFileName, "video"));
                    }
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                }
            }
        });
    }
}

