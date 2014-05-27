package com.cybertron.account.util;

import java.util.List;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class PackageUtils {
	private static final String TAG = PackageUtils.class.getSimpleName();

	private PackageUtils() {
	}
	
	public static List<ApplicationInfo> listApps(PackageManager pm) {
		return listApps(pm, 
				PackageManager.GET_UNINSTALLED_PACKAGES | 
				PackageManager.GET_DISABLED_COMPONENTS);
	}
	
	public static List<ApplicationInfo> listApps(PackageManager pm, int flags) {
		return pm.getInstalledApplications(flags);
	}
	
	public static void printAppInfos(List<ApplicationInfo> infos) {
		for (int i=0; i<infos.size(); i++) {
			ApplicationInfo info = infos.get(i);
			boolean isBoot = ((info.flags & PackageManager.COMPONENT_ENABLED_STATE_DEFAULT) != 0 
					|| (info.flags & PackageManager.COMPONENT_ENABLED_STATE_ENABLED) != 0);
			Log.i(TAG, info.packageName + ": " + isBoot);
		}
	}
	
    public static List<ResolveInfo> listBootStartReceivers(PackageManager pm) {
    	Intent intent = new Intent(Intent.ACTION_BOOT_COMPLETED);
		return pm.queryBroadcastReceivers(intent,
				PackageManager.GET_DISABLED_COMPONENTS);
    }
	
	public static boolean isSystemApp(ApplicationInfo info) {
		if ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0
				|| (info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
			return true;
		}
		return false;
	}

	public static boolean isSystemApp(PackageManager pm, String packageName)
			throws NameNotFoundException {
		ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
		return isSystemApp(info);
	}

	public static boolean isBootStart(PackageManager pm,
			ComponentName componentName) {
		int flag = pm.getComponentEnabledSetting(componentName);
		return ((flag == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT) 
				|| (flag == PackageManager.COMPONENT_ENABLED_STATE_ENABLED));
	}

	public static boolean isBootStart(PackageManager pm, String packageName,
			String cls) {
		ComponentName componentName = new ComponentName(packageName, cls);
		return isBootStart(pm, componentName);
	}
}
