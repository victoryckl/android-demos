package com.example.util;

import android.util.Log;

public class StackTrace {
	private static final String TAG = "stack";
	
	private StackTrace() {
		
	}
	
	public static final void printStackTrace() {
		String tag = TAG + " " + Thread.currentThread().getId();
		printStackTrace(tag);
	}
	
	public static final void printStackTrace(String tag) {
		if (tag != null) {
			tag = TAG+"-"+Thread.currentThread().getId()+"-"+tag;
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
