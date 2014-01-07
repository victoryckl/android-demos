package com.example.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

public class Metrics {
	private static final String TAG = Metrics.class.getSimpleName();
	private static DisplayMetrics mMetrics = new DisplayMetrics();
	
	private Metrics() {}
	
	public static void init(Activity activity) {
		activity.getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
		Log.i(TAG, "density: " + mMetrics.density
				+ "\ndensityDpi: " + mMetrics.densityDpi
				+ "\nscaledDensity: " + mMetrics.scaledDensity
				+ "\nwidthPixels: "  + mMetrics.widthPixels
				+ "\nheightPixels: " + mMetrics.heightPixels
				+ "\nxdpi: " + mMetrics.xdpi
				+ "\nydpi: " + mMetrics.xdpi);
	}
	
	private static void check() {
		if (mMetrics.density < 0.01f) {
			throw new RuntimeException("please call Metrics.init() first!!");
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
}
