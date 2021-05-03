package com.example.util;

import android.util.Log;

public class StackTrace {
    private static final String TAG = "st";

    private StackTrace() {

    }

    public static void printStackTrace() {
        printStackTrace(null);
    }

    public static void printStackTrace(String tag) {
        if (tag != null) {
            tag = TAG+"-"+tag+"-"+Thread.currentThread().getId();
        } else {
            tag = TAG+"-"+Thread.currentThread().getId();
        }
        StackTraceElement[] stackElements = new Throwable().getStackTrace();
        if (stackElements != null) {
            int size = stackElements.length;
            Log.i(tag, "begin |------->");
            for (int i=1; i<size; i++) Log.i(tag, stackElements[i].toString());
            Log.i(tag, "end <-------|");
        }
    }
}
