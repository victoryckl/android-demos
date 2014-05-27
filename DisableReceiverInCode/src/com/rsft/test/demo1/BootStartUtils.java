package com.rsft.test.demo1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class BootStartUtils {
	
	private static final String TAG = BootStartUtils.class.getSimpleName();
	 
    private static final String BOOT_START_PERMISSION = "android.permission.RECEIVE_BOOT_COMPLETED";

    private Context mContext;
    PackageManager mPm;

    public BootStartUtils(Context context) {
        mContext = context;
        mPm = mContext.getPackageManager();
    }

    /**
     * 获取Android开机启动列表
     */
    public List<Map<String, Object>> fetchInstalledApps() {
        mPm = mContext.getPackageManager();
        List<ApplicationInfo> appInfo = mPm.getInstalledApplications(0);
        Iterator<ApplicationInfo> appInfoIterator = appInfo.iterator();
        List<Map<String, Object>> appList = new ArrayList<Map<String, Object>>(appInfo.size());

        while (appInfoIterator.hasNext()) {
            ApplicationInfo app = appInfoIterator.next();
            int flag = mPm.checkPermission(BOOT_START_PERMISSION, app.packageName);
            if (flag == PackageManager.PERMISSION_GRANTED) {
                Map<String, Object> appMap = new HashMap<String, Object>();
                String label = mPm.getApplicationLabel(app).toString();
                Drawable icon = mPm.getApplicationIcon(app);
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
    
    public List<ResolveInfo> listBootStartReceivers() {
    	Intent intent = new Intent(Intent.ACTION_BOOT_COMPLETED);
    	intent.getComponent();
    	List<ResolveInfo> resolveInfoList = mPm.queryBroadcastReceivers(intent, PackageManager.GET_DISABLED_COMPONENTS);
    	return resolveInfoList;
    }
    
    /*
    public static final int COMPONENT_ENABLED_STATE_DEFAULT
    Since: API Level 1
    Flag for setApplicationEnabledSetting(String, int, int) and setComponentEnabledSetting(ComponentName, int, int): 
    This component or application is in its default enabled state (as specified in its manifest).
    Constant Value: 0 (0x00000000)
    
    public static final int COMPONENT_ENABLED_STATE_DISABLED
    Since: API Level 1
    Flag for setApplicationEnabledSetting(String, int, int) and setComponentEnabledSetting(ComponentName, int, int): 
    This component or application has been explicitly disabled, regardless of what it has specified in its manifest.
    Constant Value: 2 (0x00000002)
    
    public static final int COMPONENT_ENABLED_STATE_DISABLED_USER
    Since: API Level 14
    Flag for setApplicationEnabledSetting(String, int, int) only: 
    The user has explicitly disabled the application, regardless of what it has specified in its manifest. 
    Because this is due to the user's request, they may re-enable it if desired through the appropriate system UI. 
    This option currently can not be used with setComponentEnabledSetting(ComponentName, int, int).
    Constant Value: 3 (0x00000003)
    
    public static final int COMPONENT_ENABLED_STATE_ENABLED
	Since: API Level 1
	Flag for setApplicationEnabledSetting(String, int, int) and setComponentEnabledSetting(ComponentName, int, int): 
	This component or application has been explictily enabled, regardless of what it has specified in its manifest.
	Constant Value: 1 (0x00000001)
    */
    public void printBootStartReceivers() {
    	List<ResolveInfo> list = listBootStartReceivers();
    	for (int i=0; i<list.size(); i++) {
    		ResolveInfo ri = list.get(i);
    		ComponentName mComponentName = new ComponentName(ri.activityInfo.packageName, ri.activityInfo.name);
    		Log.i(TAG, "state:" + mPm.getComponentEnabledSetting(mComponentName) + ", "
    				+ mComponentName.flattenToString());
    	}
    }
}
