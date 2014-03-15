package com.example.aidl.client;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.aidl.IAIDLServerService;
import com.example.aidl.ICallback;

public class AidlClientActivity extends Activity {

    protected static final String TAG = "AidlClientActivity";
	private TextView mTextView;
	private TextView mTextCount;
    private Button mButton;

    private Handler mHandler = new Handler(){
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
			case 0:
				mTextCount.setText(""+msg.arg1);
				break;
			default:
				break;
			}
    	}
    };
    
    private ICallback mCallback = new ICallback.Stub() {
		public void setCount(int count) throws RemoteException {
			Message msg = Message.obtain();
			msg.what = 0;
			msg.arg1 = count;
			mHandler.sendMessage(msg);
		}
	};
	
    private IAIDLServerService mIaidlServerService = null;

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
        	Log.i(TAG, "onServiceDisconnected("+name.toString()+")");
        	if (mIaidlServerService != null) {
        		try {
					mIaidlServerService.unregisterCallback(mCallback);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
        	}
            mIaidlServerService = null;
        }
        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.i(TAG, "onServiceConnected("+name.toString()+","+service+")");
            mIaidlServerService = IAIDLServerService.Stub.asInterface(service);
            Log.i(TAG, "mIaidlServerService = "+mIaidlServerService);
            //aidl通信
            try {
            	mIaidlServerService.registerCallback(mCallback);
                String mText = "Say hello: " + mIaidlServerService.sayHello() + "\n";
                mText += "书名: " + mIaidlServerService.getBook().getBookName()+"\n";
                mText += "价格: " + mIaidlServerService.getBook().getBookPrice();
                mTextView.setText(mText);
            } catch (RemoteException e) {
            	Log.i(TAG, "onServiceConnected()-------------------------------");
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //初始化控件
        mTextView = (TextView)findViewById(R.id.textview);
        mButton = (Button)findViewById(R.id.button);
        mTextCount = (TextView)findViewById(R.id.count);
        //增加事件响应
        mButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                //bindService
                Intent service = new Intent("com.example.aidl.IAIDLServerService");
                if(bindService(service, mConnection,BIND_AUTO_CREATE)){
                	Log.i(TAG, "bindService() success!");
                } else {
                	Log.i(TAG, "bindService() failed!");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if (mIaidlServerService != null) {
    		Log.i(TAG, "unbindService()");
    		unbindService(mConnection);
    	} else {
    		Log.i(TAG, "mIaidlServerService is null!");
    	}
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	finish();
    	return super.onKeyDown(keyCode, event);
    }
}
