package wjq.WidgetDemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;

public class ProgressBarDemo extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		 requestWindowFeature(Window.FEATURE_PROGRESS);
	        setContentView(R.layout.probarpage);
	        setProgressBarVisibility(true);
	        
	        final ProgressBar progressHorizontal = (ProgressBar) findViewById(R.id.progress_horizontal);
	        setProgress(progressHorizontal.getProgress() * 100);
	        setSecondaryProgress(progressHorizontal.getSecondaryProgress() * 100);
	        
	        Button button = (Button) findViewById(R.id.increase);
	        button.setOnClickListener(new Button.OnClickListener() {
	            public void onClick(View v) {
	                progressHorizontal.incrementProgressBy(1);
	                // Title progress is in range 0..10000
	                setProgress(100 * progressHorizontal.getProgress());
	            }
	        });

	        button = (Button) findViewById(R.id.decrease);
	        button.setOnClickListener(new Button.OnClickListener() {
	            public void onClick(View v) {
	                progressHorizontal.incrementProgressBy(-1);
	                // Title progress is in range 0..10000
	                setProgress(100 * progressHorizontal.getProgress());
	            }
	        });

	        button = (Button) findViewById(R.id.increase_secondary);
	        button.setOnClickListener(new Button.OnClickListener() {
	            public void onClick(View v) {
	                progressHorizontal.incrementSecondaryProgressBy(1);
	                // Title progress is in range 0..10000
	                setSecondaryProgress(100 * progressHorizontal.getSecondaryProgress());
	            }
	        });

	        button = (Button) findViewById(R.id.decrease_secondary);
	        button.setOnClickListener(new Button.OnClickListener() {
	            public void onClick(View v) {
	                progressHorizontal.incrementSecondaryProgressBy(-1);
	                // Title progress is in range 0..10000
	                setSecondaryProgress(100 * progressHorizontal.getSecondaryProgress());
	            }
	        });
	        
	}

}
