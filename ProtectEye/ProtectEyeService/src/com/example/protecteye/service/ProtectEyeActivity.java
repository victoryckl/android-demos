package com.example.protecteye.service;

import com.example.utils.Common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ProtectEyeActivity extends Activity implements OnClickListener {
	
	private Button btn_show;
	private Button btn_hide;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_protect_eye);
		
		init();
	}

	private void init() {
		btn_show = (Button) findViewById(R.id.btn_show);
		btn_hide = (Button) findViewById(R.id.btn_hide);
		btn_show.setOnClickListener(this);
		btn_hide.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.protect_eye, menu);
		return true;
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_show:
			Intent show = new Intent(this, ProtectWindowService.class);
			show.putExtra(Common.OPERATION, Common.OPERATION_SHOW);
			startService(show);
			break;
		case R.id.btn_hide:
			Intent hide = new Intent(this, ProtectWindowService.class);
			hide.putExtra(Common.OPERATION, Common.OPERATION_HIDE);
			startService(hide);
			break;
		}
	}
}
