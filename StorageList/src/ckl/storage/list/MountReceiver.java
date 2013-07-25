package ckl.storage.list;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.drm.DrmStore.Action;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class MountReceiver {
	private static final String TAG = "MountReceiver";
	
	private static Handler mHandler;
	private static int mMsgId;
	
	private static BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "onReceive:" + intent.getAction());
			if (mHandler != null) {
				Message msg = Message.obtain();
				msg.what = mMsgId;
				msg.obj = intent;
				mHandler.sendMessage(msg);
			}
		}
	};
	
	public static void registerReceiver(Activity activity, Handler handler, int msgid) {
		Log.i(TAG, "registerReceiver()");
		try {
			mHandler = handler;
			mMsgId = msgid;
			if (activity != null) {
				IntentFilter mFilter = new IntentFilter();
				mFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
				mFilter.addAction(Intent.ACTION_MEDIA_BUTTON);
				mFilter.addAction(Intent.ACTION_MEDIA_CHECKING);
				mFilter.addAction(Intent.ACTION_MEDIA_EJECT);
				mFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
				mFilter.addAction(Intent.ACTION_MEDIA_NOFS);
				mFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
				mFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
				mFilter.addAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				mFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
				mFilter.addAction(Intent.ACTION_MEDIA_SHARED);
				mFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTABLE);
				mFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
				
				mFilter.addDataScheme("file");
				activity.registerReceiver(mReceiver, mFilter);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void unregisterReceiver(Activity activity) {
		Log.i(TAG, "unRegisterReceiver()");
		try {
			if (activity != null) {
				activity.unregisterReceiver(mReceiver);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
