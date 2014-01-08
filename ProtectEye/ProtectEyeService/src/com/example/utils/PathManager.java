package com.example.utils;

import android.content.Context;

import com.example.protecteye.service.R;

public class PathManager {
	
	public static String getPath() {
		return getPath(Common.Type.TPYE1);
	}
	
	public static String getPath(String type) {
		return "/sdcard/v.mp4";
	}
	
	public static String getRawPath(Context context, String type) {
		//assets/v.mp4
		return "android.resource://" + context.getPackageName() + "/" + R.raw.v;
	}
}
