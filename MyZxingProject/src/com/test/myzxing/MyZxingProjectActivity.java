package com.test.myzxing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.zxing.client.android.ViewfinderView;

public class MyZxingProjectActivity extends Activity {
	/** Called when the activity is first created. */
	private Button mButtonPortrait;
	private Button mButtonLandScape;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mButtonPortrait = (Button) findViewById(R.id.buttonPortrit);
		mButtonPortrait.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.setClass(MyZxingProjectActivity.this,
						CaptureActivityPortrait.class);
				startActivity(i);
			}
		});
		
		
		
		
	}
}