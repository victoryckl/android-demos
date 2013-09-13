package com.example.utils;

public class Log {
	private static boolean DEBUG_ON = true;

    public static final int VERBOSE = android.util.Log.VERBOSE;
    public static final int DEBUG = android.util.Log.DEBUG;
    public static final int INFO = android.util.Log.INFO;
    public static final int WARN = android.util.Log.WARN;
    public static final int ERROR = android.util.Log.ERROR;
    public static final int ASSERT = android.util.Log.ASSERT;

    private Log() {
    }

    public static int v(String tag, String msg) {
    	if (DEBUG_ON) {
    		return android.util.Log.v(tag, msg);
    	}
    	return 0;
    }

    public static int v(String tag, String msg, Throwable tr) {
    	if (DEBUG_ON) {
    		return android.util.Log.v(tag, msg, tr);
    	}
    	return 0;
    }

    public static int d(String tag, String msg) {
    	if (DEBUG_ON) {
    		return android.util.Log.d(tag, msg);
    	}
    	return 0;
    }
    
    public static int d(String tag, String msg, Throwable tr) {
    	if (DEBUG_ON) {
    		return android.util.Log.d(tag, msg, tr);
    	}
    	return 0;
    }

    public static int i(String tag, String msg) {
    	if (DEBUG_ON) {
    		return android.util.Log.i(tag, msg);
    	}
    	return 0;
    }

    public static int i(String tag, String msg, Throwable tr) {
    	if (DEBUG_ON) {
    		return android.util.Log.i(tag, msg, tr);
    	}
    	return 0;
    }

    public static int w(String tag, String msg) {
    	if (DEBUG_ON) {
    		return android.util.Log.w(tag, msg);
    	}
    	return 0;
    }

    public static int w(String tag, String msg, Throwable tr) {
    	if (DEBUG_ON) {
    		return android.util.Log.w(tag, msg, tr);
    	}
    	return 0;
    }

    public static boolean isLoggable(String tag, int level) {
    	return android.util.Log.isLoggable(tag, level);
    }

    public static int w(String tag, Throwable tr) {
    	if (DEBUG_ON) {
    		return android.util.Log.w(tag, tr);
    	}
    	return 0;
    }

    public static int e(String tag, String msg) {
        return android.util.Log.e(tag, msg);
    }

    public static int e(String tag, String msg, Throwable tr) {
        return android.util.Log.e(tag, msg, tr);
    }

    public static int wtf(String tag, String msg) {
        return wtf(tag, msg, null);
    }

    public static int wtf(String tag, Throwable tr) {
        return wtf(tag, tr.getMessage(), tr);
    }

    public static int wtf(String tag, String msg, Throwable tr) {
    	return android.util.Log.wtf(tag, msg, tr);
    }

    public static String getStackTraceString(Throwable tr) {
        return android.util.Log.getStackTraceString(tr);
    }

    public static int println(int priority, String tag, String msg) {
        return android.util.Log.println(priority, tag, msg);
    }

}

