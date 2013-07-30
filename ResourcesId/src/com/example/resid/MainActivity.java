package com.example.resid;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int id = ResourcesId.getResourcesId(this, "layout", "activity_main");
		setContentView(id);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		int id = ResourcesId.getResourcesId(this, "menu", "main");
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
