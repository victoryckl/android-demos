package com.example.android_demos;

import com.example.utils.SToast;
import com.example.utils.StackTrace;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

public class AndroidDemos extends Activity {
	private final static String TAG = AndroidDemos.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_android_demos);
		
//		SToast.show(this, "OnCreate()");
//		StackTrace.printStackTrace();
//		
//		new Thread(){
//			public void run() {
//				StackTrace.printStackTrace();
//			};
//		}.start();
		
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.android_demos, menu);
//		SToast.show(this, "onCreateOptionsMenu()");
		return true;
	}

	private void init() {
		findViewById(R.id.btn_go_to_edit).setOnClickListener(mBtnClickListener);
		findViewById(R.id.btn_go_to_imagebutton).setOnClickListener(mBtnClickListener);
		findViewById(R.id.btn_go_to_title).setOnClickListener(mBtnClickListener);
		findViewById(R.id.btn_go_to_get_res).setOnClickListener(mBtnClickListener);
	}
	
	private OnClickListener mBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			switch (v.getId()) {
			case R.id.btn_go_to_edit:
				intent.setClass(AndroidDemos.this, EditActivity.class);
				startActivity(intent);
				break;
			case R.id.btn_go_to_imagebutton:
				break;
			case R.id.btn_go_to_title:
				break;
			case R.id.btn_go_to_get_res:
				intent.setClass(AndroidDemos.this, ArrayResIdActivity.class);
				startActivity(intent);
				break;
			default:
				break;
			}
		}
	};
}
