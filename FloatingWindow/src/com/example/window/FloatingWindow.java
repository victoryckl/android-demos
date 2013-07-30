package com.example.window;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FloatingWindow extends Activity implements OnClickListener {

	private Button btn_show;
	private Button btn_hide;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		btn_show = (Button) findViewById(R.id.btn_show);
		btn_hide = (Button) findViewById(R.id.btn_hide);
		btn_show.setOnClickListener(this);
		btn_hide.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_show:
			Intent show = new Intent(this, FloatingWindowService.class);
			show.putExtra(FloatingWindowService.OPERATION,
					FloatingWindowService.OPERATION_SHOW);
			startService(show);
			break;
		case R.id.btn_hide:
			Intent hide = new Intent(this, FloatingWindowService.class);
			hide.putExtra(FloatingWindowService.OPERATION,
					FloatingWindowService.OPERATION_HIDE);
			startService(hide);
			break;
		}
	}

}