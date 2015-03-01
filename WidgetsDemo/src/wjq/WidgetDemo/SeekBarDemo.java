package wjq.WidgetDemo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SeekBarDemo extends Activity implements OnSeekBarChangeListener{
	private SeekBar mSeekBar;
	private TextView mProgressText;
	private TextView mTrackingText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seekbarpage);
		
		mSeekBar = (SeekBar)findViewById(R.id.seek);
        mSeekBar.setOnSeekBarChangeListener(this);
        mProgressText = (TextView)findViewById(R.id.progress);
        mTrackingText = (TextView)findViewById(R.id.tracking);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		 mProgressText.setText("进度："+progress);
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		  mTrackingText.setText("开始跟踪");
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		 mTrackingText.setText("停止跟踪");
	}

}
