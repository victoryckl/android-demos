package com.example.messenger.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

import com.example.messenger.common.Msg;

public class MessengerService extends Service {

	private Messenger mActivityMessenger;
    /**
     * 在Service处理Activity传过来消息的Handler
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Msg.SAY_HELLO:
                	showToast("hello!");
                    break;
                case Msg.SET_MESSENGER:
                	mActivityMessenger = (Messenger) msg.obj;
                	mHandler.sendEmptyMessageDelayed(Msg.TICK, 1000);
                	break;
                case Msg.TICK:
                	sendTick();
                	mHandler.sendEmptyMessageDelayed(Msg.TICK, 1000);
                	break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
    private IncomingHandler mHandler = new IncomingHandler();

    /**
     * 这个Messenger可以关联到Service里的Handler，Activity用这个对象发送Message给Service，Service通过Handler进行处理。
     */
    final Messenger mMessenger = new Messenger(mHandler);

    /**
     * 当Activity绑定Service的时候，通过这个方法返回一个IBinder，Activity用这个IBinder创建出的Messenger，就可以与Service的Handler进行通信了
     */
    @Override
    public IBinder onBind(Intent intent) {
    	showToast("onBind()");
        return mMessenger.getBinder();
    }
    
    @Override
    public void onDestroy() {
    	showToast("onDestroy()");
    	mHandler.removeMessages(Msg.TICK);
    	super.onDestroy();
    }
    
    private void showToast(String msg) {
    	Toast.makeText(getApplicationContext(), 
    			"service: "+msg, Toast.LENGTH_SHORT).show();
    }
    
    //-----------------
    private void sendTick() {
        Message msg = Message.obtain(null, Msg.TICK, (int) System.currentTimeMillis(), 0);
        try {
        	mActivityMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}