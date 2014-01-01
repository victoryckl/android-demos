package com.example.utils;

import javax.crypto.Mac;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.OrientationEventListener;

/**
 * -->手机当前是竖屏状态,Activity也是竖屏状态 ,  <br/>
 * -->用户点击切换按钮      <br/>
 * -->Activity切换为横屏,手机为竖屏;此时通过设置flag,使OrientationListener监听到竖屏时不再处理事件,waiting...    <br/>
 * -->直到当用户把手机旋转为横屏状态之后,更改flag,使OrientationListener监听到竖屏时处理相应的事件     <br/>
 * -->当用户再次旋转手机切换为竖屏之后,Activity即可自动切换为竖屏;  <br/>
 * 横屏点击切换竖屏理论同上;  <br/>
 */
public class RotateManager {
	private static final String TAG = RotateManager.class.getSimpleName();
	
	private Activity mActivity;
	
	private State mState = State.Default;
	private enum State {
		Default,
		/** 外部请求 */
		AskForLand, 
		AskForPort;
	}
	
	public RotateManager(Activity activity) {
		mActivity = activity;
		startListener();
	}
	
	public void landscape() {
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		mState = State.AskForLand;
	}
	
	public void portrait() {
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		mState = State.AskForPort;
	}
	
	// 四个方向
	private static final int[] orientations = new int[] {
			ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
			ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE,
			ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT,
			ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE };
	
	private class RotateListener extends OrientationEventListener {
		
		public RotateListener(Context context) {
			super(context);
		}

		@Override
		public void onOrientationChanged(int rotation) {
			Log.i(TAG, "rotation: " + rotation);
			// 水平放置   
			if (rotation == -1) {
				return;
			}

			// rotation[0,360)
			// 30~60度不响应   
			int r = rotation % 90;
			if (r > 30 && r < 60) {
				return;
			}

			// 为方便计算数组索引，增加30  
			r = rotation + 30;
			r %= 360;
			int idx = r / 90;
			int orientation = orientations[idx];
			//机器旋转到横屏的方向        
			if ((idx & 1) == 1) {
				if (mState == State.AskForPort) {
					return;
				} else {
					mState = State.Default;
					mActivity.setRequestedOrientation(orientation);
				}
			}
			//机器旋转到竖屏的方向        
			else {    
				if (mState == State.AskForLand) {
					return;
				} else {
					mState = State.Default;
					mActivity.setRequestedOrientation(orientation);
				}
			}
		}
	}

	private RotateListener mRotateListener;
	private void startListener() {
		mRotateListener = new RotateListener(mActivity);
		mRotateListener.enable();
    }
	
	public void destroy() {
		mState = State.Default;
		mRotateListener.disable();
		mRotateListener = null;
		mActivity = null;
	}
}
