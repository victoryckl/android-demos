package com.example.utils;

import java.io.File;

import com.example.utils.Common.Type;

public class PathManager {
	private static final String SDCARD = "/sdcard/eye/";
	private static final String HPDATA = "/hpdata/eye/";
	private static String mRoot = SDCARD;
	private static RandomList mRandomList;
	
	private static void initRoot() {
		File sf = new File(SDCARD);
		File hf = new File(HPDATA);
		if (sf.exists()) {
			mRoot = SDCARD;
		} else if (hf.exists()) {
			mRoot = HPDATA;
		}
	}
	
	private static final String[] mSwf = new String[]{
			"swf/error1.swf",
			"swf/error2.swf",
			"swf/error3.swf",
			"swf/error4.swf",
			"swf/error5.swf",
			"swf/error6.swf",
			"swf/error7.swf",
			"swf/correct1.swf"
	};
	private static final String[] mAvi512 = new String[]{
			"512avi/error1.avi",
			"512avi/error2.avi",
			"512avi/error3.avi",
			"512avi/error4.avi",
			"512avi/error5.avi",
			"512avi/error6.avi",
			"512avi/error7.avi",
			"512avi/correct1.avi"
	};
	private static final String[] mAvi1024 = new String[]{
			"1024avi/error1.avi",
			"1024avi/error2.avi",
			"1024avi/error3.avi",
			"1024avi/error4.avi",
			"1024avi/error5.avi",
			"1024avi/error6.avi",
			"1024avi/error7.avi",
			"1024avi/correct1.avi"
	};
	
	private static int mIndex = -1;
	public static String getPath() {
		return getPath(Common.Type.AVI512);
	}
	
	public static String getPath(String type) {
		initRoot();
		String path[] = mAvi512;
		
		if (type.equals(Common.Type.AVI512)) {
			path = mAvi512;					
		}
		if (type.equals(Common.Type.AVI1024)) {
			path = mAvi1024;
		}
		
		mIndex = (mIndex+1)%path.length;
		if (mRandomList == null 
				|| mRandomList.size() != path.length) {
			mRandomList = new RandomList(path.length);
		}
		if (mIndex == 0) {
			mRandomList.reset();
		}
		
		return mRoot + path[mRandomList.get(mIndex)];
	}
}
