package com.example.android_demos;

import com.example.widgets.CombineEdit;
import com.example.widgets.CombineEdit.CombineEventListener;

import android.R.menu;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class EditActivity extends Activity {
	private ImageButton mBtnClear;
	private EditText mEditText;
	private CombineEdit mCombineEdit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_edit_activity);
		init();
	}
	
	private void init() {
		findViewById(R.id.btn_search).setOnClickListener(mBtnOnClickListener);
		mBtnClear = (ImageButton)findViewById(R.id.btn_clear);
		mBtnClear.setOnClickListener(mBtnOnClickListener);
		
		mEditText = (EditText)findViewById(R.id.edit_search);
		mEditText.addTextChangedListener(mTextWatcher);
		
		mCombineEdit = (CombineEdit) findViewById(R.id.combine_edit);
		mCombineEdit.setCombineEventListener(new CombineEventListener() {
			@Override
			public boolean onSearchClick(EditText e) {
				Toast.makeText(getApplicationContext(), "searching..."+e.getText(), Toast.LENGTH_SHORT).show();
				return true;
			}
			@Override
			public boolean onClearClick(EditText e) {
				return false;
			}
		});
	}
	
	private OnClickListener mBtnOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_search:
				Toast.makeText(EditActivity.this, "do nothing", Toast.LENGTH_SHORT).show();
				break;
			case R.id.btn_clear:
				mEditText.setText("");
				break;
			default:
				break;
			}
		}
	};
	
	private TextWatcher mTextWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			String text = mEditText.getText().toString();
			if (text == null || text.equals("")) {
				mBtnClear.setVisibility(View.INVISIBLE);
			} else {
				mBtnClear.setVisibility(View.VISIBLE);
			}
		}
	};
}
