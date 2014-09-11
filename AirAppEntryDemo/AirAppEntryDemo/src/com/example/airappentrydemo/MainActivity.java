package com.example.airappentrydemo;

import java.io.File;

import air.com.adobe.appentry.AppEntry;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class MainActivity extends AppEntry {
	private static final String TAG = MainActivity.class.getSimpleName();
	private String mUnique, mContentName, mLinkPath;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		initMetaData();
		
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		FrameLayout frameLayout = (FrameLayout) findViewById(android.R.id.content);
		LinearLayout linearLayout = (LinearLayout) View.inflate(getApplicationContext(), 
				R.layout.activity_layout, null);
		linearLayout.findViewById(R.id.btn_close).setOnClickListener(mClickListener);
		frameLayout.addView(linearLayout);
	}
	
	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_close:
				MainActivity.this.finish();
				break;

			default:
				break;
			}
		}
	};
	
	@Override
	public void finish() {
		super.finish();
		Process.killProcess(Process.myPid());
	}
	
	private void initMetaData() {
		try {
			ActivityInfo info = getPackageManager().getActivityInfo(
					getComponentName(), PackageManager.GET_META_DATA);
			Bundle meta = info.metaData;
			mUnique = meta.getString("uniqueappversionid");
			mContentName = meta.getString("initialcontent");
			
			File file = new File(new File(getCacheDir(), 
					"app" + File.separator + mUnique + File.separator + "assets"), 
					mContentName);
			mLinkPath = file.getAbsolutePath();
			Log.i(TAG, mLinkPath);
			File f = new File(mLinkPath);
			if (f.exists()) {
				f.delete();
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
}
