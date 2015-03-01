package wjq.WidgetDemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;

public class ChronometerDemo extends Activity {
private Chronometer mChronometer;
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chronometerpage);
		Button button;

        mChronometer = (Chronometer) findViewById(R.id.chronometer);

        // Watch for button clicks.
        button = (Button) findViewById(R.id.start);
        button.setOnClickListener(mStartListener);

        button = (Button) findViewById(R.id.stop);
        button.setOnClickListener(mStopListener);

        button = (Button) findViewById(R.id.reset);
        button.setOnClickListener(mResetListener);

        button = (Button) findViewById(R.id.set_format);
        button.setOnClickListener(mSetFormatListener);

        button = (Button) findViewById(R.id.clear_format);
        button.setOnClickListener(mClearFormatListener);
    }

    View.OnClickListener mStartListener = new OnClickListener() {
        public void onClick(View v) {
            mChronometer.start();
        }
    };

    View.OnClickListener mStopListener = new OnClickListener() {
        public void onClick(View v) {
            mChronometer.stop();
        }
    };

    View.OnClickListener mResetListener = new OnClickListener() {
        public void onClick(View v) {
            mChronometer.setBase(SystemClock.elapsedRealtime());
        }
    };

    View.OnClickListener mSetFormatListener = new OnClickListener() {
        public void onClick(View v) {
            mChronometer.setFormat("Formatted time (%s)");
        }
    };

    View.OnClickListener mClearFormatListener = new OnClickListener() {
        public void onClick(View v) {
            mChronometer.setFormat(null);
        }
    };
}
