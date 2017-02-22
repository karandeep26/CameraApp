package com.example.stpl.cameraapp.main;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stpl.cameraapp.CustomCamera;
import com.example.stpl.cameraapp.FileListener;
import com.example.stpl.cameraapp.GestureDetector;
import com.example.stpl.cameraapp.ItemOffsetDecoration;
import com.example.stpl.cameraapp.R;
import com.example.stpl.cameraapp.RecyclerItemClickListener;
import com.example.stpl.cameraapp.ScrollListener;
import com.example.stpl.cameraapp.Utils;
import com.example.stpl.cameraapp.activity.PlayVideoActivity;
import com.example.stpl.cameraapp.adapters.RecyclerViewAdapter;
import com.example.stpl.cameraapp.fullImageView.FullImageActivity;
import com.example.stpl.cameraapp.models.MediaDetails;
import com.example.stpl.cameraapp.models.SdCardInteractorImpl;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        FileListener, RecyclerItemClickListener.OnItemClickListener,
        MainView, MainView.Adapter, MainView.UpdateView {
    public static boolean isSignedIn;
    final int MULTIPLE_PERMISSIONS = 123;
    boolean recording = false;
    TextView time;
    GestureDetectorCompat imageGestureDetector;
    int height;
    Subscriber subscription;
    MainPresenter mainPresenter;
    private FrameLayout frameLayout;
    private CustomCamera customCamera;
    private ImageButton captureButton;
    private RecyclerView recyclerGridView;
    private ImageButton videoCapture;
    public BottomSheetBehavior bottomSheetBehavior;
    private ImageButton pictures, video, delete, upload;
    private LinearLayout gridViewButton, menu;
    private SparseArray<View> tickView = new SparseArray<>();
    SdCardInteractorImpl mSdCardInteractorImpl;
    MainPresenter.OnItemClick onItemClick;
    MainPresenterImpl mainPresenterImpl;
    MainPresenter.Adapter presenterAdapter;
    View bottomSheet;
    boolean safeToTakePicture = true;
    List<AuthUI.IdpConfig> providers;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    RecyclerViewAdapter recyclerViewAdapter;
    GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mSdCardInteractorImpl = new SdCardInteractorImpl();
        mainPresenterImpl = new MainPresenterImpl(this, mSdCardInteractorImpl);
        mainPresenter = mainPresenterImpl;
        presenterAdapter = mainPresenterImpl;
        onItemClick = mainPresenterImpl;
        providers = new ArrayList<>();
        providers.add(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("data added", dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference.child("13").child("url").child("2").setValue("google.com");

        if (firebaseAuth.getCurrentUser() != null) {
            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("url");
        }


        /**
         * Set Window Flags to make app full screen
         */
        makeFullScreen();
        /**
         * Standard findViewById
         */
        findViewById();
        mainPresenter.checkForPermissions();
        /**
         * Initialize RecyclerViewAdapter
         * Set GridView
         * Set BottomSheetCallback
         */
        init();
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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**
             * Take photo
             */
            case R.id.capture:
                if (safeToTakePicture) {
                    customCamera.takePicture();
                    safeToTakePicture = false;
                }
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
                presenterAdapter.updateAdapter(Utils.VIDEO);
                if (pictures.isSelected()) {
                    pictures.setSelected(false);
                }
                break;
            /**
             * Loading pictures thumbnails in a GridView
             */
            case R.id.pictures:
                presenterAdapter.updateAdapter(Utils.IMAGE);
                pictures.setSelected(true);
                if (video.isSelected()) {
                    video.setSelected(false);
                }
                break;
            /**
             * Upload pictures to the Firebase Cloud
             */
            case R.id.upload:
                startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false).setProviders(providers).build(), 321);
                break;
            /**
             * Delete pictures/videos from the phone
             */
            case R.id.delete:
                mainPresenter.deleteFromSdCard();
                /**
                 * Restore GridView from the selection mode
                 */
                recyclerGridView.requestLayout();
//                recyclerGridView.clearChoices();
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
        if (customCamera != null) {
            customCamera.releaseCamera();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        makeFullScreen();

        if (customCamera != null && customCamera.getCamera() == null) {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                Display getOrient = getWindowManager().getDefaultDisplay();

                if (getOrient.getRotation() == 0) {
                    customCamera.setCamera();
                }
            } else {
                customCamera.setCamera();
            }
        }
    }



    /**
     * Standard findViewByIds
     */
    private void findViewById() {
        gridViewButton = $(R.id.gridViewButtons);
        menu = $(R.id.menu);
        recyclerGridView = $(R.id.recycler_grid);
        frameLayout = $(R.id.frame_layout);
        captureButton = $(R.id.capture);
        time = $(R.id.timer);
        videoCapture = $(R.id.record_video);
        pictures = $(R.id.pictures);
        video = $(R.id.videos);
        bottomSheet = $(R.id.design_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        delete = $(R.id.delete);
        upload = $(R.id.delete);

    }

    /**
     * Initialise GridView and other items
     */
    private void init() {
        Picasso.with(this).setIndicatorsEnabled(true);
        recyclerViewAdapter = new RecyclerViewAdapter();
        gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerGridView.setLayoutManager(gridLayoutManager);
        recyclerGridView.setAdapter(recyclerViewAdapter);
        recyclerGridView.addOnScrollListener(new ScrollListener(this));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
        recyclerGridView.addItemDecoration(itemDecoration);
        pictures.setSelected(true);
        /**
         * If first item of GridView is Visible,Disable GridView Scrolling top to bottom.
         * If first item is not visible,continue with gridView scroll scrolling
         */
        recyclerGridView.setOnTouchListener((v, event) -> {

            v.getParent().requestDisallowInterceptTouchEvent(true);
            recyclerGridView.setNestedScrollingEnabled(false);
            int position = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
            if (position == 0) {
                imageGestureDetector.onTouchEvent(event);
            }
            return false;
        });
        recyclerGridView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                this, recyclerGridView));


        BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior
                .BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheet.requestLayout();
                    bottomSheet.invalidate();
                    recyclerGridView.smoothScrollToPosition(0);
                    recyclerGridView.requestLayout();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        };
        bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback);
        gridViewButton.getViewTreeObserver().addOnGlobalLayoutListener(() ->
                recyclerGridView.getLayoutParams().height = height - gridViewButton.getHeight());
        imageGestureDetector = new GestureDetectorCompat(this,
                new GestureDetector(gridLayoutManager, bottomSheetBehavior));

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
        Log.d("boolean", onItemClick.isSelectionMode() + "");
        if (onItemClick.isSelectionMode()) {
            mainPresenter.removeSelectedItems();
            for (int i = 0; i < tickView.size(); i++) {
                tickView.get(tickView.keyAt(i)).findViewById(R.id.tick).setVisibility(View.GONE);
            }
            /**
             * Restore gridView from the Selection Mode
             */
            recyclerGridView.requestLayout();
//            recyclerGridView.clearChoices();
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
                subscription.onCompleted();
                subscription.unsubscribe();
            }
        }
    }



    /**
     * Loads the camera and thumbnails from the sdCard.
     * Hack for nested scrollView and gridView using GestureDetector
     */
    @Override
    public void permissionAvailable() {
        customCamera = new CustomCamera(this, mainPresenter);
        /**
         * To overlay capture button
         */
        customCamera.setWillNotDraw(false);
        frameLayout.addView(customCamera);
        mainPresenter.fetchFromSdCard(Utils.ALL);

        customCamera._getRotation().subscribe(rotation -> {

            if (rotation == Utils.ROTATION_O) {
                captureButton.animate().rotation(0);
                videoCapture.animate().rotation(0);
            } else if (rotation == Utils.ROTATION_90) {
                captureButton.animate().rotation(90).start();
                videoCapture.animate().rotation(90).start();
            } else if (rotation == Utils.ROTATION_270) {
                captureButton.animate().rotation(-90).start();
                videoCapture.animate().rotation(-90).start();
            }
        });
        customCamera._getTakePictureSubject().subscribe(safeToTakePicture -> {
            this.safeToTakePicture = safeToTakePicture;
        });

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
        recyclerViewAdapter.addItem(mediaDetails);

    }

    @Override
    public void setTimerValue(String timer) {
        time.setText(timer);
    }


    @Override
    public void updateAdapter(List<MediaDetails> mediaDetails) {
        recyclerViewAdapter.setMediaDetailsList(mediaDetails);
//        gridViewAdapter.setMediaDetails(mediaDetails);
//        recyclerGridView.setSmoothScrollbarEnabled(false);
//        recyclerGridView.post(() -> recyclerGridView.setSelection(0));
    }

    @Override
    public void onFileDeleted(MediaDetails mediaDetails) {
        recyclerViewAdapter.remove(mediaDetails);
    }

    @Override
    public void onErrorOccurred() {
        Log.d("file deleted", "false");

    }

    @Override
    public void onFileAdded(MediaDetails mediaDetails) {
        findViewById(R.id.design_bottom_sheet).requestLayout();
        File file = new File(mediaDetails.getFilePath());
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        sendBroadcast(intent);
//        if (gridViewAdapter.getMediaType().equals(mediaDetails.getMediaType())) {
//            gridViewAdapter.addImage(mediaDetails, 0);
//        }
        if (recyclerViewAdapter.getMediaType().equals(mediaDetails.getMediaType())) {
            recyclerViewAdapter.addItem(mediaDetails, 0);
        }
    }

    @Override
    public boolean addPermission(List<String> permissionsList, String permission) {
        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager
                .PERMISSION_GRANTED) {
            permissionsList.add(permission);
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permission))
                return false;
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
//                recyclerGridView.setNumColumns(5);
                break;
            default:
                recyclerGridView.getLayoutParams().height = height - gridViewButton.getHeight();
//                recyclerGridView.setNumColumns(3);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (data != null) {
                ArrayList<Integer> indexes = data.getIntegerArrayListExtra("indexes");
                if (indexes != null && indexes.size() != 0) {
                    for (Integer index : indexes) {
                        recyclerViewAdapter.removeItemAt(index);
                    }
                }
            }
        } else if (requestCode == 321) {
            if (firebaseAuth.getCurrentUser() != null)
                Log.d("email", firebaseAuth.getCurrentUser().getEmail());
            if (firebaseAuth.getCurrentUser() != null) {
                if (databaseReference.getRef().child(firebaseAuth.getCurrentUser().getUid()) ==
                        null) {
                    databaseReference.getRef().setValue((firebaseAuth.getCurrentUser().getUid()));

                }
            }
        }

    }

    public boolean isRotationEnabled() {
        return Settings.System.getInt(getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION, 0) == 1;
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T $(int id) {
        return (T) findViewById(id);
    }

    @Override
    public void onItemClick(View view, int position) {

        MediaDetails details = recyclerViewAdapter.getItemAt(position);
        details.getFilePath();
        ImageView tick = (ImageView) view.findViewById(R.id.tick);
        /**
         * if items are in selection mode,show/hide display the tick icon
         */
        if (onItemClick.isSelectionMode()) {
            mainPresenter.modifySelection(details);
            if (details.isChecked()) {
                tick.setVisibility(View.VISIBLE);
                tickView.put(position, view);
            } else {
                tick.setVisibility(View.INVISIBLE);
                tickView.remove(position);
            }
            if (!onItemClick.isSelectionMode()) {
                recyclerGridView.requestLayout();
//                recyclerGridView.clearChoices();
                gridViewButton.setVisibility(View.VISIBLE);
                menu.setVisibility(View.GONE);
            }
        }


        /**
         * If not in selection mode,fire an Intent to display Fullscreen video/picture
         */
        else {
            Intent intent;
            if (details.getMediaType().equals(Utils.IMAGE)) {
                intent = new Intent(MainActivity.this, FullImageActivity.class);
                intent.putExtra("position", position);

            } else {
                intent = new Intent(MainActivity.this, PlayVideoActivity.class);
                intent.putExtra("path", details.getFilePath());
            }

            startActivityForResult(intent, 123);
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {
        if (!onItemClick.isSelectionMode()) {
            MediaDetails details = recyclerViewAdapter.getItemAt(position);
            mainPresenter.modifySelection(details);
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            ImageView tick = (ImageView) view.findViewById(R.id.tick);
            menu.setVisibility(View.VISIBLE);
            gridViewButton.setVisibility(View.INVISIBLE);
            tick.setVisibility(View.VISIBLE);
            tickView.put(position, view);
        }
    }
}


