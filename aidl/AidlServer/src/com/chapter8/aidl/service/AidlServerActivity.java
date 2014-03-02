package com.chapter8.aidl.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class AidlServerActivity extends Activity {
	private static final String TAG = "AidlServerActivity";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//        Log.i(TAG, "startService");
//        Intent service = new Intent(this, AidlServerService.class);
//        startService(service);
    }
}