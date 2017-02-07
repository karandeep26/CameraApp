package com.example.stpl.cameraapp.main;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

import com.example.stpl.cameraapp.CustomCamera;
import com.example.stpl.cameraapp.GestureDetector;
import com.example.stpl.cameraapp.R;
import com.example.stpl.cameraapp.ScrollListener;
import com.example.stpl.cameraapp.activity.PlayVideoActivity;
import com.example.stpl.cameraapp.adapters.GridViewAdapter;
import com.example.stpl.cameraapp.customViews.ExpandableHeightGridView;
import com.example.stpl.cameraapp.fullImageView.FullImageActivity;
import com.example.stpl.cameraapp.models.MediaDetails;
import com.example.stpl.cameraapp.models.SdCardInteractorImpl;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rx.Subscription;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener, MainView.FileListener, AdapterView.OnItemLongClickListener,
        MainView,MainView.Adapter {
    public static boolean isSignedIn;
    final int MULTIPLE_PERMISSIONS = 123;
    boolean recording = false;
    TextView time;
    GestureDetectorCompat imageGestureDetector;
    int height;
    Subscription subscription;
    MainPresenter mainPresenter;
    private FrameLayout frameLayout;
    private CustomCamera customCamera;
    private ImageButton captureButton;
    private GridViewAdapter gridViewAdapter;
    private ExpandableHeightGridView imageGridView;
    private ImageButton videoCapture;
    private BottomSheetBehavior bottomSheetBehavior;
    private ImageButton pictures, video, delete, upload;
    private LinearLayout gridViewButton, menu;
    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;
    private ArrayList<MediaDetails> selectedItems = new ArrayList<>();
    private HashMap<Integer, View> tickView = new HashMap<>();
    SdCardInteractorImpl mSdCardInteractorImpl;
    MainPresenter.OnItemClick onItemClick;
    MainPresenterImpl mainPresenterImpl;
    MainPresenter.Adapter presenterAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSdCardInteractorImpl = new SdCardInteractorImpl();
        mainPresenterImpl = new MainPresenterImpl((MainView)this,mSdCardInteractorImpl);
        mainPresenter = mainPresenterImpl;
        presenterAdapter=mainPresenterImpl;
        onItemClick=mainPresenterImpl;


        /**
         * Set Window Flags to make app full screen
         */
        makeFullScreen();
        /**
         * Standard findViewById
         */
        findViewById();
        /**
         * Request run time permissions
         */
        mainPresenter.checkForPermissions();
        /**
         * Initialize GridViewAdapter
         * Set GridView
         * Set BottomSheetCallback
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


//        /**
//         * Initialize Firebase instance
//         */
//        FirebaseService.getInstance().initializeFirebase();
//        FirebaseService.getInstance().getmAuth().addAuthStateListener(firebaseAuth -> {
//            FirebaseUser user = firebaseAuth.getCurrentUser();
//            isSignedIn = user != null;
//        });
        if (customCamera != null) {
            customCamera.getSubject().subscribe(s -> {
                Log.d("file name", s);
            });
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**
             * Take photo
             */
            case R.id.capture:
                customCamera.takePicture();
                break;
            /**
             * Record Video
             */
            case R.id.record_video:
                recordVideo();
                break;
            /**
             * Load Video thumbnails in GridView
             */
            case R.id.videos:
                video.setSelected(true);
                presenterAdapter.updateAdapter("video");
                if (pictures.isSelected()) {
                    pictures.setSelected(false);
                }
                break;
            /**
             * Loading pictures thumbnails in a GridView
             */
            case R.id.pictures:
                presenterAdapter.updateAdapter("image");
                pictures.setSelected(true);
                if (video.isSelected()) {
                    video.setSelected(false);
                }
                break;
            /**
             * Upload pictures to the Firebase Cloud
             */
            case R.id.upload:

                break;
            /**
             * Delete pictures/videos from the phone
             */
            case R.id.delete:
                mainPresenter.deleteFromSdCard(selectedItems);
                /**
                 * Restore GridView from the selection mode
                 */
                imageGridView.requestLayout();
                imageGridView.clearChoices();
                selectedItems.clear();
                gridViewButton.setVisibility(View.VISIBLE);
                menu.setVisibility(View.GONE);
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS:
                Map<String, Integer> perms = new HashMap<>();

                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager
                        .PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager
                        .PERMISSION_GRANTED);

                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);

                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED) {
                    /**
                     * Permissions are available,set up the screen now
                     */
                    permissionAvailable();
                }
                // All Permissions Granted
                else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Some Permission is Denied", Toast
                            .LENGTH_SHORT)
                            .show();
                }

                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    @Override
    protected void onPause() {
        super.onPause();
//        customCamera.releaseCamera();
    }


    @Override
    protected void onResume() {
        super.onResume();
        makeFullScreen();

//       if(customCamera.getCamera()==null)
//            customCamera.setCamera();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MediaDetails details = (MediaDetails) parent.getItemAtPosition(position);
        details.getFilePath();
        ImageView tick = (ImageView) view.findViewById(R.id.tick);
        /**
         * if items are in selection mode,show/hide display the tick icon
         */
        if(onItemClick.isSelectionMode()){
            mainPresenter.modifySelection(details);
            if(details.isChecked()){
                tick.setVisibility(View.VISIBLE);
                tickView.put(position, view);
            }
            else{
                tick.setVisibility(View.INVISIBLE);
                tickView.remove(position);
            }
            if(!onItemClick.isSelectionMode()){
                imageGridView.requestLayout();
                imageGridView.clearChoices();
                gridViewButton.setVisibility(View.VISIBLE);
                menu.setVisibility(View.GONE);
            }
        }





        /**
         * If not in selection mode,fire an Intent to display Fullscreen video/picture
         */
        else {
            Intent intent;
            if (details.getMediaType().equals("image")) {
                intent = new Intent(MainActivity.this, FullImageActivity.class);
                Log.d("file path", details.getFilePath());

            } else {
                intent = new Intent(MainActivity.this, PlayVideoActivity.class);
            }
            intent.putExtra("position", position);
            startActivity(intent);
        }
    }

    /**
     * Standard findViewByIds
     */
    private void findViewById() {
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
        imageGridView.setSaveEnabled(true);

    }

    /**
     * Initialise GridView and other items
     */
    private void initVariables() {
        Picasso.with(this).setIndicatorsEnabled(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        imageGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        imageGridView.setOnItemLongClickListener(this);
        imageGridView.setOnScrollListener(new ScrollListener(this));

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
        /**
         * If GridView is in selection mode,clear the selections
         */
        Log.d("boolean",onItemClick.isSelectionMode()+"");
        if (onItemClick.isSelectionMode()) {
            mainPresenter.removeSelectedItems();


            for (Integer key : tickView.keySet()) {
                tickView.get(key).findViewById(R.id.tick).setVisibility(View.GONE);
            }
            /**
             * Restore gridView from the Selection Mode
             */
            imageGridView.requestLayout();
            imageGridView.clearChoices();
            selectedItems.clear();
            gridViewButton.setVisibility(View.VISIBLE);
            menu.setVisibility(View.GONE);

        }
        /**
         * If bottom sheet is open,hide it.
         */

        else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        /**
         * Exit the Activity
         */
        else {
            finish();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (customCamera != null && customCamera.getCamera() != null) {
            customCamera.releaseCamera();
        }
        if (mainPresenter != null) {
            mainPresenter.onDestroy();
            mainPresenter = null;
        }
    }

    /**
     * Start/Stop recording the video
     */
    private void recordVideo() {
        if (!recording) {
            recording = true;
            time.setVisibility(View.VISIBLE);
            customCamera.recordVideo();
            subscription = mainPresenter.startTimer();
        } else {
            time.setVisibility(View.GONE);
            customCamera.stopVideo();
            recording = false;
            if (subscription != null && !subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        MediaDetails image = (MediaDetails) parent.getItemAtPosition(position);
        mainPresenter.modifySelection(image);
        ImageView tick = (ImageView) view.findViewById(R.id.tick);
        menu.setVisibility(View.VISIBLE);
        gridViewButton.setVisibility(View.INVISIBLE);
        tick.setVisibility(View.VISIBLE);
        tickView.put(position, view);
        return true;
    }

    /**
     * Loads the camera and thumbnails from the sdCard.
     * Hack for nested scrollView and gridView using GestureDetector
     */
    @Override
    public void permissionAvailable() {
        gridViewAdapter = new GridViewAdapter(this);
        pictures.setSelected(true);
        customCamera = new CustomCamera(this, mainPresenter);

        /**
         * To overlay capture button
         */
        customCamera.setWillNotDraw(false);
        frameLayout.addView(customCamera);
        gridViewButton.getViewTreeObserver().addOnGlobalLayoutListener(() -> imageGridView.
                getLayoutParams().height = height - gridViewButton.getHeight());
        imageGestureDetector = new GestureDetectorCompat(this,
                new GestureDetector(imageGridView, bottomSheetBehavior));
        /**
         * If first item of GridView is Visible,Disable GridView Scrolling top to bottom.
         * If first item is not visible,continue with gridView scroll scrolling
         */
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
        mainPresenter.fetchFromSdCard("all");

    }

    @Override
    public void permissionNotAvailable(ArrayList<String> permissionNeeded,
                                       ArrayList<String> permissionList) {
        String message = "You need to grant access to " + permissionNeeded.get(0);
        for (String permission : permissionNeeded) {
            message = message + ", " + permission;
        }
        showMessageOKCancel(message,
                (dialog, which) -> ActivityCompat.requestPermissions(this, permissionList.toArray
                                (new String[permissionList.size()]),
                        MULTIPLE_PERMISSIONS));
    }

    @Override
    public void itemAdd(MediaDetails mediaDetails) {
        gridViewAdapter.addImage(mediaDetails, 1);

    }

    @Override
    public void setTimerValue(String timer) {
        time.setText(timer);
    }


    @Override
    public void updateAdapter(ArrayList<MediaDetails> mediaDetails) {
        gridViewAdapter.setMediaDetails(mediaDetails);
        imageGridView.setSmoothScrollbarEnabled(false);
        imageGridView.post(() -> imageGridView.setSelection(0));
    }

    @Override
    public void onFileDeleted(MediaDetails mediaDetails) {
        gridViewAdapter.notifyDataSetChanged();
        Log.d("file deleted", "true");

    }

    @Override
    public void onErrorOccurred() {
        Log.d("file deleted", "false");

    }

    @Override
    public void onFileAdded(MediaDetails mediaDetails) {
        findViewById(R.id.design_bottom_sheet).requestLayout();
        gridViewAdapter.addImage(mediaDetails, 0);

    }


}


