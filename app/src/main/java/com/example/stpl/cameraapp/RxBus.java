package com.example.stpl.cameraapp;

import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by stpl on 3/9/2017.
 */

public class RxBus {
    private static RxBus rxBus = new RxBus();
    private final Subject<Integer> bus = PublishSubject.create();

    public static RxBus getInstance() {
        if (rxBus == null) {
            rxBus = new RxBus();
            Log.d("instance created", "****");
        }
        return rxBus;
    }

    public Observable<Integer> getBus() {
        return bus;
    }

    public void send(Integer index) {
        bus.onNext(index);
    }


}
