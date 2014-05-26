package com.rsft.test.demo1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class BootStartUtils {
	 
    private static final String BOOT_START_PERMISSION = "android.permission.RECEIVE_BOOT_COMPLETED";

    private Context mContext;

    public BootStartUtils(Context context) {
        mContext = context;
    }

    /**
     * 获取Android开机启动列表
     */
    public List<Map<String, Object>> fetchInstalledApps() {
        PackageManager pm = mContext.getPackageManager();
        List<ApplicationInfo> appInfo = pm.getInstalledApplications(0);
        Iterator<ApplicationInfo> appInfoIterator = appInfo.iterator();
        List<Map<String, Object>> appList = new ArrayList<Map<String, Object>>(appInfo.size());

        while (appInfoIterator.hasNext()) {
            ApplicationInfo app = appInfoIterator.next();
            int flag = pm.checkPermission(
                    BOOT_START_PERMISSION, app.packageName);
            if (flag == PackageManager.PERMISSION_GRANTED) {
                Map<String, Object> appMap = new HashMap<String, Object>();
                String label = pm.getApplicationLabel(app).toString();
                Drawable icon = pm.getApplicationIcon(app);
                String desc = app.packageName;
                appMap.put("label", label);
                appMap.put("icon", icon);
                appMap.put("desc", desc);
                appList.add(appMap);
                
                Log.i("BootStartUtils", label);
            }
        }
        return appList;
    }
}
