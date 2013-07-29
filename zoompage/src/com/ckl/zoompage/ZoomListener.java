package com.ckl.zoompage;

import java.util.ArrayList;

import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public class ZoomListener implements View.OnTouchListener, 
	OnGestureListener, OnDoubleTapListener {
	private ZoomState mState;
	
	private GestureDetector mGestureDetector;

	private float mX;
	private float mY;
	private float mGap;

	public ZoomListener() {
		mGestureDetector = new GestureDetector(null, this, null, true);
	}
	
	public void setZoomState(ZoomState state) {
		mState = state;
	}

	public boolean onTouch(View v, MotionEvent event) {
		final int action = event.getAction();
		int pointCount = event.getPointerCount();
		
		testPoint(event);
		
		if (mGestureDetector.onTouchEvent(event)) {
			return true;
		}
		
		if (pointCount == 1) {
			final float x = event.getX();
			final float y = event.getY();
			
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				logd("------> 1.ACTION_DOWN");
				mX = x;
				mY = y;
				//测试坐标转换
				testConvertXY(x, y);
				break;
			case MotionEvent.ACTION_MOVE:
//				logd("------> 1.ACTION_MOVE");
				
			 	float dx = (x - mX) / mState.getBitmapScaleWidth();
			 	float dy = (y - mY) / mState.getBitmapScaleHeight();
				
				mState.setPanX(mState.getPanX() - dx);
				mState.setPanY(mState.getPanY() - dy);
				mState.notifyObservers();
				mX = x;
				mY = y;
				break;
			case MotionEvent.ACTION_UP:
				logd("------> 1.ACTION_UP");
				mState.startReturnAnimation();
				break;
			}
		}
		if (pointCount > 1) {
			final float x0 = event.getX(event.getPointerId(0));
			final float y0 = event.getY(event.getPointerId(0));

			final float x1 = event.getX(event.getPointerId(1));
			final float y1 = event.getY(event.getPointerId(1));

			float gap = getGap(x0, x1, y0, y1);
			switch (action) {
			case MotionEvent.ACTION_POINTER_2_DOWN:
			case MotionEvent.ACTION_POINTER_1_DOWN:
				if (action == MotionEvent.ACTION_POINTER_1_DOWN) {
					logd("------> 2.ACTION_POINTER_1_DOWN");
				} else {
					logd("------> 2.ACTION_POINTER_2_DOWN");
				}
				mGap = gap;
				break;
			case MotionEvent.ACTION_POINTER_1_UP:
				logd("------> 2.ACTION_POINTER_1_UP");
				mX = x1;
				mY = y1;
				break;
			case MotionEvent.ACTION_POINTER_2_UP:
				logd("------> 2.ACTION_POINTER_2_UP");
				mX = x0;
				mY = y0;
				break;
			case MotionEvent.ACTION_MOVE:
//				logd("------> 2.ACTION_MOVE");
				if (Math.abs(mGap) > 1 && Math.abs(gap) > 1) {
					float dgap = (gap - mGap) / mGap;
			    	if (dgap != 0)  {
			    		if (Math.abs(dgap) > 0.1) {
				    		logd("----------------------------------------");
							logd("x0,y0 = " + x0 + "," + y0);
							logd("x1,y1 = " + x1 + "," + y1);
					    	logd("mGap = " + mGap);
					    	logd("gap = " + gap);
					    	logd("dgap = " + dgap);
					    	
					    	//缩放改变太大，这里限制下，修正为较小的改变，避免图片忽大忽小的快闪
					    	dgap = 0.1f * Math.abs(dgap) / dgap;
					    	gap = mGap * (1 + dgap);
					    	
					    	logd("pow(5, " + dgap + ") = " + (float) Math.pow(5, dgap));
						}
			    		float z = mState.getZoom() * (float) Math.pow(5, dgap);
						mState.setZoom(z);
						mState.notifyObservers();
						mGap = gap;
						logd("mZoom = " + mState.getZoom());
			    	}			
				}
				break;
			}
		}

		return true;
	}

	private float getGap(float x0, float x1, float y0, float y1) {
		return (float) Math.hypot(x0 - x1, y0 - y1);
	}

	//测试坐标转换
	private void testConvertXY(float x, float y) {
		if (debugOn && false) {
			logd("------------------------------");
			mState.toScreenX(mState.toImageX(x));
			mState.toScreenY(mState.toImageY(y));
		}
	}
	
	//调试
	private final boolean debugOn = false;
	private final String TAG = "ZoomListener";
    private int logd(String msg) {
    	int retVal = 0;
    	if (debugOn) {
    		retVal = Log.i(TAG, msg);
		}
    	return retVal;
    }
    
    private void testPoint(MotionEvent event) {
//    	if (!debugOn || true)
//    		return;
    	
        ArrayList<Float> mXs = null;
        ArrayList<Float> mYs = null;
		if (mXs == null && mYs == null) {
            mXs = new ArrayList<Float>();
            mYs = new ArrayList<Float>();
		}
		mXs.clear();
		mYs.clear();
		
    	final int N = event.getPointerCount();
		float x, y;
    	for (int i = 0; i < N; i++) {
    		x = event.getX(event.getPointerId(i));
    		y = event.getY(event.getPointerId(i));
    		//logd("x[" +i+ "],y[" +i+ "] = " + x + "," + y);
    		mXs.add(x);
    		mYs.add(y);
		}
    	if (N > 0)
    		mState.setPoints(mXs, mYs);
    }

	@Override
	public boolean onDown(MotionEvent e) {
		logd("onDown");
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		logd("onShowPress");
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		logd("onSingleTapUp");
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		logd("onScroll");
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		logd("onLongPress");
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		logd("onFling");
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		logd("onSingleTapConfirmed");
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		logd("onDoubleTap");
		mState.doubleClick();
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		logd("onDoubleTapEvent");
		return false;
	}
}
