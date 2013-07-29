package com.ckl.zoompage;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ZoomControls;

//此程序已经提交到github.com
public class ZoomPage extends Activity implements Observer {
	private ImageZoomView mZoomView;
	private ZoomState mZoomState;
	private Bitmap mBitmap;
	private ZoomListener mZoomListener;
	
	private Button mSwitchBtn;
	private int mCurrentBmp = 1;
	private int[] mBmpResId = {
			R.drawable.mj,
			R.drawable.mj1,
			R.drawable.mj2,
			R.drawable.audi,
			R.drawable.audir8,
			R.drawable.bluck};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image);
		
		ZoomInit();
		
		//testPow();
		
		//addIcon();
		
		mZoomState.addObserver(this);
	}
	
	private Bitmap getBitmap(int index) {
		return BitmapFactory.decodeResource(getResources(), mBmpResId[index % mBmpResId.length]);
	}
	
	private void setBitmap(int index) {
		mBitmap = getBitmap(index);
		int w = mBitmap.getWidth();
		int h = mBitmap.getHeight();
		Log.i(TAG, "bmp w,h = " + w + "," + h);
		mSwitchBtn.setText("切换图片(" 
				+ getResources().getResourceEntryName(mBmpResId[index % mBmpResId.length])
				+ ": " + w + "/" + h + "=" + w/(float)h + ")");
		mZoomView.setImage(mBitmap);
	}

	protected void ZoomInit(){
		mSwitchBtn = (Button)findViewById(R.id.switchBtn);
		mSwitchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCurrentBmp++;
				setBitmap(mCurrentBmp);
			}
		});
		
		mZoomView = (ImageZoomView) findViewById(R.id.zoomView);
		
		setBitmap(mCurrentBmp);
		
		mZoomState = new ZoomState();
		mZoomView.setZoomState(mZoomState);
		
		mZoomListener = new ZoomListener();
		mZoomListener.setZoomState(mZoomState);
		mZoomView.setOnTouchListener(mZoomListener);
		
		ZoomControls zoomCtrl = (ZoomControls) findViewById(R.id.zoomCtrl);
		zoomCtrl.setOnZoomInClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				float z = mZoomState.getZoom() + 0.1f;
				zoomCtrlClick(z);
			}
		});
		zoomCtrl.setOnZoomOutClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				float z = mZoomState.getZoom() - 0.1f;
				zoomCtrlClick(z);
			}
		});
	}
	
	private void zoomCtrlClick(float z) {
		mZoomState.setZoom(z);
		mZoomState.notifyObservers();
		mZoomState.startReturnAnimation();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBitmap != null)
			mBitmap.recycle();
		// mZoomView.setOnTouchListener(null);
		// mZoomState.deleteObservers();
	}

	private final int mIconId = 123;
	private final int mImageX = 880, mImageY = 370, mImageW = 80, mImageH = 100;
	private ImageView addIcon() {
		ImageView icon = new ImageView(this);
		icon.setId(mIconId);
		icon.setImageResource(R.drawable.mj1);
		
		AbsoluteLayout.LayoutParams params = 
			new AbsoluteLayout.LayoutParams(
					(int)(mImageW * mZoomState.getZoomX()),
					(int)(mImageH * mZoomState.getZoomY()),
					(int)mZoomState.toScreenX(mImageX),
					(int)mZoomState.toScreenY(mImageY));
		
		AbsoluteLayout mylayout = (AbsoluteLayout)findViewById(R.id.mylayout);
		mylayout.addView(icon, params);
		
		return icon;
	}
	
	private void moveIcon() {
		int x, y, w, h;
		ImageView icon = (ImageView)findViewById(mIconId);
		if (icon == null) {
			addIcon();
		} else {
			x = (int)mZoomState.toScreenX(mImageX);
			y = (int)mZoomState.toScreenY(mImageY);
			w = (int)(mImageW * mZoomState.getZoomX());
			h = (int)(mImageH * mZoomState.getZoomY());
			icon.layout(x, y, x + w, y + h);	
		}
	}
	
	@Override
	public void update(Observable observable, Object data) {
		// TODO Auto-generated method stub
		logd("update");
		moveIcon();
	}
	
	private void testPow() {
		if (debugOn) {
			float dgap = -5;
			for (int i = 0; i < 20; i++) {
				logd("pow(5, " + dgap + ") = " + (float) Math.pow(5, dgap));
				dgap += 0.5f;
			}	
		}
	}
	
	//调试
	private final boolean debugOn = true;
	private final String TAG = "ZoomPage";
    private int logd(String msg) {
    	int retVal = 0;
    	if (debugOn) {
    		retVal = Log.i(TAG, msg);
		}
    	return retVal;
    }
}