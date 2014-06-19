package com.svo.platform.widget;



import android.content.Context;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.svo.platform.R;

public class PushRefreshListView extends ListView implements OnScrollListener{
	 private static final String TAG = "listview";

	    /**
	     * 往下拖动列表至“松开刷新”完全显示出来
	     */
	    private final static int RELEASE_To_REFRESH = 0;

	    /**
	     * 拖动列表未到“松开刷新”位置
	     */
	    private final static int PULL_To_REFRESH = 1;

	    /**
	     * 刷新
	     */
	    private final static int REFRESHING = 2;

	    /**
	     * 刷新完成
	     */
	    private final static int DONE = 3;

	    //    private final static int LOADING = 4;

	    // 实际的padding的距离与界面上偏移距离的比例
	    private final static int RATIO = 3;

	    /**
	     * headview布局填充器
	     */
	    private LayoutInflater inflater;

	    /**
	     * liestview顶部布局
	     */
	    private LinearLayout headView;

	    /**
	     * 顶部“下拉刷新”或“松开刷新”
	     */
	    private TextView tipsTextview;

	    /**
	     * 最近刷新时间显示
	     */
	    private TextView lastUpdatedTextView;

	    /**
	     * 箭头
	     */
	    private ImageView arrowImageView;

	    /**
	     * 刷新时的进度条
	     */
	    private ProgressBar progressBar;

	    private RotateAnimation animation;
	    private RotateAnimation reverseAnimation;

	    // 用于保证startY的值在一个完整的touch事件中只被记录一次
	    private boolean isRecored;

	    private int headContentWidth;
	    private int headContentHeight;

	    private int startY;
	    private int firstItemIndex;

	    private int state;

	    private boolean isBack;

	    private OnRefreshListener refreshListener;

	    private boolean isRefreshable;

	    public PushRefreshListView(Context context)
	    {
	        super(context);
	        init(context);
	    }

	    public PushRefreshListView(Context context, AttributeSet attrs)
	    {
	        super(context, attrs);
	        init(context);
	    }

	    private void init(Context context)
	    {
	        setCacheColorHint(context.getResources().getColor(R.color.transparent));
	        inflater = LayoutInflater.from(context);

	        headView = (LinearLayout) inflater.inflate(R.layout.pull_refresh_header, null);

	        arrowImageView = (ImageView) headView
	                .findViewById(R.id.head_arrowImageView);
	        arrowImageView.setMinimumWidth(70);
	        arrowImageView.setMinimumHeight(50);
	        progressBar = (ProgressBar) headView
	                .findViewById(R.id.head_progressBar);
	        tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);
	        lastUpdatedTextView = (TextView) headView
	                .findViewById(R.id.head_lastUpdatedTextView);

	        measureView(headView);
	        headContentHeight = headView.getMeasuredHeight();
	        headContentWidth = headView.getMeasuredWidth();

	        headView.setPadding(0, -1 * headContentHeight, 0, 0);
	        headView.invalidate();

	        Log.i("size", "width:" + headContentWidth + " height:"
	                + headContentHeight);

	        addHeaderView(headView, null, false);
	        setOnScrollListener(this);

	        animation = new RotateAnimation(0, 180,
	                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
	                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
	        animation.setInterpolator(new LinearInterpolator());
	        animation.setDuration(250);
	        animation.setFillAfter(true);

	        reverseAnimation = new RotateAnimation(180, 0,
	                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
	                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
	        reverseAnimation.setInterpolator(new LinearInterpolator());
	        reverseAnimation.setDuration(200);
	        reverseAnimation.setFillAfter(true);

	        state = DONE;
	        isRefreshable = false;
	    }

	    public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2,
	            int arg3)
	    {
	        firstItemIndex = firstVisiableItem;
	    }

	    public void onScrollStateChanged(AbsListView arg0, int arg1)
	    {
	    }

	    public boolean onTouchEvent(MotionEvent event)
	    {

	        if (isRefreshable)
	        {
	            switch (event.getAction())
	            {
	                case MotionEvent.ACTION_DOWN:
	                    if (firstItemIndex == 0 && !isRecored)
	                    {
	                        isRecored = true;
	                        startY = (int) event.getY();
	                        Log.v(TAG, "在down时候记录当前位置");
	                    }
	                break;

	                case MotionEvent.ACTION_UP:

	                    if (state != REFRESHING && firstItemIndex == 0)
	                    {
	                        if (state == DONE)
	                        {
	                            // 什么都不做
	                        }
	                        if (state == PULL_To_REFRESH)
	                        {
	                            state = DONE;
	                            changeHeaderViewByState();

	                            Log.v(TAG, "由下拉刷新状态，到done状态");
	                        }
	                        if (state == RELEASE_To_REFRESH)
	                        {
	                            state = REFRESHING;
	                            changeHeaderViewByState();
	                            onRefresh();

	                            Log.v(TAG, "由松开刷新状态，到done状态");
	                        }
	                    }

	                    isRecored = false;
	                    isBack = false;

	                break;

	                case MotionEvent.ACTION_MOVE:
	                    int tempY = (int) event.getY();

	                    if (!isRecored && firstItemIndex == 0)
	                    {
	                        Log.v(TAG, "在move时候记录下位置");
	                        isRecored = true;
	                        startY = tempY;
	                    }

	                    if (state != REFRESHING && isRecored /*&& state != LOADING*/)
	                    {

	                        // 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动

	                        // 可以松手去刷新了
	                        if (state == RELEASE_To_REFRESH)
	                        {

	                            setSelection(0);

	                            // 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
	                            if (((tempY - startY) / RATIO < headContentHeight)
	                                    && (tempY - startY) > 0)
	                            {
	                                state = PULL_To_REFRESH;
	                                changeHeaderViewByState();

	                                Log.v(TAG, "由松开刷新状态转变到下拉刷新状态");
	                            }
	                            // 一下子推到顶了
	                            else if (tempY - startY <= 0)
	                            {
	                                state = DONE;
	                                changeHeaderViewByState();

	                                Log.v(TAG, "由松开刷新状态转变到done状态");
	                            }
	                            // 更新headView的position
	                            headView.setPadding(0, (tempY - startY) / RATIO
	                                    - headContentHeight, 0, 0);
	                        }
	                        // 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
	                        if (state == PULL_To_REFRESH)
	                        {

	                            setSelection(0);

	                            // 下拉到可以进入RELEASE_TO_REFRESH的状态
	                            if ((tempY - startY) / RATIO >= headContentHeight)
	                            {
	                                state = RELEASE_To_REFRESH;
	                                isBack = true;
	                                changeHeaderViewByState();

	                                Log.v(TAG, "由done或者下拉刷新状态转变到松开刷新");
	                            }
	                            // 上推到顶了
	                            else if (tempY - startY <= 0)
	                            {
	                                state = DONE;
	                                changeHeaderViewByState();

	                                Log.v(TAG, "由DOne或者下拉刷新状态转变到done状态");
	                            }
	                            // 更新headView的position
	                            headView.setPadding(0, -1 * headContentHeight
	                                    + (tempY - startY) / RATIO, 0, 0);
	                        }

	                        // done状态下
	                        if (state == DONE)
	                        {
	                            if (tempY - startY > 0)
	                            {
	                                state = PULL_To_REFRESH;
	                                changeHeaderViewByState();
	                            }
	                        }

	                    }

	                break;
	            }
	        }

	        return super.onTouchEvent(event);
	    }

	    // 当状态改变时候，调用该方法，以更新界面
	    private void changeHeaderViewByState()
	    {
	        switch (state)
	        {
	            case RELEASE_To_REFRESH:
	                arrowImageView.setVisibility(View.VISIBLE);
	                progressBar.setVisibility(View.GONE);
	                tipsTextview.setVisibility(View.VISIBLE);
	                lastUpdatedTextView.setVisibility(View.VISIBLE);

	                arrowImageView.clearAnimation();
	                arrowImageView.startAnimation(animation);

	                tipsTextview.setText("松开刷新");

	                Log.v(TAG, "当前状态，松开刷新");
	            break;
	            case PULL_To_REFRESH:
	                progressBar.setVisibility(View.GONE);
	                tipsTextview.setVisibility(View.VISIBLE);
	                lastUpdatedTextView.setVisibility(View.VISIBLE);
	                arrowImageView.clearAnimation();
	                arrowImageView.setVisibility(View.VISIBLE);
	                // 是由RELEASE_To_REFRESH状态转变来的
	                if (isBack)
	                {
	                    isBack = false;
	                    arrowImageView.clearAnimation();
	                    arrowImageView.startAnimation(reverseAnimation);

	                    tipsTextview.setText("下拉刷新");
	                }
	                else
	                {
	                    tipsTextview.setText("下拉刷新");
	                }
	                Log.v(TAG, "当前状态，下拉刷新");
	            break;

	            case REFRESHING:

	                headView.setPadding(0, 0, 0, 0);

	                progressBar.setVisibility(View.VISIBLE);
	                arrowImageView.clearAnimation();
	                arrowImageView.setVisibility(View.GONE);
	                tipsTextview.setText("正在刷新...");
	                lastUpdatedTextView.setVisibility(View.VISIBLE);

	                Log.v(TAG, "当前状态,正在刷新...");
	            break;
	            case DONE:
	                headView.setPadding(0, -1 * headContentHeight, 0, 0);

	                progressBar.setVisibility(View.GONE);
	                arrowImageView.clearAnimation();
	                arrowImageView.setImageResource(R.drawable.pull_refresh_down);
	                tipsTextview.setText("下拉刷新");
	                lastUpdatedTextView.setVisibility(View.VISIBLE);

	                Log.v(TAG, "当前状态，done");
	            break;
	        }
	    }

	    public void setonRefreshListener(OnRefreshListener refreshListener)
	    {
	        this.refreshListener = refreshListener;
	        isRefreshable = true;
	    }

	    public interface OnRefreshListener
	    {
	        public void onRefresh();
	    }

	    public void onRefreshComplete()
	    {   
	    	state = DONE;
	    	Time time = new Time();
			time.setToNow();
			int month = time.month + 1;
			int day = time.monthDay;
			int hour = time.hour;
			int minute = time.minute;
			if (minute<10) {
				 lastUpdatedTextView.setText("上次刷新时间:"+
							+ month + "-" + day + " " + hour + ":0" + minute);
			}else {
				 lastUpdatedTextView.setText("上次刷新时间:"+
							+ month + "-" + day + " " + hour + ":" + minute);
			}
	       
	        changeHeaderViewByState();
	    }

	    private void onRefresh()
	    {
	        if (refreshListener != null)
	        {
	            refreshListener.onRefresh();
	        }
	    }

	    // 此方法直接照搬自网络上的一个下拉刷新的demo，此处是“估计”headView的width以及height
	    @SuppressWarnings("deprecation")
		private void measureView(View child)
	    {
	        ViewGroup.LayoutParams p = child.getLayoutParams();
	        if (p == null)
	        {
	            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
	                    ViewGroup.LayoutParams.WRAP_CONTENT);
	        }
	        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
	        int lpHeight = p.height;
	        int childHeightSpec;
	        if (lpHeight > 0)
	        {
	            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
	                    MeasureSpec.EXACTLY);
	        }
	        else
	        {
	            childHeightSpec = MeasureSpec.makeMeasureSpec(0,
	                    MeasureSpec.UNSPECIFIED);
	        }
	        child.measure(childWidthSpec, childHeightSpec);
	    }

	    public void setAdapter(BaseAdapter adapter)
	    {
	    	state = DONE;
	    	Time time = new Time();
			time.setToNow();
			int month = time.month + 1;
			int day = time.monthDay;
			int hour = time.hour;
			int minute = time.minute;
			if (minute<10) {
				 lastUpdatedTextView.setText("上次刷新时间:"+
							+ month + "-" + day + " " + hour + ":0" + minute);
			}else {
				 lastUpdatedTextView.setText("上次刷新时间:"+
							+ month + "-" + day + " " + hour + ":" + minute);
			}
	        super.setAdapter(adapter);
	    }
	    
		public void GoShuaXin() {	
			headView.setPadding(0, 0, 0, 0);
	        progressBar.setVisibility(View.VISIBLE);
	        arrowImageView.clearAnimation();
	        arrowImageView.setVisibility(View.GONE);
	        tipsTextview.setText("正在刷新...");
	        lastUpdatedTextView.setVisibility(View.VISIBLE);	
		}
	    

}
