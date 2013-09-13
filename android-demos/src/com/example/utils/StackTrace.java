package com.example.utils;

import com.example.utils.Log;

public class StackTrace {
	private static final String TAG = "call-stack";
	
	private StackTrace() {
		
	}
	
	public static final void printStackTrace() {
		StackTraceElement[] stackElements = new Throwable().getStackTrace();
		if (stackElements != null) {
			int size = stackElements.length;
			String tag = TAG + " " + Thread.currentThread().getId();
			Log.i(tag, "call-stack begin |------->");
			for (int i=1; i<size; i++) {
				Log.i(tag, stackElements[i].toString());
			}
			Log.i(tag, "call-stack end <-------|");
		}
	}
}
