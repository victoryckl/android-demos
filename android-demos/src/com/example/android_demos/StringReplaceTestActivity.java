package com.example.android_demos;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.TextView;

public class StringReplaceTestActivity extends Activity {
	private static final String TAG = StringReplaceTestActivity.class.getSimpleName();
	
	private TextView tvString1, tvString2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_string_activity);
		
		init();
	}
	
	private void init() {
		tvString1 = (TextView)findViewById(R.id.tv_string_out1);
		tvString2 = (TextView)findViewById(R.id.tv_string_out2);

		oText = new String(buf);
		tvString1.setText(oText);
		Log.i(TAG, oText);
		
		String text = oText.replace("\n", "<br/>");
		Log.i(TAG, text);
		
		Spanned sp = Html.fromHtml(text);
		Log.i(TAG, sp.toString());
		
		tvString2.setText(sp);
	}
	
	private String oText = "\nstring1\nstring2\nstring3";
	private char[] buf = new char[] {'\n', '　', '　', '孩', '子', '们', '，', '\n', '每', '天', '清', '晨', '\n'};
}
