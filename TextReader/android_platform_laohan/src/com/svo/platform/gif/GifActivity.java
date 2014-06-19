package com.svo.platform.gif;


import android.app.Activity;
import android.graphics.Movie;
import android.os.Bundle;

import com.svo.gifview.GifView;
import com.svo.platform.R;

public class GifActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gif_layout);
		setTitle("Gif图片浏览");
		GifView gifView = (GifView) findViewById(R.id.gif);
		Movie movie = Movie.decodeFile(getIntent().getStringExtra("path"));
		gifView.setMovie(movie);
	}
}
