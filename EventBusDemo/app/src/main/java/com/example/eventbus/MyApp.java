package com.example.eventbus;

import android.app.Application;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

public class MyApp extends Application {
    private static final String TAG = "MyApp";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "build 1");
        EventBus.builder()
                .addIndex(new MyEventBusIndex())
                .installDefaultEventBus();
        Log.i(TAG, "build 2");
    }
}
