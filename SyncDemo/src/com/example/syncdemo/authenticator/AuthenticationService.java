package com.example.syncdemo.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Service to handle Account authentication. It instantiates the authenticator
 * and returns its IBinder.
 */
public class AuthenticationService extends Service {

    private static final String TAG = AuthenticationService.class.getSimpleName();

    private Authenticator mAuthenticator;

    @Override
    public void onCreate() {
        Log.v(TAG, "SampleSyncAdapter Authentication Service started.");
        mAuthenticator = new Authenticator(this);
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "SampleSyncAdapter Authentication Service stopped.");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "getBinder()...  returning the AccountAuthenticator binder for intent "
                + intent);
        return mAuthenticator.getIBinder();
    }
}
