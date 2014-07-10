package com.example.exceldemo;

import com.example.utils.AssetUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {
	private static final String xmlPath = "/sdcard/test.xls";
	private static final String pngPath = "/sdcard/nb.png";
	
	private ExcelUtils excelUtils;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		AssetUtils.copyFile(getAssets(), "test.xls", xmlPath);
		AssetUtils.copyFile(getAssets(), "nb.png", pngPath);
		
		excelUtils = new ExcelUtils();
		
		findViewById(R.id.btn_read_excel).setOnClickListener(mClickListener);
		findViewById(R.id.btn_write_excel).setOnClickListener(mClickListener);
	}
	
	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_read_excel:
				excelUtils.readExcel(xmlPath);
				break;
			case R.id.btn_write_excel:
				break;
			default:
				break;
			}
		}
	};
}
