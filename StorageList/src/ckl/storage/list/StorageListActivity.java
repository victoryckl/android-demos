package ckl.storage.list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.storage.StorageManager;
import android.util.Log;
import android.widget.TextView;

public class StorageListActivity extends Activity {
    private static final String TAG = "StorageListActivity";
    
    private TextView mList, mStatus;
    StorageList mStorageList;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        MountReceiver.registerReceiver(this, mHandler, 0);

        mStorageList = new StorageList(this);
        String[] paths = mStorageList.getVolumePaths();
        String text = "storage list:\n";
        for (String path:paths){
        	Log.i(TAG, path);
        	text += path + "\n";
        }
        Log.i(TAG, "Environment: "+Environment.getExternalStorageDirectory().getAbsolutePath());
        mList = (TextView)findViewById(R.id.list);
        mList.setText(text);
        
        mStatus = (TextView)findViewById(R.id.status);
    }
    
    @Override
    protected void onDestroy() {
    	MountReceiver.unregisterReceiver(this);
    	super.onDestroy();
    }
    
    private Handler mHandler = new Handler() {
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
			case 0:
				Intent intent = (Intent)msg.obj;
				String str = "Status:\n";
				str += "Component: "  + intent.getComponent() + "\n";
				str += "Aciton: "     + intent.getAction() + "\n";
				str += "Categories: " + intent.getCategories() + "\n";
				str += "Data: "       + intent.getData() + "\n";
				str += "DataType: "   + intent.getType() + "\n";
				str += "DataSchema: " + intent.getScheme() + "\n";
				mStatus.setText(str);
				Log.i(TAG, str);
				
//                Log.i(TAG, "Component: "  + intent.getComponent());  
//                Log.i(TAG, "Aciton: "     + intent.getAction());  
//                Log.i(TAG, "Categories: " + intent.getCategories());  
//                Log.i(TAG, "Data: "       + intent.getData());  
//                Log.i(TAG, "DataType: "   + intent.getType());  
//                Log.i(TAG, "DataSchema: " + intent.getScheme());  
				break;

			default:
				break;
			}
    	};
    };
}