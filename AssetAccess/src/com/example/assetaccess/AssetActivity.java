package com.example.assetaccess;

import com.example.utils.AssetUtils;
import com.example.utils.RawUtils;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

public class AssetActivity extends Activity {
	private String SDCARD = Environment.getExternalStorageDirectory().toString();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_asset);
		
		findViewById(R.id.btn_copy).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AssetUtils.copyDir(getAssets(), "test", SDCARD+"/temp/assets");
			}
		});
		
		findViewById(R.id.btn_copy_raw).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				RawUtils.copyFile(getResources(), R.raw.air, SDCARD+"/temp/raw/air.apk");
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.asset, menu);
		return true;
	}

}
