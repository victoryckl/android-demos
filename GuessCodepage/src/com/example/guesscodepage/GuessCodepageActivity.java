package com.example.guesscodepage;

import java.nio.charset.Charset;

import com.example.utils.GuessCodepage;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class GuessCodepageActivity extends Activity {

	private static final int FILE_SELECT_CODE = 0;

	private static final String TAG = GuessCodepageActivity.class.getSimpleName();
	
	private EditText mEditFilePath;
	private TextView mTextCodepage, mTextDefault, mTextSupport;

	private String mStrCodepage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guess_codepage);
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.guess_codepage, menu);
		return true;
	}

	private void init() {
		mEditFilePath = (EditText)findViewById(R.id.x_edit_path);
		findViewById(R.id.x_btn_choose_file).setOnClickListener(mBtnClickListener);
		findViewById(R.id.x_btn_guess_codepage).setOnClickListener(mBtnClickListener);
		mTextCodepage = (TextView)findViewById(R.id.x_tv_codepage);
		
		mStrCodepage = getResources().getString(R.string.x_str_codepage);
		mTextDefault = (TextView)findViewById(R.id.x_tv_default_codepage);
		mTextSupport = (TextView)findViewById(R.id.x_tv_support_codepage);
		
		mTextDefault.setText(
				getResources().getString(R.string.x_str_default_codepage) 
				+ Charset.defaultCharset());
		
		mTextSupport.setText(
				getResources().getString(R.string.x_str_support_codepage) 
				+  Charset.availableCharsets().keySet());
	}
	
	private OnClickListener mBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.x_btn_choose_file:
				chooseFile();
				break;
			case R.id.x_btn_guess_codepage:
				guess();
				break;
			default:
				break;
			}
		}
	};
	
	private void chooseFile() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try {
			String str = (String)getResources().getString(R.string.x_str_select_file);
			startActivityForResult(Intent.createChooser(intent, str), FILE_SELECT_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(this, R.string.x_str_no_file_browser, Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode != Activity.RESULT_OK) {
			Log.e(TAG, "onActivityResult() error, resultCode: " + resultCode);
			super.onActivityResult(requestCode, resultCode, data);
			return;
		}
		if (requestCode == FILE_SELECT_CODE) {
			Uri uri = data.getData();
			Log.i(TAG, "------->" + uri.getPath());
			mEditFilePath.setText(uri.getPath());
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private String getPath() {
		String path = null;

		path = mEditFilePath.getText().toString();
		if (path == null || path.length() <= 0) {
			Toast.makeText(this, R.string.x_str_no_path, Toast.LENGTH_SHORT).show();
			path = null;
		} else {
			Log.i(TAG, "getPath(): " + path);
		}
		
		return path;
	}
	
	private void guess() {
		String path = getPath();
		
		if (path != null) {
			String code = GuessCodepage.getFileEncode(path);
			if (code != null) {
				mTextCodepage.setText(mStrCodepage+code);
				return ;
			}
		}
		mTextCodepage.setText(R.string.x_str_unknow_codepage);
		Toast.makeText(this, R.string.x_str_unknow_codepage, Toast.LENGTH_SHORT).show();
	}
}
