package com.example.protecteye.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.utils.Common;

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

	public void onClick(View v) {
		Intent intent = new Intent();
		intent.setClassName("com.example.protecteye.service", 
				"com.example.protecteye.service.ProtectWindowService");
		switch (v.getId()) {
		case R.id.btn_show:
			intent.putExtra(Common.OPERATION, Common.OPERATION_SHOW);
			startService(intent);
			break;
		case R.id.btn_hide:
			intent.putExtra(Common.OPERATION, Common.OPERATION_HIDE);
			startService(intent);
			break;
		}
	}
}
