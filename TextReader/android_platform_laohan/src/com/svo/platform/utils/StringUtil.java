package com.svo.platform.utils;

import java.util.HashMap;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class StringUtil {
	
	private static final String TAG = "StringUtil";

    
    /**
     * 根据一个文件的绝对路径得到文件所在的目录路径和文件名
     * @param filePath 某文件的绝对路径
     * @return 长度为2的数组,第一个元素是目录路径,第二个元素是文件名.可能为null
     */
    public  static String[] sepPath(String filePath) {
    	if (filePath == null || !filePath.contains("/") || (filePath.lastIndexOf("/") == (filePath.length() - 1))) {
    		Log.e(TAG, "不是合法的文件路径");
			return null;
		}
    	int index = filePath.lastIndexOf("/")+1;
    	String[] arr = new String[2];
    	arr[0] = filePath.substring(0,index);
    	arr[1] = filePath.substring(index);
    	return arr;
	}
    
	
	/**
	 * 获得应用程序名字
	 * @param context
	 * @return
	 */
	public static String getApplicationName(Context context) { 
        PackageManager packageManager = null; 
        ApplicationInfo applicationInfo = null; 
        try { 
            packageManager = context.getPackageManager(); 
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0); 
        } catch (PackageManager.NameNotFoundException e) { 
            applicationInfo = null; 
        } 
        String applicationName =  
        (String) packageManager.getApplicationLabel(applicationInfo); 
        return applicationName; 
    } 
	/**
	 * 获得url中键值对
	 * @param url
	 * @return
	 */
	public static HashMap<String, String> paserParams(String url) {
		String params = url.substring(url.indexOf("?")+1);
		String[] arr = params.split("&");
		HashMap<String, String> maps = new HashMap<String, String>();
		for (String s : arr) {
			maps.put(s.substring(0, s.indexOf("=")), s.substring(s.indexOf("=")+1));
		}
		return maps;
	}
}
