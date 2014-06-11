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
		
		// Ä¬ÈÏÈí¼üÅÌ²»µ¯³ö 
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		FrameLayout layout = (FrameLayout) findViewById(android.R.id.content);
		LinearLayout linearLayout = (LinearLayout) View.inflate(this, R.layout.activity_main, null);
		layout.addView(linearLayout);
		
		findViewById(R.id.btn_close).setOnClickListener(mClickListener);
	}
	
	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_close:
				MainActivity.this.finish();
				Process.killProcess(Process.myPid());
				break;
			default:
				break;
			}
		}
	};
}
