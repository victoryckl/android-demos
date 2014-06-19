package com.svo.platform.picbrowser;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class PicViewPager extends ViewPager {

	public PicViewPager(Context context) {
		super(context);
	}

	public PicViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		try {
			return super.onInterceptTouchEvent(ev);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}