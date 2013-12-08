package com.example.widgets;

import com.example.android_demos.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

public class TextImageButton extends FrameLayout {

	public TextImageButton(Context context) {
		super(context);
		init(context);
	}

	public TextImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TextImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.text_image_button, this);
	}
}
