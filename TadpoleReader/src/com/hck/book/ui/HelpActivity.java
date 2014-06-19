package com.hck.book.ui;



import com.hck.test.R;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HelpActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        LinearLayout layout=new LinearLayout(this);
        params.leftMargin=5;
        params.topMargin=10;
        params.rightMargin=5;
        layout.setBackgroundResource(R.drawable.bg);
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.VERTICAL);
        
        
        
		TextView textView=new TextView(this);
		textView.setTextColor(Color.BLACK);
		textView.setTextSize(20);
		textView.setText(getResources().getString(R.string.helps));
		textView.setPadding(3, 3, 3, 3);
		layout.addView(textView);
		setContentView(layout);
		
	}

	

}
