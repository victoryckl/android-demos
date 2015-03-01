package com.brandontate.androidwebviewselection;

import java.lang.reflect.Method;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.webkit.WebView;

public class WebViewCopy {
	private Activity activity;
	private WebView webview;
	private static boolean mIsSelectingText;

	public static final String TAG = WebViewCopy.class.getSimpleName();

	public WebViewCopy(final Activity activity, final WebView webView) {
		this.webview = webView;
		this.activity = activity;
		this.activity.registerForContextMenu(this.webview);
		webView.requestFocus(View.FOCUS_DOWN);
		webView.setOnTouchListener(new OnTouchListener() {

			boolean mHasPerformedLongPress;
			Runnable mPendingCheckForLongPress;

			@Override
			public boolean onTouch(final View v, MotionEvent event) {
				/*
				 * webView.getSettings().setBuiltInZoomControls(false);
				 * webView.getSettings().setSupportZoom(false);
				 */
				Log.d(TAG, "event:" + event.getAction());

				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					mIsSelectingText = false;
					if (!v.hasFocus()) {
						v.requestFocus();
					}
					if (!mHasPerformedLongPress) {
						// This is a tap, so remove the longpress check
						if (mPendingCheckForLongPress != null) {
							v.removeCallbacks(mPendingCheckForLongPress);
						}
						// v.performClick();
					}
					break;
				case MotionEvent.ACTION_DOWN:
					if (!v.hasFocus()) {
						v.requestFocus();
					}
					if (mPendingCheckForLongPress == null) {

						mPendingCheckForLongPress = new Runnable() {
							public void run() {
								// ((WebView)v).performLongClick();
								if (!mIsSelectingText) {
									activity.openContextMenu(webview);
									mHasPerformedLongPress = true;
									mIsSelectingText = false;
								}
							}
						};
					}
					mHasPerformedLongPress = false;
					v.postDelayed(mPendingCheckForLongPress,
							ViewConfiguration.getLongPressTimeout());
					break;

				case MotionEvent.ACTION_MOVE:
					final int x = (int) event.getX();
					final int y = (int) event.getY();

					// Be lenient about moving outside of buttons
					int slop = ViewConfiguration.get(v.getContext())
							.getScaledTouchSlop();
					if ((x < 0 - slop) || (x >= v.getWidth() + slop)
							|| (y < 0 - slop) || (y >= v.getHeight() + slop)) {

						if (mPendingCheckForLongPress != null) {
							v.removeCallbacks(mPendingCheckForLongPress);
						}
					}
					break;
				default:
					return false;
				}
				return false;
			}
		});
	}

	public static synchronized void emulateShiftHeld(WebView view) {

		try {
			KeyEvent shiftPressEvent = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN,
					KeyEvent.KEYCODE_SHIFT_LEFT, 0, 0);
			shiftPressEvent.dispatch(view);
		} catch (Exception e) {
			Log.e(TAG, "Exception in emulateShiftHeld()", e);
		}
	}

	public synchronized void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo, final int copy, String menuString) {
		MenuItem menuitem = menu.add(1, copy, Menu.NONE, menuString);
		menuitem.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (item.getItemId() == copy) {
					// emulateShiftHeld(webview);
					selectAndCopyText(webview);
				}
				return false;
			}
		});
	}

	public static synchronized void selectAndCopyText(WebView v) {
		try {

			mIsSelectingText = true;

			// Method m = WebView.class.getMethod("emulateShiftHeld",
			// Boolean.TYPE);
			// m.invoke(v, false);

			if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.ECLAIR) {
				Method m = WebView.class.getMethod("emulateShiftHeld",
						Boolean.TYPE);
				m.invoke(v, false);
			} else {
				Method m = WebView.class.getMethod("emulateShiftHeld");
				m.invoke(v);
			}

		} catch (Exception e) {
			// fallback
			emulateShiftHeld(v);
		} finally {
			Log.i(TAG, "Select text");
		}
	}
}
