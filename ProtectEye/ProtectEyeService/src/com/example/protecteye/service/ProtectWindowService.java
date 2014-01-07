package com.example.protecteye.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.PluginState;
import android.widget.Button;
import android.widget.Toast;

import com.example.utils.Common;

@SuppressLint("NewApi")
public class ProtectWindowService extends Service {

	private boolean isAdded = false; // 是否已增加悬浮窗
	private static WindowManager wm;
	private static WindowManager.LayoutParams params;

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
			int operation = intent.getIntExtra(Common.OPERATION, Common.OPERATION_SHOW);
			switch (operation) {
			case Common.OPERATION_SHOW:
				showWindow();
				break;
			case Common.OPERATION_HIDE:
				hideWindow();
				break;
			}
		}
	}

	private void showWindow() {
		if (!isAdded) {
			wm.addView(mWindow, params);
			isAdded = true;
		}
	}
	
	private void hideWindow() {
		if (isAdded) {
			wm.removeView(mWindow);
			isAdded = false;
		}
	}

	private ViewGroup mWindow;
	/**
	 * 创建悬浮窗
	 */
	private void createFloatView() {
		mWindow = (ViewGroup) View.inflate(getApplicationContext(),
				R.layout.protect_window_layout, null);
		
		WebView webView = (WebView) mWindow.findViewById(R.id.wv_webview);
		webSettings(webView);
		
		Button btnClose = (Button) mWindow.findViewById(R.id.btn_close);
		btnClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideWindow();
			}
		});

		wm = (WindowManager) getApplicationContext().getSystemService(
				Context.WINDOW_SERVICE);
		params = new WindowManager.LayoutParams();

		// 设置window type
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
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

		wm.addView(mWindow, params);
		isAdded = true;
		webView.loadUrl("file:///android_asset/v.html");
	}
	
	private void webSettings(WebView view) {
		WebSettings s = view.getSettings();
		
		s.setJavaScriptEnabled(true);
		s.setPluginState(PluginState.ON);
		s.setTextZoom(100);
		
		view.setBackgroundColor(0x88ffff00);
	}
}
