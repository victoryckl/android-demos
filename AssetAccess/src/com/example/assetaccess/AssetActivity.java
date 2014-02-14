package com.example.assetaccess;

import com.example.utils.AssetUtil;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

public class AssetActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_asset);
		
		findViewById(R.id.btn_copy).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AssetUtil.copyAssetDir(getAssets(), "test", "/sdcard/test-temp");
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.asset, menu);
		return true;
	}

}
