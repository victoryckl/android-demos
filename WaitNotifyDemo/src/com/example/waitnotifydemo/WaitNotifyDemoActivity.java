package com.example.waitnotifydemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class WaitNotifyDemoActivity extends Activity {
	private static final String TAG = WaitNotifyDemoActivity.class.getSimpleName();

	private Thread thread1, thread2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wait_notify_demo);
		
		init();
	}
	
	private void init() {
		findViewById(R.id.btn_start).setOnClickListener(mClickListener);
		findViewById(R.id.btn_call_listener).setOnClickListener(mClickListener);
	}
	
	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_start:
				Log.i(TAG, "btn_start");
				thread1 = new Thread1();
				thread1.start();
				break;
			case R.id.btn_call_listener:
				break;
			default:
				break;
			}
		}
	};
	
	private Object lock = new Object();
	class Thread1 extends Thread {
		@Override
		public void run() {
			Log.i(TAG, "Thread 1 run 1");
			sleep1(2000);
			thread2 = new Thread2();
			thread2.start();
			synchronized (lock) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Log.i(TAG, "Thread 1 run 2");
				sleep1(2000);
				lock.notify();
			}
			Log.i(TAG, "Thread 1 run 3");
		}
	}
	
	class Thread2 extends Thread {
		@Override
		public void run() {
			Log.i(TAG, "Thread 2 run 1");
			sleep1(2000);
			synchronized (lock) {
				lock.notify();
				Log.i(TAG, "Thread 2 run 2");
				try {
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				sleep1(2000);
			}
			
			Log.i(TAG, "Thread 2 run 3");
		}
	}
	
	private void sleep1(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
