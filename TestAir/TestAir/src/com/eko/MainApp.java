package com.eko;

import java.io.File;

import air.app.AppEntry;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainApp extends AppEntry {
	@Override
	public void onCreate(Bundle arg0) {

		super.onCreate(arg0);
		FrameLayout root = (FrameLayout) findViewById(android.R.id.content);
		TextView text = new TextView(this);
		text.setText("game over");
		root.addView(text);
	}

	@Override
	public void showDialog() {
		// 当机器上没有装air的时候执行的函数
		new AlertDialog.Builder(this).setTitle("提醒").setMessage("安装完Air后请点击确定")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						// 重新启动程序
						AlarmManager mgr = (AlarmManager) MainApp.super
								.getSystemService(Context.ALARM_SERVICE);
						mgr.set(AlarmManager.RTC,
								System.currentTimeMillis() + 500, PendingIntent
										.getActivity(
												MainApp.super.getBaseContext(),
												0, new Intent(getIntent()),
												getIntent().getFlags()));
						System.exit(2);
					}
				}).show();
		try {
			// 将air.apk文件拷贝到sdcard中
			File sd = Environment.getExternalStorageDirectory();
			String path = sd.toString()+"/.eko/air.apk";
			boolean ok = RawUtils.copyFile(getResources(), R.raw.air, path);
			if (ok) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				Uri uri = Uri.fromFile(new File(path));
				intent.setDataAndType(uri,
						"application/vnd.android.package-archive");
				// 执行air.apk进行安装
				startActivity(intent);
			}
		} catch (Exception e) {
			// TODO: handle exception 1
		}
	}
}