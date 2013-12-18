package com.example.multiscreen;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MultiScreenActivity extends Activity {
	private static final String TAG = MultiScreenActivity.class.getSimpleName();
	
	private DisplayMetrics mDisplayMetrics;
	private TextView mTvDeviceInfo, mTvDeviceInfoValues; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multi_screen);
		
		init();
	}

	private void init() {
		mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		
		int sw = mDisplayMetrics.widthPixels < mDisplayMetrics.heightPixels ?
				mDisplayMetrics.widthPixels:mDisplayMetrics.heightPixels;
		float swdp = sw/mDisplayMetrics.density;
		
		mTvDeviceInfo = (TextView)findViewById(R.id.tv_device_info);
		mTvDeviceInfo.setText("device info"
				+"\ndensity:"
				+"\ndensityDpi:"
				+"\nwidthPixels:"
				+"\nheightPixels:"
				+"\nxdpi:"
				+"\nydpi:"
				+"\nsw dp:");
		mTvDeviceInfoValues = (TextView)findViewById(R.id.tv_device_info_values);
		mTvDeviceInfoValues.setText(""
				+"\n" + mDisplayMetrics.density
				+"\n" + mDisplayMetrics.densityDpi
				+"\n" + mDisplayMetrics.widthPixels
				+"\n" + mDisplayMetrics.heightPixels
				+"\n" + mDisplayMetrics.xdpi
				+"\n" + mDisplayMetrics.ydpi
				+"\n" + swdp);

		Log.i(TAG, "Build.MANUFACTURER: " + Build.MANUFACTURER);
		Log.i(TAG, "Build.MODEL: " + Build.MODEL);
		Log.i(TAG, ""
				+"\ndensity: " + mDisplayMetrics.density
				+"\ndensityDpi: " + mDisplayMetrics.densityDpi
				+"\nwidthPixels: " + mDisplayMetrics.widthPixels
				+"\nheightPixels: " + mDisplayMetrics.heightPixels
				+"\nxdpi: " + mDisplayMetrics.xdpi
				+"\nydpi: " + mDisplayMetrics.ydpi
				+"\nsw dp: " + swdp);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.multi_screen, menu);
		return true;
	}

}
