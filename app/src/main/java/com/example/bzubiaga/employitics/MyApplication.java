package com.example.bzubiaga.employitics;

import android.app.Application;
import android.content.Context;

/**
 * Created by bzubiaga on 12/21/15.
 */
public class MyApplication extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}