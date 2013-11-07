package com.example.widget;

import com.example.android_demos.EditActivity;
import com.example.android_demos.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CombineEdit extends LinearLayout {

	private EditText mEditText;
	private ImageButton mBtnClear;

	public CombineEdit(Context context) {
		super(context);
		init(context);
	}

	public CombineEdit(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	@SuppressLint("NewApi")
	public CombineEdit(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		
//        android:background="@drawable/password_field_default"
//        android:gravity="center_vertical"
//        android:orientation="horizontal"
		
		setBackgroundResource(R.drawable.password_field_default);
		setGravity(Gravity.CENTER_VERTICAL);
		setOrientation(HORIZONTAL);
        
		LayoutInflater.from(context).inflate(R.layout.combine_edit, this);
		
		findViewById(R.id.combine_btn_search).setOnClickListener(mBtnOnClickListener);
		
		mEditText = (EditText) findViewById(R.id.combine_edit_search);
		mEditText.addTextChangedListener(mTextWatcher);
		
		mBtnClear = (ImageButton) findViewById(R.id.combine_btn_clear);
		mBtnClear.setOnClickListener(mBtnOnClickListener);
	}
	
	private OnClickListener mBtnOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.combine_btn_search:
				Toast.makeText(getContext(), "do nothing", Toast.LENGTH_SHORT).show();
				break;
			case R.id.combine_btn_clear:
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
