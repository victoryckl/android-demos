package com.example.webstest;

import java.io.IOException;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class WebTest extends Activity {

	protected MediaPlayer mMediaPlayer;
	private String path;
	private TextView mTextAudio;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_test);
		initViews();
	}
	
	private void initViews() {
		mTextAudio = (TextView)findViewById(R.id.editText1);
		findViewById(R.id.button1).setOnClickListener(mBtnListener);
	}

	private OnClickListener mBtnListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button1:
                path = mTextAudio.getText().toString();
                if (path == "") {
                    // Tell the user to provide an audio file URL.
                    Toast
                            .makeText(
                            		WebTest.this,
                                    "Please edit MediaPlayer_Audio Activity, "
                                            + "and set the path variable to your audio file path."
                                            + " Your audio file must be stored on sdcard.",
                                    Toast.LENGTH_LONG).show();

                }
                mMediaPlayer = new MediaPlayer();
                try {
					mMediaPlayer.setDataSource(path);
	                mMediaPlayer.prepare();
	                mMediaPlayer.start();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;

			default:
				break;
			}
		}
	};
	
    protected void onDestroy() {
        super.onDestroy();
       // if (mMediaPlayer != null) {
       //     mMediaPlayer.release();
       //     mMediaPlayer = null;
       // }
    }
}
