package com.brandontate.androidwebviewselection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
		
		findViewById(R.id.btn_bt_webview).setOnClickListener(this);
		findViewById(R.id.btn_webview_copy).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.btn_bt_webview:
			intent.setClass(this, BTAndroidWebViewSelectionActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_webview_copy:
			intent.setClass(this, WebViewCopyActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
}
