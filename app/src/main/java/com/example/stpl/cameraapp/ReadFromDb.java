package com.example.stpl.cameraapp;

import android.os.AsyncTask;

import com.example.stpl.cameraapp.Activity.MainActivity;

import java.io.IOException;

/**
 * Created by stpl on 12/14/2016.
 */

public class ReadFromDb extends AsyncTask<Void,Void,Void> {
    MainActivity activity;
    public ReadFromDb(MainActivity activity)
    {
        this.activity=activity;
    }
    @Override
    protected Void doInBackground(Void... params) {
        try {
            activity.getFromSdcard();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
