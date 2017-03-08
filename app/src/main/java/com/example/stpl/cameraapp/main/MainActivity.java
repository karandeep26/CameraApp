package com.example.stpl.cameraapp.main;

import android.Manifest;
import android.app.SharedElementCallback;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.stpl.cameraapp.BaseActivity;
import com.example.stpl.cameraapp.CustomCamera;
import com.example.stpl.cameraapp.FileListener;
import com.example.stpl.cameraapp.ItemOffsetDecoration;
import com.example.stpl.cameraapp.MyGestureDetector;
import com.example.stpl.cameraapp.R;
import com.example.stpl.cameraapp.RecyclerItemClickListener;
import com.example.stpl.cameraapp.ScrollListener;
import com.example.stpl.cameraapp.Utils;
import com.example.stpl.cameraapp.activity.PlayVideoActivity;
import com.example.stpl.cameraapp.adapters.RecyclerViewAdapter;
import com.example.stpl.cameraapp.fullImageView.FullImageActivity;
import com.example.stpl.cameraapp.login.FirebaseLoginImpl;
import com.example.stpl.cameraapp.login.FirebaseLoginPresenter;
import com.example.stpl.cameraapp.login.FirebaseLoginView;
import com.example.stpl.cameraapp.models.MediaDetails;
import com.example.stpl.cameraapp.models.SdCardInteractorImpl;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

import static com.example.stpl.cameraapp.Utils.IMAGE;
import static com.example.stpl.cameraapp.Utils.MULTIPLE_PERMISSIONS;
import static com.example.stpl.cameraapp.Utils.RC_SIGN_IN;
import static com.example.stpl.cameraapp.Utils.ROTATION_270;
import static com.example.stpl.cameraapp.Utils.ROTATION_90;
import static com.example.stpl.cameraapp.Utils.ROTATION_O;
import static com.example.stpl.cameraapp.Utils.VIDEO;


public class MainActivity extends BaseActivity implements View.OnClickListener,
        FileListener, RecyclerItemClickListener.OnItemClickListener, MainView, MainView.UpdateView,
        FirebaseLoginView {
    int previousRotation = -1;
    int positionReturned;
    CompositeDisposable compositeDisposable;
    boolean recording = false;
    TextView time;
    GestureDetectorCompat imageGestureDetector;
    int height;
    DisposableObserver<Integer> timerObserver;
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
    RecyclerViewAdapter recyclerViewAdapter;
    GridLayoutManager gridLayoutManager;
    FirebaseLoginPresenter firebaseLoginPresenter;
    FirebaseMainPresenter firebaseMainPresenter;
    Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setExitSharedElementCallback(sharedElementCallback());
        providers = new ArrayList<>();
        providers.add(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());

        // Calculate Screen Height

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        Utils.height = this.height;
        Utils.width = displayMetrics.widthPixels;
//        String[] projection={ MediaStore.Images.Media._ID};
//        Cursor mediaCursor = getContentResolver().query(MediaStore.Files.getContentUri
//                        ("external"),
//                projection,
//                MediaStore.Images.Media.DATA + " like ? ",
//                new String[]{"%"+ mediaStorageDir+"%"},
//                null);
//        if(mediaCursor!=null) {
//            mediaCursor.getCount();
//            if (mediaCursor.moveToFirst()) {
//                do {
//                    int index = mediaCursor.getColumnIndex(MediaStore.Images.Media._ID);
//                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT
//
//                }
//                while (mediaCursor.moveToNext());
//                mediaCursor.close();
//            }
//        }

        // Initialize MVP components

        initMVP();


        // Set Window Flags to make app full screen

        makeFullScreen();

        // Standard findViewById

        findViewById();
        /*
        Initialize RecyclerViewAdapter
        Set GridView
        Set BottomSheetCallback
        Check for permissions
         */

        init();


        // Set button on click listeners

        setClickListeners();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //Take photo
            case R.id.capture:
                if (safeToTakePicture) {
                    customCamera.takePicture();
                }
                break;

            // Record Video

            case R.id.record_video:
                recordVideo();
                break;

            //Load Video thumbnails in GridView

            case R.id.videos:
                video.setSelected(true);
                presenterAdapter.updateAdapter(VIDEO);
                if (pictures.isSelected()) {
                    pictures.setSelected(false);
                }
                break;

            // Loading pictures thumbnails in a GridView

            case R.id.pictures:
                presenterAdapter.updateAdapter(IMAGE);
                pictures.setSelected(true);
                if (video.isSelected()) {
                    video.setSelected(false);
                }
                break;

            // Upload pictures to the Firebase Cloud

            case R.id.upload:
                firebaseMainPresenter.uploadToCloud(mainPresenter.getSelected().getFilePath());
                break;
            //Delete pictures/videos from the phone
            case R.id.delete:
                mainPresenter.deleteFromSdCard();
                //Restore GridView from the selection mode
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
                    //Permissions are available,set up the screen now
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
            compositeDisposable.dispose();
        }
        Glide.with(this).pauseRequests();
    }


    @Override
    protected void onResume() {
        super.onResume();
        makeFullScreen();

        if (customCamera != null && customCamera.getCamera() == null) {
            compositeDisposable = new CompositeDisposable();
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                Display getOrient = getWindowManager().getDefaultDisplay();

                if (getOrient.getRotation() == 0) {
                    customCamera.setCamera();
                }
            } else {
                customCamera.setCamera();
            }

            boolean flag = compositeDisposable.add(getTakePictureDisposable());
            Log.d("take picture", flag + "");
            flag = compositeDisposable.add(getRotationDisposable());
            Log.d("rotation", flag + "");


        }
    }


    // Standard findViewByIds

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
        upload = $(R.id.upload);

    }


    //Initialise GridView and other items

    private void init() {
        compositeDisposable = new CompositeDisposable();
        recyclerViewAdapter = new RecyclerViewAdapter();
        gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerGridView.setLayoutManager(gridLayoutManager);
        recyclerGridView.setAdapter(recyclerViewAdapter);
        recyclerGridView.addOnScrollListener(new ScrollListener(this));
        recyclerGridView.setHasFixedSize(true);
        recyclerGridView.getLayoutParams().height = height;
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
        recyclerGridView.addItemDecoration(itemDecoration);
        pictures.setSelected(true);

        // If first item of GridView is Visible,Disable GridView Scrolling top to bottom.
        // If first item is not visible,continue with gridView scroll scrolling

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
                    Glide.with(MainActivity.this).resumeRequests();
                    bottomSheet.requestLayout();
                    bottomSheet.invalidate();
                    recyclerGridView.requestLayout();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

                } else {
                    recyclerGridView.scrollToPosition(0);

                    Glide.with(MainActivity.this).pauseRequests();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        };
        bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback);
        gridViewButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                recyclerGridView.getLayoutParams().height = height - gridViewButton.getHeight();
                if (gridViewButton.getHeight() > 0) {
                    gridViewButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });


        imageGestureDetector = new GestureDetectorCompat(this,
                new MyGestureDetector(gridLayoutManager, bottomSheetBehavior));
        mainPresenter.checkForPermissions();
    }

    private void setClickListeners() {
        videoCapture.setOnClickListener(this);
        captureButton.setOnClickListener(this);
        pictures.setOnClickListener(this);
        video.setOnClickListener(this);
        upload.setOnClickListener(this);
        delete.setOnClickListener(this);
    }


    @Override
    public void onBackPressed() {

        //If GridView is in selection mode,clear the selections

        if (onItemClick.isSelectionMode()) {
            mainPresenter.removeSelectedItems();
            for (int i = 0; i < tickView.size(); i++) {
                tickView.get(tickView.keyAt(i)).findViewById(R.id.tick).setVisibility(View.GONE);
            }
            gridViewButton.setVisibility(View.VISIBLE);
            menu.setVisibility(View.GONE);

        }
        // If bottom sheet is open,hide it.


        else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        // Exit the Activity

        else {
            Glide.with(this).pauseRequests();
            finish();
        }
    }


    @Override
    protected void onDestroy() {

        if (customCamera != null && customCamera.getCamera() != null) {
            customCamera.releaseCamera();
        }
        if (mainPresenter != null) {
            mainPresenter.onDestroy();
            mainPresenter = null;
        }
        compositeDisposable.clear();
        super.onDestroy();

    }


    /**
     * Start/Stop recording the video
     */

    private void recordVideo() {
        if (!recording) {
            recording = true;
            time.setVisibility(View.VISIBLE);
            customCamera.recordVideo();
            timerObserver = mainPresenter.startTimer();
        } else {
            time.setVisibility(View.GONE);
            customCamera.stopVideo();
            recording = false;

            if (timerObserver != null && !timerObserver.isDisposed()) {
                timerObserver.onComplete();
                timerObserver.dispose();
            }
        }
    }

    /**
     * Loads the camera and thumbnails from the sdCard.
     * Hack for nested scrollView and gridView using MyGestureDetector
     */
    @Override
    public void permissionAvailable() {
        customCamera = new CustomCamera(this, mainPresenter);

        // To overlay capture button

        customCamera.setWillNotDraw(false);
        frameLayout.addView(customCamera);
        presenterAdapter.updateAdapter(Utils.IMAGE);
        compositeDisposable.add(getRotationDisposable());
        compositeDisposable.add(getTakePictureDisposable());

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
    public void updateAdapter(List<MediaDetails> mediaDetails, String type) {
        recyclerGridView.scrollToPosition(0);
        recyclerViewAdapter.setMediaDetailsList(mediaDetails, type);
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
        recyclerGridView.getLayoutParams().height = height - gridViewButton.getHeight();
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                gridLayoutManager.setSpanCount(5);
                break;
            default:
                gridLayoutManager.setSpanCount(3);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == ResultCodes.OK) {
                loggedIn(response != null ? response.getEmail() : null);

            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button

                } else if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {

                } else if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {

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

    /**
     * Handles RecyclerViewItem OnClick
     *
     * @param view     that is clicked
     * @param position of the clicked View
     */

    @Override
    public void onItemClick(View view, int position, float x, float y) {

        MediaDetails details = recyclerViewAdapter.getItemAt(position);


        ImageView tick = (ImageView) view.findViewById(R.id.tick);
        ActivityOptionsCompat options;


        //if items are in selection mode,show/hide display the tick icon

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
                gridViewButton.setVisibility(View.VISIBLE);
                menu.setVisibility(View.GONE);
            }


        }
        // If not in selection mode,fire an Intent to display Fullscreen video/picture
        else {
            Intent intent;
            if (details.getMediaType().equals(IMAGE)) {
                intent = new Intent(MainActivity.this, FullImageActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("path", details.getFilePath());
                ImageView image = (ImageView) view.findViewById(R.id.image);
                Log.d("transition name", image.getTransitionName());
                options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(this, image, details.getFilePath() + "");
                ActivityCompat.startActivity(this, intent, options.toBundle());
            } else {
                intent = new Intent(MainActivity.this, PlayVideoActivity.class);
                intent.putExtra("path", details.getFilePath());
                startActivity(intent);

            }


        }
    }

    /**
     * Handles LongPress on RecyclerViewItem Click
     *
     * @param view     that is long pressed
     * @param position position of the view.
     */
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

    @Override
    public void moveToLogin() {
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                .setProviders(providers).build(), 321);
    }

    @Override
    public void loggedIn(String user) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.slide_panel), "Signed in using " +
                user, Snackbar.LENGTH_SHORT);
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.show();
        Log.d("already logged in", user);
    }

    private void initMVP() {
        mSdCardInteractorImpl = new SdCardInteractorImpl();
        mainPresenterImpl = new MainPresenterImpl(this, mSdCardInteractorImpl);
        mainPresenter = mainPresenterImpl;
        presenterAdapter = mainPresenterImpl;
        onItemClick = mainPresenterImpl;
        firebaseLoginPresenter = new FirebaseLoginImpl(this);
        firebaseMainPresenter = new FirebaseMainImpl();


    }

    private SharedElementCallback sharedElementCallback() {
        return new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                if (bundle != null) {
                    View view;
                    int position = bundle.getInt("position");
                    int correctPosition = position - gridLayoutManager
                            .findFirstVisibleItemPosition();
                    view = recyclerGridView.getChildAt(correctPosition);
                    if (view != null) {
                        ImageView imageView = (ImageView) view.findViewById(R.id.image);
                        if (imageView != null) {
                            if (imageView.getTransitionName() != null) {
                                names.clear();
                                names.add(imageView.getTransitionName());
                                sharedElements.clear();
                                Log.d("****",imageView.getTransitionName());
                                sharedElements.put(imageView.getTransitionName(), imageView);
                            } else {
                                Log.d("transition name is null", "true");
                            }
                        } else {
                            Log.d("imageview is null", "true");
                        }

                    } else {
                        Log.d("View is null", "true");
                    }
                }
                bundle = null;


            }
        };
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        if (data != null) {
            postponeEnterTransition();
            bundle = new Bundle(data.getExtras());
            positionReturned = bundle.getInt("position");
            ArrayList<Integer> indexes = bundle.getIntegerArrayList("indexes");
            if (indexes != null && indexes.size() > 0) {
                for (Integer i : indexes) {
                    recyclerViewAdapter.removeItemAt(i);
                }
            }
            gridLayoutManager.scrollToPosition(positionReturned);

            recyclerGridView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver
                    .OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    recyclerGridView.getViewTreeObserver().removeOnPreDrawListener(this);
                    recyclerGridView.requestLayout();
                    startPostponedEnterTransition();
                    return true;
                }
            });


        }
    }

    private Disposable getRotationDisposable() {
        return customCamera._getRotation().subscribe(currentRotation -> {
            if (previousRotation != currentRotation) {
                if (currentRotation == ROTATION_O) {
                    captureButton.animate().rotation(0);
                    videoCapture.animate().rotation(0);
                } else if (currentRotation == ROTATION_90) {
                    captureButton.animate().rotation(90).start();
                    videoCapture.animate().rotation(90).start();
                } else if (currentRotation == ROTATION_270) {
                    captureButton.animate().rotation(-90).start();
                    videoCapture.animate().rotation(-90).start();
                }
                previousRotation = currentRotation;
                Log.d("rotation blocked", "called");
            }
        });
    }

    private Disposable getTakePictureDisposable() {
        return customCamera._getTakePictureSubject()
                .subscribe(safeToTakePicture -> this.safeToTakePicture = safeToTakePicture);
    }

}


