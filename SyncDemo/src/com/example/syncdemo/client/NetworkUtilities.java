package com.example.syncdemo.client;

import android.util.Log;

final public class NetworkUtilities {
    private static final String TAG = NetworkUtilities.class.getSimpleName();

	/**
     * Connects to the SampleSync test server, authenticates the provided
     * username and password.
     *
     * @param username The server account username
     * @param password The server account password
     * @return String The authentication token returned by the server (or null)
     */
    public static String authenticate(String username, String password) {
    	Log.i(TAG, "authenticate...");
    	return "token:authenticate";
    }
}
