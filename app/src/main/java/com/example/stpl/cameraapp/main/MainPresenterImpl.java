package com.example.stpl.cameraapp.main;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.stpl.cameraapp.Utils;
import com.example.stpl.cameraapp.models.MediaDetails;
import com.example.stpl.cameraapp.models.SdCardInteractor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.example.stpl.cameraapp.Utils.mediaStorageDir;

/**
 * Created by stpl on 1/20/2017.
 */

class MainPresenterImpl implements MainPresenter, SdCardInteractor.OnFinishedListener {
    private Context mContext;
    private MainView mainView;
    private MainView.FileDeletedListener fileDeletedListener;
    private SdCardInteractor sdCardInteractor;
    private CompositeSubscription compositeSubscription;
    private int minutes = 0;
    private static final Random rand = new Random();

    Subscription subscription;



    MainPresenterImpl(MainView mainView, SdCardInteractor sdCardInteractor) {
        this.mainView = mainView;
        mContext = (Context) mainView;
        this.sdCardInteractor = sdCardInteractor;
        compositeSubscription = new CompositeSubscription();
        fileDeletedListener = (MainView.FileDeletedListener) mainView;
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
        ExecutorService executor = Executors.newFixedThreadPool(5);
        File file = new File(mediaStorageDir.getPath());
        File[] listFile = file.listFiles();
        Observable<File> fileObservable = Observable.from(listFile);
        AtomicInteger group = new AtomicInteger();
        int n = Runtime.getRuntime().availableProcessors();
//        ArrayList<Integer> array=new ArrayList<>();
//        array.add(1);
//        array.add(2);
//        array.add(3);
//        array.add(4);
//        array.add(5);
//        Observable<Integer>vals = Observable.from(array);
//
        fileObservable.flatMap(val -> Observable.just(val)
                .subscribeOn(Schedulers.computation()).map(this::intenseCalculation).toList()
                .subscribe(val -> System.out.println("Subscriber received "
                        + val + " on "
                        + Thread.currentThread().getName()));
        fileObservable.flatMap(Observable::just).subscribeOn(Schedulers.computation())
                .map(file12 -> {

                }).subscribe(bitmap -> {
            Log.d("Subscriber Thread name", Thread.currentThread().getName());

        });
//        subscription = sdCardInteractor.getFromSdCard().subscribeOn(Schedulers.io()).
//                observeOn(AndroidSchedulers.mainThread()).
//                subscribe(mediaDetails -> mainView.itemAdd(mediaDetails),
//                        throwable -> Log.d("debug", throwable.getMessage()),
//                        () -> {
//                            Log.d("debug", "completed");
//                            Log.d("subscription ", subscription.isUnsubscribed() + "");
//                            subscription.unsubscribe();
//                        });
//        compositeSubscription.add(subscription);
    }

    private Bitmap intenseCalculation(File file12) {
        Bitmap bitmap = null;
        String path = file12.getAbsolutePath();
        String newFileName = path.substring(path.lastIndexOf("/") + 1);
        if (newFileName.contains("jpg")) {
            Bitmap image = Utils.decodeSampledBitmapFromFile
                    (file12.getAbsolutePath(), 500, 500);
            bitmap = (ThumbnailUtils.extractThumbnail(image, 500, 500));
        } else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.outWidth = 500;
            options.outHeight = 500;
            ThumbnailUtils
                    .createVideoThumbnail
                            (file12.getAbsolutePath(), MediaStore.Video
                                    .Thumbnails
                                    .MINI_KIND);
        }
        Log.d("worker thread", Thread.currentThread().getName());
        return bitmap;
    }

    @Override
    public void deleteFromSdCard(ArrayList<MediaDetails> mediaDetails) {
        for (MediaDetails details : mediaDetails) {
            boolean isDeletionSuccessful;
            isDeletionSuccessful = sdCardInteractor.deleteFromSdCard(details);
            if (isDeletionSuccessful) {
                fileDeletedListener.onFileDeleted(details);
            } else {
                fileDeletedListener.onErrorOccurred();
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
//    public static void waitSleep() {
//        try {
//            Thread.sleep(20000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//    public static int intenseCalculation(int i) {
//        try {
//            System.out.println("Calculating " + i + " on " + Thread.currentThread().getName());
//            Thread.sleep(randInt(1000,5000));
//            return i;
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public static int randInt(int min, int max) {
//        return rand.nextInt((max - min) + 1) + min;
//    }


}
