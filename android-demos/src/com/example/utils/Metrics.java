package com.example.utils;

import java.lang.reflect.Field;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class Metrics {
	private static final String TAG = Metrics.class.getSimpleName();
	private static DisplayMetrics mMetrics = new DisplayMetrics();
	private static int mStatusBarHeight;
	
	private Metrics() {}
	
	public static void initialize(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(mMetrics);
		mStatusBarHeight = initStatusBarHeight(context);
		Log.i(TAG, "density: " + mMetrics.density
				+ "\ndensityDpi: " + mMetrics.densityDpi
				+ "\nscaledDensity: " + mMetrics.scaledDensity
				+ "\nwidthPixels: "  + mMetrics.widthPixels
				+ "\nheightPixels: " + mMetrics.heightPixels
				+ "\nxdpi: " + mMetrics.xdpi
				+ "\nydpi: " + mMetrics.xdpi
				+ "\nmStatusBarHeight: " + mStatusBarHeight);
	}
	
	private static void check() {
		if (mMetrics.density < 0.01f) {
			throw new RuntimeException("please call Metrics.initialize() first!!");
		}
	}
	
	public static int px2dp(int px) {
		check();
		return (int) (px/mMetrics.density);
	}
	
	public static int dp2px(int dp) {
		check();
		return (int) (dp*mMetrics.density);
	}
	
	public static float dp2px(float dp) {
		check();
		return dp*mMetrics.density;
	}
	
	public static DisplayMetrics getDisplayMetrics() {
		check();
		return mMetrics;
	}
	
	public static int getWidth() {
		check();
		return mMetrics.widthPixels;
	}
	
	public static int getHeight() {
		check();
		return mMetrics.heightPixels;
	}
	
	//---------------
	public static int getStatusBarHeight(){
		check();
		return mStatusBarHeight;
	}
	
	private static int initStatusBarHeight(Context context){
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }
}
