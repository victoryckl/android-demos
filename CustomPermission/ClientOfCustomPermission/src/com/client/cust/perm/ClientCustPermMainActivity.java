package com.client.cust.perm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ClientCustPermMainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button btn = (Button) findViewById(R.id.btn);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				// 通过指定包名来启动我们想要启动的 Activity 注意第二个参数是完全限定包名
				intent.setClassName("com.cust.perm",
						"com.cust.perm.PrivActivity");
				startActivity(intent);
			}
		});
	}
}