package com.example.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class Metrics {
	private static final String TAG = Metrics.class.getSimpleName();
	private static DisplayMetrics mMetrics = new DisplayMetrics();
	private static int mStatusBarHeight;
	private static boolean mIsTablet;
	private static int mOrientation;
	
	private Metrics() {}
	
	public static void initialize(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(mMetrics);
		mStatusBarHeight = initStatusBarHeight(context);
		mIsTablet = initIsTablet(context);
		mOrientation = initOrientation();
		
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
	
	public static boolean isTablet() {
		check();
		return mIsTablet;
	}
	
	private static boolean initIsTablet(Context context) {
	    if (android.os.Build.VERSION.SDK_INT >= 11) { // honeycomb
	        // test screen size, use reflection because isLayoutSizeAtLeast is only available since 11
	        Configuration con = context.getResources().getConfiguration();
	        try {
	            Method mIsLayoutSizeAtLeast = con.getClass().getMethod("isLayoutSizeAtLeast", int.class);
	            Boolean r = (Boolean) mIsLayoutSizeAtLeast.invoke(con, 0x00000004); // Configuration.SCREENLAYOUT_SIZE_XLARGE
	            return r;
	        } catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        }
	    }
	    return false;
	}
	
	//------------
	private static int initOrientation() {
		return mIsTablet ? 
				ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
				:ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
	}
	
	public static int getOrientation() {
		check();
		return mOrientation;
	}
	
	public static void setOrientation(Activity activity) {
		check();
		activity.setRequestedOrientation(mOrientation);
	}
	
	public static boolean isPortrait(Activity activity) {
        int orient = activity.getRequestedOrientation(); 
        if(orient != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE 
        		&& orient != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            WindowManager windowManager = activity.getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            int screenWidth  = display.getWidth();
            int screenHeight = display.getHeight();
            Log.i(TAG, "screenWidth:"+screenWidth+", screenHeight:"+screenHeight);
            orient = screenWidth < screenHeight ? 
            		ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : 
            		ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        }
       	return (orient == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
}
