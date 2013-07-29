package org.ckl.root.mount;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import org.ckl.root.R;
import org.ckl.root.RootCmd;

public class MountSdcard extends Activity {
	private static final String TAG = "MountSdcard";
	private EditText mEdit1;
	private Button mButton1;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mount);
		mEdit1 = (EditText)findViewById(R.id.mountEdit1);
		mButton1 = (Button)findViewById(R.id.mountBtn1);
		
		mButton1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.mountBtn1:
					String cmd = mEdit1.getText().toString();
					RootCmd.execRootCmd(cmd);
					break;
				default:
					break;
				}
			}
		});
	}

	
}
