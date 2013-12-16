package com.example.multiscreen;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
		
		mTvDeviceInfo = (TextView)findViewById(R.id.tv_device_info);
		mTvDeviceInfo.setText("device info"
				+"\ndensity:"
				+"\ndensityDpi:"
				+"\nwidthPixels:"
				+"\nheightPixels:"
				+"\nxdpi:"
				+"\nydpi:");
		mTvDeviceInfoValues = (TextView)findViewById(R.id.tv_device_info_values);
		mTvDeviceInfoValues.setText(""
				+"\n" + mDisplayMetrics.density
				+"\n" + mDisplayMetrics.densityDpi
				+"\n" + mDisplayMetrics.widthPixels
				+"\n" + mDisplayMetrics.heightPixels
				+"\n" + mDisplayMetrics.xdpi
				+"\n" + mDisplayMetrics.ydpi );
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.multi_screen, menu);
		return true;
	}

}
