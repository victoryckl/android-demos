package com.example.protecteye.service;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.VideoView;

import com.example.utils.Common;
import com.example.utils.PathManager;

@SuppressLint("NewApi")
public class ProtectWindowService extends Service {

	private static final String TAG = ProtectWindowService.class.getSimpleName();
	private boolean isAdded = false;
	private static WindowManager wm;
	private static WindowManager.LayoutParams params;
	
	private ViewGroup mWindow;
	private VideoView mVideoView;
	private String mType;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		createFloatView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (intent != null) {
			String operation = intent.getStringExtra(Common.OPERATION);
			mType = intent.getStringExtra(Common.TYPE);
			if (mType == null || mType.isEmpty()) {
				mType = Common.Type.TYPE2;
			}
			if (operation.equals(Common.Operation.SHOW)) {
				showWindow(mType);
			}
			else if (operation.equals(Common.Operation.HIDE)) {
				hideWindow();
			}
		}
	}

	private void showWindow(String type) {
		if (!isAdded) {
			if (play(type)) {
				wm.addView(mWindow, params);
				isAdded = true;
			} else {
				hideWindow();
			}
		}
	}
	
	private void hideWindow() {
		if (isAdded) {
			stop();
			wm.removeView(mWindow);
			isAdded = false;
		}
	}

	private boolean play(String type) {
		boolean ret = false;
		String path = PathManager.getRawPath(getApplicationContext(), type);
		if (path == null) {
			return ret;
		}
		Log.i(TAG, "path: "+ path);
		File f = new File(path);
//		if (f.exists()) {//assets file
			Uri uri = Uri.parse(path);
			mVideoView.setVideoURI(uri);  
			mVideoView.start();  
			mVideoView.requestFocus();
			ret = true;//OK
//		}
		return ret;
	}
	
	private void stop() {
		mVideoView.stopPlayback();
	}
	
	private void createFloatView() {
		mWindow = (ViewGroup) View.inflate(getApplicationContext(),
				R.layout.protect_window_layout, null);
		
		initParams();
		initVideoView();
		
		Button btnClose = (Button) mWindow.findViewById(R.id.btn_close);
		btnClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideWindow();
			}
		});
	}
	
	private void initParams() {
		wm = (WindowManager) getApplicationContext().getSystemService(
				Context.WINDOW_SERVICE);
		params = new WindowManager.LayoutParams();

		// 设置window type
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
//		params.type = WindowManager.LayoutParams.TYPE_KEYGUARD;
//		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		/*
		 * 如果设置为params.type = WindowManager.LayoutParams.TYPE_PHONE; 那么优先级会降低一些,
		 * 即拉下通知栏不可见
		 */

		params.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明

		// 设置Window flag
		params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		/*
		 * 下面的flags属性的效果形同“锁定”。 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
		 * wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL |
		 * LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCHABLE;
		 */

		// 设置悬浮窗的长得宽
		params.width = ViewGroup.LayoutParams.MATCH_PARENT;
		params.height = ViewGroup.LayoutParams.MATCH_PARENT;
	}

	private void initVideoView() {
		mVideoView = (VideoView)mWindow.findViewById(R.id.vv_videoview);
		mVideoView.setOnCompletionListener(mVideoViewListener);
		mVideoView.setOnErrorListener(mVideoViewListener);
	}
	
	private VideoViewListener mVideoViewListener = new VideoViewListener();
	private class VideoViewListener 
		implements OnCompletionListener, OnErrorListener {
		
		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			Log.e(TAG, "VideoView, onError(), what:"+what+". extra:"+extra);
			hideWindow();
			return true;
		}

		@Override
		public void onCompletion(MediaPlayer mp) {
			play(mType);
		}
	}
}
