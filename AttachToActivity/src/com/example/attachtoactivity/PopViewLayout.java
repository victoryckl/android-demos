package com.example.attachtoactivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PopViewLayout extends RelativeLayout {

	private static final String TAG = PopViewLayout.class.getSimpleName();

	public static PopViewLayout create(Context context) {
		PopViewLayout p = (PopViewLayout) LayoutInflater.from(context)
				.inflate(R.layout.popview_layout, null);
		return p;
	}
	
	public PopViewLayout(Context context) {
		super(context);
	}
	
	public PopViewLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PopViewLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Log.i(TAG, "PopViewAbsolute()");
	}

	public void attachToActivity(Activity activity) {
		ViewGroup content = (ViewGroup) activity.findViewById(android.R.id.content);
		
		ViewGroup parent = null;
		
//		parent = (ViewGroup) content.getParent();
		
		parent = (ViewGroup)activity.getWindow().getDecorView();
		
		parent.addView(this);
	}
}
