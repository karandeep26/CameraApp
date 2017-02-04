package com.example.stpl.cameraapp.main;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.stpl.cameraapp.models.MediaDetails;
import com.example.stpl.cameraapp.models.SdCardInteractor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;


class MainPresenterImpl implements MainPresenter, SdCardInteractor.OnFinishedListener {
    private Context mContext;
    private MainView mainView;
    private MainView.FileListener fileListener;
    private SdCardInteractor sdCardInteractor;
    private CompositeSubscription compositeSubscription;
    private int minutes = 0;
    private Subscription subscription;
    private SdCardInteractor.GetMediaList getMediaList;


    MainPresenterImpl(MainView mainView, SdCardInteractor sdCardInteractor) {
        this.mainView = mainView;
        mContext = (Context) mainView;
        this.sdCardInteractor = sdCardInteractor;
        compositeSubscription = new CompositeSubscription();
        fileListener = (MainView.FileListener) mainView;
        getMediaList = (SdCardInteractor.GetMediaList) sdCardInteractor;
    }

    @Override
    public void checkForPermissions() {
        ArrayList<String> permissionNeeded = new ArrayList<>();
        ArrayList<String> permissionList = new ArrayList<>();
        if (!addPermission(permissionList, Manifest.permission.CAMERA))
            permissionNeeded.add("camera");
        if (!addPermission(permissionList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionNeeded.add("write");
        if (!addPermission(permissionList, Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionNeeded.add("read");
        if (!addPermission(permissionList, Manifest.permission.RECORD_AUDIO))
            permissionNeeded.add("record audio");
        if (permissionNeeded.size() > 0) {
            mainView.permissionNotAvailable(permissionNeeded, permissionList);
        } else {
            mainView.permissionAvailable();
        }
        mContext = null;

    }

    @Override
    public void fetchFromSdCard() {
        float startTime = System.currentTimeMillis();
        Log.d("start time", startTime + "");

        subscription = sdCardInteractor.getFromSdCard().observeOn(AndroidSchedulers.mainThread())
                .subscribe(mediaDetails -> {
                            if (mediaDetails.getMediaType().equals("image")) {
                                mainView.itemAdd(mediaDetails);
                            }
                        },
                        throwable -> Log.d("debug", throwable.getMessage()),
                        () -> {
                            subscription.unsubscribe();
                            float endTime = System.currentTimeMillis();
                            float totalTime = endTime - startTime;
                            Log.d("total time******", "****" + totalTime / 1000 + "");
                        });
        compositeSubscription.add(subscription);

    }


    @Override
    public void deleteFromSdCard(ArrayList<MediaDetails> mediaDetails) {
        for (MediaDetails details : mediaDetails) {
            boolean isDeletionSuccessful;
            isDeletionSuccessful = sdCardInteractor.deleteFromSdCard(details);
            if (isDeletionSuccessful) {
                fileListener.onFileDeleted(details);
            } else {
                fileListener.onErrorOccurred();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (compositeSubscription != null && !compositeSubscription.isUnsubscribed()) {
            compositeSubscription.unsubscribe();
        }
        sdCardInteractor = null;
    }

    @Override
    public Subscription startTimer() {

        Subscription timerSubscription = Observable.zip(Observable.range(0, 60),
                Observable.interval(1, TimeUnit.SECONDS), (integer, aLong) -> integer).repeat()
                .observeOn(AndroidSchedulers.mainThread()).subscribe(seconds -> {
                    if (seconds > 9) {
                        mainView.setTimerValue(minutes + ": 0" + seconds);
                    } else {
                        mainView.setTimerValue(minutes + ":" + seconds);
                    }
                    if (seconds == 59) {
                        minutes++;
                    }
                });
        compositeSubscription.add(timerSubscription);
        return timerSubscription;
    }

    @Override
    public int getMediaSize(String mediaType) {
        return sdCardInteractor.getMediaCount(mediaType);
    }

    @Override
    public void savePhotoSdCard(byte[] data) {
        MediaDetails mediaDetails = sdCardInteractor.savePhoto(data);
        if (mediaDetails == null) {
            fileListener.onErrorOccurred();
        } else {
            fileListener.onFileAdded(mediaDetails);
        }
    }

    @Override
    public void getCurrentSavedVideo(String fileName) {
        MediaDetails mediaDetails = sdCardInteractor.getSavedVideo(fileName);
        if (mediaDetails == null) {
            fileListener.onErrorOccurred();
        } else {
            fileListener.onFileAdded(mediaDetails);
        }
    }

    @Override
    public void updateAdapter(String mediaType) {
        ArrayList<MediaDetails> mediaDetails = getMediaList.getMedia(mediaType);
        mainView.updateAdapter(mediaDetails);
    }


    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ActivityCompat.checkSelfPermission(mContext, permission) != PackageManager
                .PERMISSION_GRANTED) {
            permissionsList.add(permission);
            if (!ActivityCompat.shouldShowRequestPermissionRationale((MainActivity) mContext,
                    permission))
                return false;
        }
        return true;
    }

    @Override
    public void onFinished() {

    }


}
