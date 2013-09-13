package com.example.android_demos;

import com.example.utils.SToast;
import com.example.utils.StackTrace;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class AndroidDemos extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_android_demos);
		
		SToast.show(this, "OnCreate()");
		StackTrace.printStackTrace();
		
		new Thread(){
			public void run() {
				StackTrace.printStackTrace();
			};
		}.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.android_demos, menu);
		SToast.show(this, "onCreateOptionsMenu()");
		return true;
	}

}
