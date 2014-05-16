package com.example.receiver;

import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * @brief ÍøÂç×´Ì¬½ÓÊÕÕß
 */
public class NetworkReceiver extends BroadcastReceiver {

    static final String TAG = "NetworkReceiver";
    static final boolean DEBUG = false;

    private static Map<Context, NetworkReceiver> mReceiverMap = new HashMap<Context, NetworkReceiver>();

    private OnNetworkListener mListener;

    public NetworkReceiver(OnNetworkListener listener) {
        mListener = listener;
    }

    /**
     * ×¢²á
     */
    public static void register(Context context, OnNetworkListener listener) {
        if (mReceiverMap.containsKey(context)) {
            if (DEBUG)
                Log.d(TAG, "This context already registered.");
            return;
        }

        NetworkReceiver receiver = new NetworkReceiver(listener);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(receiver, filter);

        mReceiverMap.put(context, receiver);

        if (DEBUG)
            Log.d(TAG, "NetworkReceiver registered.");
    }

    /**
     * ×¢Ïú
     */
    public static void unregister(Context context) {
        NetworkReceiver receiver = mReceiverMap.remove(context);
        if (receiver != null) {
            context.unregisterReceiver(receiver);
            receiver = null;

            if (DEBUG)
                Log.d(TAG, "NetworkReceiver unregistered.");
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager conn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conn.getActiveNetworkInfo();

        if (DEBUG)
            Log.d(TAG, intent.getAction() + "\ngetActiveNetworkInfo: " + info);

        if (info != null) {
            boolean isWifi = info.getType() == ConnectivityManager.TYPE_WIFI;
            if (mListener != null) {
                mListener.onConnected(isWifi);
            }
        } else {
            if (mListener != null) {
                mListener.onDisconnected();
            }
        }
    }
}