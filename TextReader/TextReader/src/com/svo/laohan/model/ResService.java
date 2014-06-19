package com.svo.laohan.model;

import android.content.Context;
import android.util.Log;

import com.svo.laohan.model.dao.DBHelper;
import com.svo.laohan.model.dao.FileData;

/**
 * @author duweibin 
 * @version 创建时间：2012-11-12 下午8:12:57
 * 类说明
 */
public class ResService {
	//	private static final String TAG = "ResService";
	private Context context;
	private DBHelper dbHelper;
	public ResService(Context context) {
		this.context = context;
		dbHelper = DBHelper.getInstance(context);
	}
	
	
}
