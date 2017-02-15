package com.example.stpl.cameraapp.fullImageView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.stpl.cameraapp.FileListener;
import com.example.stpl.cameraapp.R;
import com.example.stpl.cameraapp.Utils;
import com.example.stpl.cameraapp.ZoomOutPageTransformer;
import com.example.stpl.cameraapp.adapters.CustomViewPagerAdapter;
import com.example.stpl.cameraapp.main.MainActivity;
import com.example.stpl.cameraapp.models.MediaDetails;
import com.example.stpl.cameraapp.models.SdCardInteractorImpl;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FullImageActivity extends AppCompatActivity implements View.OnClickListener,
        FullImageView, FileListener {
    int position;
    ViewPager mViewPager;
    FirebaseAuth firebaseAuth;
    LinearLayout topPanel;
    int visibility;
    ImageButton upload, delete;
    StorageReference uploadReference;
    FullImagePresenterImpl fullImagePresenterImpl;
    FullImageInterface fullImageInterface;
    CustomViewPagerAdapter customViewPagerAdapter;
    ArrayList<Integer> indexes = new ArrayList<>();
    int currentItem = -1;
    boolean deleteClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_full_image);
        findViewById();
        setOnClickListeners();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        Utils.height = displaymetrics.heightPixels;
        Utils.width = displaymetrics.widthPixels;
        fullImagePresenterImpl = new FullImagePresenterImpl(this, new SdCardInteractorImpl());
        fullImageInterface = fullImagePresenterImpl;
        fullImageInterface.fetchImages();
        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);
        Log.d("position", position + "");
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (deleteClicked && state == ViewPager.SCROLL_STATE_IDLE) {
//                    customViewPagerAdapter.removeItemAt(currentItem);
//                    deleteClicked =false;
                }

            }
        });

        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            private float pointX;
            private float pointY;
            private int tolerance = 50;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        return false; //This is important, if you return TRUE the action of swipe
                    // will not take place.
                    case MotionEvent.ACTION_DOWN:
                        pointX = event.getX();
                        pointY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        boolean sameX = pointX + tolerance > event.getX() && pointX - tolerance <
                                event.getX();
                        boolean sameY = pointY + tolerance > event.getY() && pointY - tolerance <
                                event.getY();
                        if (sameX && sameY) {
                            toggleTopPanelVisibility();
                            //The user "clicked" certain point in the screen or just returned to
                            // the same position an raised the finger
                        }
                }
                return false;
            }
        });


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
        makeFullScreen();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete:
                currentItem = mViewPager.getCurrentItem();
                MediaDetails mediaDetails = customViewPagerAdapter.getObjectAt(currentItem);
                fullImageInterface.deleteFile(mediaDetails);

            case R.id.upload:
//                try {
//                    InputStream inputStream = new FileInputStream(new File(Utils.mediaStorageDir
//                            + "/" + mediaDetails.
//                            get(mViewPager.getCurrentItem()).getFilePath()));
//                    UploadTask uploadTask = uploadReference.putStream(inputStream);
//                    uploadTask.addOnSuccessListener(taskSnapshot -> Log.d("file uploaded", "true")).
//                            addOnFailureListener(e -> Log.e("error", e.getMessage())).
//                            addOnProgressListener(taskSnapshot -> Log.d("bytes", taskSnapshot
//                                    .getBytesTransferred() / 1024 + ""));
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
        }

    }

    private void findViewById() {
        mViewPager = (ViewPager) findViewById(R.id.pager);
        topPanel = (LinearLayout) findViewById(R.id.topPanel);
        upload = (ImageButton) findViewById(R.id.upload);
        delete = (ImageButton) findViewById(R.id.delete);
    }

    private void setOnClickListeners() {
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
    public void updateAdapter(ArrayList<MediaDetails> mediaDetails) {
        customViewPagerAdapter = new CustomViewPagerAdapter(this, mediaDetails);
        mViewPager.setAdapter(customViewPagerAdapter);
        mediaDetails.get(position).getFilePath();
        mViewPager.setCurrentItem(position);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putIntegerArrayListExtra("indexes", indexes);
        setResult(123, intent);
        super.onBackPressed();

    }

    @Override
    public void onFileDeleted(MediaDetails mediaDetails) {
        indexes.add(currentItem);
        Object object = mViewPager.getChildAt(currentItem + 1);
        customViewPagerAdapter.removeItemAt(currentItem);
//        mViewPager.setCurrentItem(currentItem,true);
        if (mViewPager.getChildCount() == 0) {
            onBackPressed();
        }
//        customViewPagerAdapter.removeItemAt(currentItem);
        customViewPagerAdapter.destroyItem(mViewPager, currentItem, object);

        deleteClicked = true;
    }

    @Override
    public void onErrorOccurred() {

    }

    @Override
    public void onFileAdded(MediaDetails mediaDetails) {

    }

}
