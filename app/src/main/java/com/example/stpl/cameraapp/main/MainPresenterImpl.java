package com.example.stpl.cameraapp.main;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by stpl on 1/20/2017.
 */

class MainPresenterImpl implements MainPresenter, SdCardInteractor.OnFinishedListener {
    private Context mContext;
    private MainView mainView;
    private SdCardInteractor sdCardInteractor;
    private Subscription subscription;

    MainPresenterImpl(MainView mainView, SdCardInteractor sdCardInteractor) {
        this.mainView = mainView;
        mContext = (Context) mainView;
        this.sdCardInteractor = sdCardInteractor;
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
        subscription = sdCardInteractor.getFromSdCard().subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(mediaDetails -> mainView.itemAdd(mediaDetails),
                        throwable -> Log.d("debug", throwable.getMessage()),
                        () -> Log.d("debug", "completed"));
    }

    @Override
    public void onDestroy() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
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
