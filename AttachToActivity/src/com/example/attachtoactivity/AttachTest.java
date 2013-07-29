package com.example.attachtoactivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;

@SuppressLint("NewApi")
public class AttachTest extends Activity {

    private int hideFlags = 0;
	
    private int showFlags = View.SYSTEM_UI_FLAG_VISIBLE;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attach_test);
		
//	    hideFlags |=  View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//	    		| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//	    		| View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

		
    	hideFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE 
        		| View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		
		//getWindow().getDecorView().setSystemUiVisibility(hideFlags);
		PopViewLayout p = PopViewLayout.create(getApplicationContext());
		p.attachToActivity(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_attach_test, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_exit:
			AttachTest.this.finish();
			break;

		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		setNavVisibility(true);
		return super.onTouchEvent(event);
	}
	
	//------------------------------
    void setNavVisibility(boolean visible) {
    	View view = getWindow().getDecorView();
        view.setSystemUiVisibility(hideFlags);
    }
}
