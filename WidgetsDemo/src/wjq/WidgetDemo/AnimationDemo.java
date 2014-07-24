package wjq.WidgetDemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * 动画Animation示例
 * 
 * @author 记忆的永恒
 * 
 */
public class AnimationDemo extends Activity implements OnClickListener,
		OnItemSelectedListener {

	private View v;
	private String[] mStrings = { "向上", "向右", "穿越", "旋转" };

	private static final String[] INTERPOLATORS = { "加速", "Decelerate", "减速",
			"左右", "Overshoot", "Anticipate/Overshoot", "弹回" };
	private ViewFlipper mFlipper;
	private Spinner spinner;
	private Spinner spinner1;
	private ArrayAdapter<String> aa;
	private ArrayAdapter<String> aa1;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.animationpage);
		v = findViewById(R.id.login);
		v.setOnClickListener(this);
		mFlipper = (ViewFlipper) findViewById(R.id.flipper);

		// 反转
		mFlipper.startFlipping();

		spinner = (Spinner) findViewById(R.id.spinner);
		aa = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, mStrings);
		aa
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(aa);
		spinner.setOnItemSelectedListener(this);

		spinner1 = (Spinner) findViewById(R.id.spinner1);
		aa1 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, INTERPOLATORS);
		aa1
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner1.setAdapter(aa1);
		spinner1.setOnItemSelectedListener(this);
	}

	@Override
	public void onClick(View v) {
		Animation shake = AnimationUtils.loadAnimation(this, R.anim.animlayout);
		findViewById(R.id.pw).startAnimation(shake);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		if (parent.getAdapter() == aa) {
			switch (position) {

			case 0:
				mFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
						R.anim.push_up_in));
				mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
						R.anim.push_up_out));
				break;
			case 1:
				mFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
						R.anim.push_left_in));
				mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
						R.anim.push_left_out));
				break;
			case 2:
				mFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
						android.R.anim.fade_in));
				mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
						android.R.anim.fade_out));
				break;
			default:
				mFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
						R.anim.hyperspace_in));
				mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
						R.anim.hyperspace_out));
				break;
			}
		}

		else {
			final View target = findViewById(R.id.target);
			final View targetParent = (View) target.getParent();
			Animation anm = new TranslateAnimation(0.0f, targetParent
					.getWidth()
					- target.getWidth()
					- targetParent.getPaddingLeft()
					- targetParent.getPaddingRight(), 0.0f, 0.0f);

			anm.setDuration(1000);
			anm.setStartOffset(300);
			anm.setRepeatMode(Animation.RESTART);
			anm.setRepeatCount(Animation.INFINITE);

			switch (position) {
			case 0:
				anm.setInterpolator(AnimationUtils.loadInterpolator(this,
						android.R.anim.accelerate_interpolator));
				break;
			case 1:
				anm.setInterpolator(AnimationUtils.loadInterpolator(this,
						android.R.anim.decelerate_interpolator));
				break;
			case 2:
				anm.setInterpolator(AnimationUtils.loadInterpolator(this,
						android.R.anim.accelerate_decelerate_interpolator));
				break;
			case 3:
				anm.setInterpolator(AnimationUtils.loadInterpolator(this,
						android.R.anim.anticipate_interpolator));
				break;
			case 4:
				anm.setInterpolator(AnimationUtils.loadInterpolator(this,
						android.R.anim.overshoot_interpolator));
				break;
			case 5:
				anm.setInterpolator(AnimationUtils.loadInterpolator(this,
						android.R.anim.anticipate_overshoot_interpolator));
				break;
			case 6:
				anm.setInterpolator(AnimationUtils.loadInterpolator(this,
						android.R.anim.bounce_interpolator));
				break;
			}

			target.startAnimation(anm);
		}

	}

	@Override
	public void onNothingSelected(AdapterView parent) {
		// TODO Auto-generated method stub

	}

}
