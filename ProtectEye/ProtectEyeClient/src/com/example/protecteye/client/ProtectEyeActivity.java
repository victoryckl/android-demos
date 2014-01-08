package com.example.protecteye.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.utils.Common;
import com.example.utils.Common.Type;

public class ProtectEyeActivity extends Activity implements OnClickListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_protect_eye);
		
		init();
	}

	private void init() {
		findViewById(R.id.btn_show).setOnClickListener(this);
		findViewById(R.id.btn_hide).setOnClickListener(this);
	}

	public void onClick(View v) {
		Intent intent = new Intent();
		intent.setClassName("com.example.protecteye.service", 
				"com.example.protecteye.service.ProtectWindowService");
		switch (v.getId()) {
		case R.id.btn_show:
			intent.putExtra(Common.OPERATION, Common.Operation.SHOW);
			startService(intent);
			break;
		case R.id.btn_hide:
			intent.putExtra(Common.OPERATION, Common.Operation.HIDE);
			startService(intent);
			break;
		}
	}
}
