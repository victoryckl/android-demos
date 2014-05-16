package com.example.receiver;

import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * @brief 存储状�?接收�?
 * @author join
 */
public class StorageReceiver extends BroadcastReceiver {

    static final String TAG = "StorageReceiver";
    static final boolean DEBUG = false;

    private static Map<Context, StorageReceiver> mReceiverMap = new HashMap<Context, StorageReceiver>();

    private OnStorageListener mListener;

    public StorageReceiver(OnStorageListener listener) {
        mListener = listener;
    }

    /**
     * 注册
     */
    public static void register(Context context, OnStorageListener listener) {
        if (mReceiverMap.containsKey(context)) {
            if (DEBUG)
                Log.d(TAG, "This context already registered.");
            return;
        }

        StorageReceiver receiver = new StorageReceiver(listener);

        final IntentFilter filter = new IntentFilter();
        // filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        // filter.addAction(Intent.ACTION_MEDIA_BUTTON);
        // filter.addAction(Intent.ACTION_MEDIA_CHECKING);
        // filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        // filter.addAction(Intent.ACTION_MEDIA_NOFS);
        // filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        // filter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        // filter.addAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        // filter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        // filter.addAction(Intent.ACTION_MEDIA_SHARED);
        // filter.addAction(Intent.ACTION_MEDIA_UNMOUNTABLE);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        // 必须添加，否则无法接收到广播
        filter.addDataScheme("file");
        context.registerReceiver(receiver, filter);

        mReceiverMap.put(context, receiver);

        if (DEBUG)
            Log.d(TAG, "StorageReceiver registered.");
    }

    /**
     * 注销
     */
    public static void unregister(Context context) {
        StorageReceiver receiver = mReceiverMap.remove(context);
        if (receiver != null) {
            context.unregisterReceiver(receiver);
            receiver = null;

            if (DEBUG)
                Log.d(TAG, "StorageReceiver unregistered.");
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (DEBUG)
            Log.d(TAG, action);
        if (mListener == null) {
            return;
        }
        if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
            mListener.onMounted();
        } else {
            mListener