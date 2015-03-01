package wjq.WidgetDemo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.RatingBar.OnRatingBarChangeListener;

public class RatingBarDemo extends Activity implements
		OnRatingBarChangeListener {
	private RatingBar mSmallRatingBar;
	private RatingBar mIndicatorRatingBar;
	private TextView mRatingText;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ratingbarpage);
		
		 mRatingText = (TextView) findViewById(R.id.rating);

	        // We copy the most recently changed rating on to these indicator-only
	        // rating bars
	        mIndicatorRatingBar = (RatingBar) findViewById(R.id.indicator_ratingbar);
	        mSmallRatingBar = (RatingBar) findViewById(R.id.small_ratingbar);
	        
	        // The different rating bars in the layout. Assign the listener to us.
	        ((RatingBar)findViewById(R.id.ratingbar1)).setOnRatingBarChangeListener(this);
	        ((RatingBar)findViewById(R.id.ratingbar2)).setOnRatingBarChangeListener(this);
	}

	@Override
	public void onRatingChanged(RatingBar ratingBar, float rating,
			boolean fromUser) {
		 final int numStars = ratingBar.getNumStars();
	        mRatingText.setText( 
	                 " Κά»¶Σ­¶Θ" + rating + "/" + numStars);

	        // Since this rating bar is updated to reflect any of the other rating
	        // bars, we should update it to the current values.
	        if (mIndicatorRatingBar.getNumStars() != numStars) {
	            mIndicatorRatingBar.setNumStars(numStars);
	            mSmallRatingBar.setNumStars(numStars);
	        }
	        if (mIndicatorRatingBar.getRating() != rating) {
	            mIndicatorRatingBar.setRating(rating);
	            mSmallRatingBar.setRating(rating);
	        }
	        final float ratingBarStepSize = ratingBar.getStepSize();
	        if (mIndicatorRatingBar.getStepSize() != ratingBarStepSize) {
	            mIndicatorRatingBar.setStepSize(ratingBarStepSize);
	            mSmallRatingBar.setStepSize(ratingBarStepSize);
	        }

	}

}
