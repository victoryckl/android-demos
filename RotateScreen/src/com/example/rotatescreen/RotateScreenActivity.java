package com.example.rotatescreen;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.utils.RotateManager;
import com.example.utils.RotateManager.OnChangeListener;
import com.example.utils.SToast;

public class RotateScreenActivity extends Activity {

	private static final String TAG = RotateScreenActivity.class.getSimpleName();
	private RotateManager mRotateMgr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rotate_screen);
		init();
	}

    private TextView mTvInfo;
    
    
    private void init() {
    	mTvInfo = (TextView) findViewById(R.id.tv_info);
    	findViewById(R.id.btn_rotate).setOnClickListener(mBtnClickListener);
    	
    	mRotateMgr = new RotateManager(this);
    	mRotateMgr.setOnChangeListener(mChangeListener);
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//    	super.onConfigurationChanged(newConfig);
//    }
    
    private OnClickListener mBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int orientation = getRequestedOrientation();
			if (orientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE 
			 &&	orientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE 
			 && orientation != ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
				mRotateMgr.landscape();
				showToast("click to landscape");
			} else {
				mRotateMgr.portrait();
				showToast("click to portrait");
			}
		}
    };
    
    private OnChangeListener mChangeListener = new OnChangeListener() {
		@Override
		public void onChange(int orientation) {
			if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
			 || orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
			 || orientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
				showToast("rotate to landscape");
			} else {
				showToast("rotate to portrait");
			}
		}
	};
	
	private void showToast(String text) {
		SToast.show(getApplicationContext(), text);
	}
    
	@Override
	protected void onResume() {
		mRotateMgr.resume();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		mRotateMgr.pause();
		super.onPause();
	}
	
    @Override
    protected void onDestroy() {
    	mRotateMgr.destroy();
    	mRotateMgr = null;
    	super.onDestroy();
    };
}
