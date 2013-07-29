package org.ckl.root;

import java.io.DataOutputStream;
import java.io.File;

import org.ckl.root.mount.MountSdcard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ExecCmdActivity extends Activity {
	protected static final String TAG = "ExecCmdActivity";
	private TextView mMountText, mInfoText;
	private Button mMount, mRefresh, mExecBtn, mJumpToMount;
	private EditText mCmdEdit;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mRefresh = (Button)findViewById(R.id.refresh);
        mRefresh.setOnClickListener(mClickListener);

        mMountText = (TextView)findViewById(R.id.mountText);
        mMount = (Button)findViewById(R.id.mountBtn1);
        mMount.setOnClickListener(mClickListener);
        if (RootCmd.haveRoot()) {
        	refreshStatus();
        } else {
        	mMountText.setText("Please root you machine first!");
        	mMount.setVisibility(View.GONE);
        	mRefresh.setVisibility(View.GONE);
        }
        
        mCmdEdit = (EditText)findViewById(R.id.cmdEdit1);
        mExecBtn = (Button)findViewById(R.id.execBtn1);
        mExecBtn.setOnClickListener(mClickListener);

        mInfoText = (TextView)findViewById(R.id.outputinfo);
        
        if (RootCmd.haveRoot()) {
        	Toast.makeText(this, "have root", Toast.LENGTH_LONG).show();
        } else {
        	Toast.makeText(this, "not root", Toast.LENGTH_LONG).show();
        }
        
        mJumpToMount = (Button)findViewById(R.id.jumptomount);
        mJumpToMount.setOnClickListener(mClickListener);
    }
    
    private void refreshStatus() {
        String text = SystemPartition.isWriteable() ? " rw" : " ro";
        mMountText.setText("" + SystemPartition.getSystemMountPiont() + " /system," + text);
        text = SystemPartition.isWriteable() ? " ro" : " rw";
        mMount.setText("mount to" + text);
    }
    
    private OnClickListener mClickListener = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.refresh:
				refreshStatus();
				break;
			case R.id.mountBtn1:
				SystemPartition.remountSystem(!SystemPartition.isWriteable());
				refreshStatus();
				break;
			case R.id.execBtn1:
				String result = RootCmd.execRootCmd(mCmdEdit.getText().toString());
				Log.i(TAG, result);
				mInfoText.setText(result);
				break;
			case R.id.jumptomount:
				startActivity(new Intent(ExecCmdActivity.this, MountSdcard.class));
				break;
			default:
				break;
			}
		}
	};
	
	protected void onPause() {
		super.onPause();
		
	};
}