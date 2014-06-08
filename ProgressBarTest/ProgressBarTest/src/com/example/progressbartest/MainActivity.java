package com.example.progressbartest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;

public class MainActivity extends Activity {
	private ProgressBar progress;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		progress = (ProgressBar) findViewById(R.id.progress_horizontal);
		progress.setMax(100);
		progress.setProgress(28);
		
		findViewById(R.id.decrease).setOnClickListener(mClickListener);
		findViewById(R.id.increase).setOnClickListener(mClickListener);
	}
	
	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.decrease:
				progress.incrementProgressBy(-5);
				break;
			case R.id.increase:
				progress.incrementProgressBy(5);
				break;
			default:
				break;
			}
		}
	};
}
