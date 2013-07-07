package com.example.utils;

import android.content.Context;
import android.widget.Toast;

public class SToast {
	private static final String TAG = SToast.class.getSimpleName();

	private static Toast mToast = null;
	
	private SToast() {}
	
	private static void init(Context context) {
		if (mToast == null) {
			mToast = Toast.makeText(context.getApplicationContext(), "", Toast.LENGTH_SHORT);
		}
	}
	
	public static void show(Context context, String text, int duration) {
		init(context);
		mToast.setText(text);
		mToast.setDuration(duration);
		mToast.show();
	}
	
	public static void show(Context context, int string_id, int duration) {
		init(context);
		mToast.setText(string_id);
		mToast.setDuration(duration);
		mToast.show();
	}
	
	public static void show(Context context, String text) {
		init(context);
		mToast.setText(text);
		mToast.show();
	}
	
	public static void show(Context context, int string_id) {
		init(context);
		mToast.setText(string_id);
		mToast.show();
	}
}
