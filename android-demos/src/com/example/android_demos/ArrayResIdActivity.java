package com.example.android_demos;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;

public class ArrayResIdActivity extends Activity {
	private TextView mTvDrawable, mTvString, mTvColor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_resid_activity);
		
		init();
	}
	private void init() {
		mTvDrawable = (TextView)findViewById(R.id.tv_drawable);
		mTvString = (TextView)findViewById(R.id.tv_string);
		mTvColor = (TextView)findViewById(R.id.tv_color);
		
		mTvDrawable.setText(getDrawable());
		mTvString.setText(getString());
		mTvColor.setText(getColor());
	}
	
	private String getDrawable() {
		String text = "drawabe:\n";
		Resources res = getResources();
		TypedArray array = res.obtainTypedArray(R.array.array_drawable);
		try {
			for (int i = 0; i < array.length(); i++) {
				Drawable d = array.getDrawable(i);
				int id = array.getResourceId(i, 0);
				text += "0x"+Integer.toHexString(id) + ", " + d +"\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			array.recycle();
		}
		return text;
	}
	
	private String getString() {
		String text = "string:\n";
		Resources res = getResources();
		TypedArray array = res.obtainTypedArray(R.array.array_string);
		try {
			for (int i = 0; i < array.length(); i++) {
				String s = array.getString(i);
				int id = array.getResourceId(i, 0);
				text += "0x"+Integer.toHexString(id) + ", " + s +"\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			array.recycle();
		}
		return text;
	}
	
	private String getColor() {
		String text = "color:\n";
		Resources res = getResources();
		TypedArray array = res.obtainTypedArray(R.array.array_color);
		try {
			for (int i = 0; i < array.length(); i++) {
				int s = array.getColor(i, 0);
				int id = array.getResourceId(i, 0);
				text += "0x"+Integer.toHexString(id) + ", " + Integer.toHexString(s) + "\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			array.recycle();
		}
		return text;
	}
}
