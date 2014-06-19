package com.svo.platform.utils;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class SvoUtil {

	/**
	 * 获取版本号
	 * 
	 * @return 当前应用的版本号
	 */
	public static int getVersion(Activity activity) {
		try {
			PackageManager manager = activity.getPackageManager();
			PackageInfo info = manager.getPackageInfo(activity.getPackageName(), 0);
			return info.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
