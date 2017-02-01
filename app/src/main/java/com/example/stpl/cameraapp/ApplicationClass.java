package com.example.stpl.cameraapp;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.picasso.Picasso;


public class ApplicationClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.addRequestHandler(new MyRequestHandler());
        Picasso picasso = builder.build();

        Picasso.setSingletonInstance(picasso);
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.

            return;
        }
        LeakCanary.install(this);

        // Normal app init code...
    }

}
