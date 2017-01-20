package com.example.stpl.cameraapp.main;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stpl on 1/20/2017.
 */

public class MainPresenterImpl implements MainPresenter {
    private Context mContext;
    private MainView mainView;

    MainPresenterImpl(Context context, MainView mainView) {
        this.mainView = mainView;
        mContext = context;
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
}
