package com.example.stpl.cameraapp.main;

import android.Manifest;
import android.util.Log;

import com.example.stpl.cameraapp.FileListener;
import com.example.stpl.cameraapp.Utils;
import com.example.stpl.cameraapp.models.MediaDetails;
import com.example.stpl.cameraapp.models.SdCardInteractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


class MainPresenterImpl implements MainPresenter, SdCardInteractor.OnFinishedListener,
        MainPresenter.OnItemClick, MainPresenter.Adapter {
    private MainView mainView;
    private MainView.UpdateView updateView;
    private FileListener fileListener;
    private SdCardInteractor sdCardInteractor;
    private CompositeSubscription compositeSubscription;
    private int minutes = 0;
    private Subscription subscription;
    private SdCardInteractor.GetMediaList getMediaList;
    private SdCardInteractor.Selection selection;
    private String mediaType = "image";


    MainPresenterImpl(MainView mainView, SdCardInteractor sdCardInteractor) {
        compositeSubscription = new CompositeSubscription();
        this.sdCardInteractor = sdCardInteractor;
        fileListener = mainView;
        this.updateView = (MainView.UpdateView) mainView;
        getMediaList = (SdCardInteractor.GetMediaList) sdCardInteractor;
        this.mainView = mainView;
        selection = (SdCardInteractor.Selection) sdCardInteractor;
    }

    @Override
    public void checkForPermissions() {
        ArrayList<String> permissionNeeded = new ArrayList<>();
        ArrayList<String> permissionList = new ArrayList<>();
        if (!mainView.addPermission(permissionList, Manifest.permission.CAMERA))
            permissionNeeded.add("camera");
        if (!mainView.addPermission(permissionList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionNeeded.add("write");
        if (!mainView.addPermission(permissionList, Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionNeeded.add("read");
        if (!mainView.addPermission(permissionList, Manifest.permission.RECORD_AUDIO))
            permissionNeeded.add("record audio");
        if (permissionNeeded.size() > 0) {
            mainView.permissionNotAvailable(permissionNeeded, permissionList);
        } else {
            mainView.permissionAvailable();
        }

    }

    @Override
    public void fetchFromSdCard(String mediaType) {
        float startTime = System.currentTimeMillis();
        Log.d("start time", startTime + "");

        subscription = sdCardInteractor.getFromSdCard(mediaType).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(mediaDetails -> {
                            if (mediaDetails != null) {
                                Collections.reverse(mediaDetails);
                                for (MediaDetails temp : mediaDetails) {
                                    if (temp.getMediaType().equals(Utils.IMAGE)) {
                                        mainView.itemAdd(temp);
                                    }
                                }
                            }
                        },
                        throwable -> Log.d("debug", throwable.getMessage()),
                        () -> {
                            subscription.unsubscribe();
                            float endTime = System.currentTimeMillis();
                            float totalTime = endTime - startTime;
                            Log.d("total time", endTime + "");
                        });
        compositeSubscription.add(subscription);

    }


    @Override
    public void deleteFromSdCard() {
        ArrayList<MediaDetails> mediaDetails = selection.getSelectedItems();
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

    public MediaDetails getSelected() {
        return selection.getSelectedItems().get(0);
    }

    @Override
    public void onDestroy() {
        if (compositeSubscription != null && !compositeSubscription.isUnsubscribed()) {
            compositeSubscription.unsubscribe();
        }
        sdCardInteractor = null;
    }

    @Override
    public Subscriber<Integer> startTimer() {
        Subscriber<Integer> subscriber = new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                updateView.setTimerValue("");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer seconds) {
                if (seconds > 9) {
                    updateView.setTimerValue(minutes + ": 0" + seconds);
                } else {
                    updateView.setTimerValue(minutes + ":" + seconds);
                }
                if (seconds == 59) {
                    minutes++;
                }
                Log.d("seconds", seconds + "");

            }
        };
        Observable.zip(Observable.range(0, 60),
                Observable.interval(1, TimeUnit.SECONDS), (integer, aLong) -> integer).repeat()
                .observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);

        compositeSubscription.add(subscriber);
        return subscriber;
    }

    @Override
    public int getMediaSize(String mediaType) {
        return sdCardInteractor.getMediaCount(mediaType);
    }

    @Override
    public String savePhotoSdCard(byte[] data) {
        MediaDetails mediaDetails = sdCardInteractor.savePhoto(data);
        if (mediaDetails == null) {
            fileListener.onErrorOccurred();
            return null;
        } else {
            fileListener.onFileAdded(mediaDetails);
        }
        return mediaDetails.getFilePath();
    }

    @Override
    public void getCurrentSavedVideo(String fileName) {
        MediaDetails mediaDetails = sdCardInteractor.getSavedVideo(fileName);
        if (mediaDetails == null) {
            fileListener.onErrorOccurred();
        } else {
            if (mediaType.equals(mediaDetails.getMediaType())) {
                fileListener.onFileAdded(mediaDetails);
            }
        }
    }

    @Override
    public void updateAdapter(String mediaType) {
        this.mediaType = mediaType;
        sdCardInteractor.getFromSdCard(mediaType).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(mediaDetails -> {
            Collections.reverse(mediaDetails);
            updateView.updateAdapter(mediaDetails);
        });

    }

    @Override
    public void onFinished() {

    }

    @Override
    public boolean modifySelection(MediaDetails mediaDetail) {
        if (mediaDetail.isChecked()) {
            selection.removeFromSelection(mediaDetail);
        } else {
            selection.addToSelection(mediaDetail);
        }
        mediaDetail.toggleChecked();
        return selection.isSelectionMode();
    }


    @Override
    public boolean isSelectionMode() {
        return selection.isSelectionMode();
    }

    @Override
    public void removeSelectedItems() {
        selection.clearAll();
    }
}
