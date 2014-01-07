package com.example.protecteye.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.utils.Common;

public class ProtectWindowService extends Service {

	private boolean isAdded = false; // 是否已增加悬浮窗
	private static WindowManager wm;
	private static WindowManager.LayoutParams params;
	private Button btn_floatView;

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
			wm.addView(btn_floatView, params);
			isAdded = true;
		}
	}
	
	private void hideWindow() {
		if (isAdded) {
			wm.removeView(btn_floatView);
			isAdded = false;
		}
	}

	/**
	 * 创建悬浮窗
	 */
	private void createFloatView() {
		btn_floatView = new Button(getApplicationContext());
		btn_floatView.setText("$_$");
		btn_floatView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

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
		params.width = getResources().getDimensionPixelSize(R.dimen.float_width);
		params.height = getResources().getDimensionPixelSize(R.dimen.float_height);

		// 设置悬浮窗的Touch监听
		btn_floatView.setOnTouchListener(new OnTouchListener() {
			int lastX, lastY;
			int paramX, paramY;

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					lastX = (int) event.getRawX();
					lastY = (int) event.getRawY();
					paramX = params.x;
					paramY = params.y;
					break;
				case MotionEvent.ACTION_MOVE:
					int dx = (int) event.getRawX() - lastX;
					int dy = (int) event.getRawY() - lastY;
					params.x = paramX + dx;
					params.y = paramY + dy;
					// 更新悬浮窗位置
					wm.updateViewLayout(btn_floatView, params);
					break;
				case MotionEvent.ACTION_UP:
					Toast.makeText(getApplicationContext(), "touch up !!", Toast.LENGTH_SHORT).show();
					break;
				}
				return true;
			}
		});
		
		wm.addView(btn_floatView, params);
		isAdded = true;
	}
}
