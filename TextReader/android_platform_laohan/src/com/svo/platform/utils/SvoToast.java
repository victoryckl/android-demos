package com.svo.platform.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast封装类，解决了多个Toast在页面长时间显示的问题
 * @author 杜卫宾（duweibn@foxmail.com）
 *
 */
public class SvoToast {
	private static Toast toast;

	public static void showHint(Context context, CharSequence text, int duration) {
		if (toast == null) {
			toast = Toast.makeText(context, text, duration);
		} else {
			toast.setText(text);
		}
		toast.show();
	}
	public static void showHint(Context context, int resId, int duration) {
		showHint(context, context.getResources().getString(resId), duration);
	}
}
