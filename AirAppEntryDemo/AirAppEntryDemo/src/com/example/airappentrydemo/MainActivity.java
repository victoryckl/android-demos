package com.example.airappentrydemo;

import air.com.adobe.appentry.AppEntry;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class MainActivity extends AppEntry {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		FrameLayout frameLayout = (FrameLayout) findViewById(android.R.id.content);
		LinearLayout linearLayout = (LinearLayout) View.inflate(getApplicationContext(), 
				R.layout.activity_layout, null);
		linearLayout.findViewById(R.id.btn_close).setOnClickListener(mClickListener);
		frameLayout.addView(linearLayout);
	}
	
	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_close:
				MainActivity.this.finish();
				break;

			default:
				break;
			}
		}
	};
	
	@Override
	public void finish() {
		super.finish();
		Process.killProcess(Process.myPid());
	};
}
