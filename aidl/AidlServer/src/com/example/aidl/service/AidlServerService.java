package com.example.aidl.service;
import com.example.aidl.IAIDLServerService.Stub;
import com.example.aidl.Book;
import com.example.aidl.IAIDLServerService;
import com.example.aidl.ICallback;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
public class AidlServerService extends Service {
    protected static final String TAG = "AidlServerService";
    private int mCount = 0;
    private Handler mHandler = new Handler(){
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
			case 0:
				mCount++;
				callback(mCount);
				mHandler.sendEmptyMessageDelayed(0, 1000);
				break;
			default:
				break;
			}
    	}
    };
    
    void callback(int count){
    	int N = mCallbacks.beginBroadcast();
    	if (N > 0) {
    		try {
				mCallbacks.getBroadcastItem(0).setCount(count);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	mCallbacks.finishBroadcast();
    }
    
	@Override
    public IBinder onBind(Intent intent) {
		Log.i(TAG, "onBind()");
		mHandler.sendEmptyMessageDelayed(0, 1000);
        return mBinder;
    }
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.i(TAG, "onUnbind()");
		mHandler.removeMessages(0);
		mCallbacks.kill();
		return super.onUnbind(intent);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "onStartCommand() ");
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy() ");
		super.onDestroy();
	}
	
    /**
     * 在AIDL文件中定义的接口实现。
     */
    private IAIDLServerService.Stub mBinder = new Stub() {

        public String sayHello() throws RemoteException {
        	Log.i(TAG, "sayHello()");
            return "Hello";
        }

        public Book getBook() throws RemoteException {
        	Log.i(TAG, "getBook()");
            Book mBook = new Book();
            mBook.setBookName("Android应用开发");
            mBook.setBookPrice(50);
            return mBook;
        }

		public void registerCallback(ICallback cb) throws RemoteException {
			if (cb != null) {
				mCallbacks.register(cb);
			}
		}

		public void unregisterCallback(ICallback cb) throws RemoteException {
			if (cb != null) {
				mCallbacks.unregister(cb);
			}
		}
    };
    
    private RemoteCallbackList<ICallback> mCallbacks = new RemoteCallbackList<ICallback>();
}