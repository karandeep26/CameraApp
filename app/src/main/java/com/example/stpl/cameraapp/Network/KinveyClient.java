package com.example.stpl.cameraapp.Network;

import android.content.Context;
import android.util.Log;

import com.example.stpl.cameraapp.loginInterface;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;

/**
 * Created by stpl on 1/11/2017.
 */

public class KinveyClient {
    private Client kinveyClient;
    private static String APPKEY = "kid_BJH4mr6me";
    private static String APPSECRET = "bf1e29b625dc4537819d284c6a73869b";
    loginInterface mLoginInterface;

    public KinveyClient(Context context,loginInterface mLoginInterface) {
        this.kinveyClient = new Client.Builder(APPKEY, APPSECRET, context).build();
        this.mLoginInterface=mLoginInterface;
    }

    public void login() {
        kinveyClient.user().login(new KinveyUserCallback() {
            @Override
            public void onSuccess(User user) {
                mLoginInterface.onSuccess();
            }

            @Override
            public void onFailure(Throwable throwable) {
                mLoginInterface.onFailure();
            }
        });
    }

}
