package com.example.draggridview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.utils.MLog;

@SuppressLint("NewApi")
public class DragGridView extends GridView{
	/**
	 * DragGridView的item长按响应的时间， 默认是1000毫秒，也可以自行设置
	 */
	private long dragResponseMS = 1000;
	
	/**
	 * 是否可以拖拽，默认不可以
	 */
	private boolean isDrag = false;
	
	private int mDownX;
	private int mDownY;
	private int moveX;
	private int moveY;
	/**
	 * 正在拖拽的position
	 */
	private int mDragPosition;
	
	/**
	 * 刚开始拖拽的item对应的View
	 */
	private View mStartDragItemView = null;
	
	/**
	 * 用于拖拽的镜像，这里直接用一个ImageView
	 */
	private ImageView mDragImageView;
	
	/**
	 * 震动器
	 */
	private Vibrator mVibrator;
	
	private WindowManager mWindowManager;
	/**
	 * item镜像的布局参数
	 */
	private WindowManager.LayoutParams mWindowLayoutParams;
	
	/**
	 * 我们拖拽的item对应的Bitmap
	 */
	private Bitmap mDragBitmap;
	
	/**
	 * 按下的点到所在item的上边缘的距离
	 */
	private int mPoint2ItemTop ; 
	
	/**
	 * 按下的点到所在item的左边缘的距离
	 */
	private int mPoint2ItemLeft;
	
	/**
	 * DragGridView距离屏幕顶部的偏移量
	 */
	private int mOffset2Top;
	
	/**
	 * DragGridView距离屏幕左边的偏移量
	 */
	private int mOffset2Left;
	
	/**
	 * 状态栏的高度
	 */
	private int mStatusHeight; 
	
	/**
	 * DragGridView自动向下滚动的边界值
	 */
	private int mDownScrollBorder;
	
	/**
	 * DragGridView自动向上滚动的边界值
	 */
	private int mUpScrollBorder;
	
	/**
	 * DragGridView自动滚动的速度
	 */
	private static final int speed = 10;
	
	/**
	 * item发生变化回调的接口
	 */
	private OnChanageListener mOnChanageListener;
	
	private int mDelta; 
	
	
	public DragGridView(Context context) {
		this(context, null);
	}
	
	public DragGridView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DragGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		mStatusHeight = getStatusHeight(context); //获取状态栏的高度
		mDelta = context.getResources().getDimensionPixelSize(R.dimen.max_delta);
	}
	
	private Handler mHandler = new Handler();
	
	//用来处理是否为长按的Runnable
	private Runnable mLongClickRunnable = new Runnable() {
		@Override
		public void run() {
			if (mOnChanageListener != null) {
				mOnChanageListener.onStartDrag();
			}
			
			isDrag = true; //设置可以拖拽
			mVibrator.vibrate(50); //震动一下
			
			mStartDragItemView.setVisibility(View.INVISIBLE);//隐藏该item
			//根据我们按下的点显示item镜像
			createDragImage(mDragBitmap, mDownX, mDownY);
			
			//解决进入了拖动状态，在没有Move前就松开后的异常问题
			//这里模拟一次move
			MotionEvent ev = MotionEvent.obtain(
					SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 
					MotionEvent.ACTION_MOVE, 
					mDownX, mDownY, 0);
			dispatchTouchEvent(ev);
		}
	};
	
	/**
	 * 设置回调接口
	 * @param listener
	 */
	public void setOnChangeListener(OnChanageListener listener){
		mOnChanageListener = listener;
	}
	
	/**
	 * 设置响应拖拽的毫秒数，默认是1000毫秒
	 * @param dragResponseMS
	 */
	public void setDragResponseMS(long dragResponseMS) {
		this.dragResponseMS = dragResponseMS;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			MLog.i("onInterceptTouchEvent(), down");
		} else if (action == MotionEvent.ACTION_UP) {
			MLog.i("onInterceptTouchEvent(), up");
		}
		
		if (isDrag) {
			return true;
		}
		return super.onInterceptTouchEvent(ev);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch(ev.getAction()){
		case MotionEvent.ACTION_DOWN:
			MLog.i("----------------------");
			MLog.i("dispatchTouchEvent(), down");
			
			mDownX = (int) ev.getX();
			mDownY = (int) ev.getY();
			
			//根据按下的X,Y坐标获取所点击item的position
			mDragPosition = pointToPosition(mDownX, mDownY);
			MLog.i("mDragPosition: "+mDragPosition);
			
			if(mDragPosition == AdapterView.INVALID_POSITION){
				return super.dispatchTouchEvent(ev);
			}
			
			//使用Handler延迟dragResponseMS执行mLongClickRunnable
			mHandler.postDelayed(mLongClickRunnable, dragResponseMS);
			
			//根据position获取该item所对应的View
			mStartDragItemView = getChildAt(getIndex(mDragPosition));
			initAnimArgs(mStartDragItemView);
			
			//下面这几个距离大家可以参考我的博客上面的图来理解下
			mPoint2ItemTop = mDownY - mStartDragItemView.getTop();
			mPoint2ItemLeft = mDownX - mStartDragItemView.getLeft();
			
			mOffset2Top = (int) (ev.getRawY() - mDownY);
			mOffset2Left = (int) (ev.getRawX() - mDownX);
			
			//获取DragGridView自动向上滚动的偏移量，小于这个值，DragGridView向下滚动
			mDownScrollBorder = getHeight() /4;
			//获取DragGridView自动向下滚动的偏移量，大于这个值，DragGridView向上滚动
			mUpScrollBorder = getHeight() * 3/4;
			
			//开启mDragItemView绘图缓存
			mStartDragItemView.setDrawingCacheEnabled(true);
			//获取mDragItemView在缓存中的Bitmap对象
			mDragBitmap = Bitmap.createBitmap(mStartDragItemView.getDrawingCache());
			//这一步很关键，释放绘图缓存，避免出现重复的镜像
			mStartDragItemView.destroyDrawingCache();
			
			break;
		case MotionEvent.ACTION_MOVE:
//			MLog.i("dispatchTouchEvent(), move");
			int moveX = (int)ev.getX();
			int moveY = (int) ev.getY();
			
			//如果我们在按下的item上面移动，只要不超过item的边界我们就不移除mRunnable
			if(!isTouchInItem(mStartDragItemView, moveX, moveY)){
				mHandler.removeCallbacks(mLongClickRunnable);
			}
			if (!isMoveSmall(mDownX, mDownY, moveX, moveY)) {
				mHandler.removeCallbacks(mLongClickRunnable);
			}
			break;
		case MotionEvent.ACTION_UP:
			MLog.i("dispatchTouchEvent(), up");
			mHandler.removeCallbacks(mLongClickRunnable);
			mHandler.removeCallbacks(mScrollRunnable);
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	private boolean isMoveSmall(int orgX, int orgY, int newX, int newY) {
		boolean small = true;
		if ((Math.abs(orgX-newX) > mDelta)
				|| (Math.abs(orgY-newY) > mDelta)) {
			small = false;
		}
		return small;
	}
	
	/**
	 * 是否点击在GridView的item上面
	 * @param itemView
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean isTouchInItem(View dragView, int x, int y){
		if (dragView == null) {
			return false;
		}
		
		int leftOffset = dragView.getLeft();
		int topOffset = dragView.getTop();
		if(x < leftOffset || x > leftOffset + dragView.getWidth()){
			return false;
		}
		
		if(y < topOffset || y > topOffset + dragView.getHeight()){
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
//		MLog.i("isDrag: " + isDrag);
		if(isDrag && mDragImageView != null){
			switch(ev.getAction()){
			case MotionEvent.ACTION_DOWN:
				MLog.i("onTouchEvent(), down");
				break;
			case MotionEvent.ACTION_MOVE:
//				MLog.i("onTouchEvent(), move");
				moveX = (int) ev.getX();
				moveY = (int) ev.getY();
				//拖动item
				onDragItem(moveX, moveY);
				break;
			case MotionEvent.ACTION_UP:
				MLog.i("onTouchEvent(), up");
				onStopDrag();
				isDrag = false;
				break;
			}
			return true;
		}
		return super.onTouchEvent(ev);
	}
	
	/**
	 * 创建拖动的镜像
	 * @param bitmap 
	 * @param downX
	 * 			按下的点相对父控件的X坐标
	 * @param downY
	 * 			按下的点相对父控件的X坐标
	 */
	private void createDragImage(Bitmap bitmap, int downX , int downY){
		mWindowLayoutParams = new WindowManager.LayoutParams();
		mWindowLayoutParams.format = PixelFormat.TRANSLUCENT; //图片之外的其他地方透明
		mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
		mWindowLayoutParams.x = downX - mPoint2ItemLeft + mOffset2Left;
		mWindowLayoutParams.y = downY - mPoint2ItemTop + mOffset2Top - mStatusHeight;
		mWindowLayoutParams.alpha = 0.55f; //透明度
		mWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;  
		mWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;  
		mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE  
	                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE ;
		  
		mDragImageView = new ImageView(getContext());  
		mDragImageView.setImageBitmap(bitmap);  
		mWindowManager.addView(mDragImageView, mWindowLayoutParams);  
	}
	
	/**
	 * 从界面上面移动拖动镜像
	 */
	private void removeDragImage(){
		if(mDragImageView != null){
			mWindowManager.removeView(mDragImageView);
			mDragImageView = null;
		}
	}
	
	/**
	 * 拖动item，在里面实现了item镜像的位置更新，item的相互交换以及GridView的自行滚动
	 * @param x
	 * @param y
	 */
	private void onDragItem(int moveX, int moveY){
		mWindowLayoutParams.x = moveX - mPoint2ItemLeft + mOffset2Left;
		mWindowLayoutParams.y = moveY - mPoint2ItemTop + mOffset2Top - mStatusHeight;
		mWindowManager.updateViewLayout(mDragImageView, mWindowLayoutParams); //更新镜像的位置
		onSwapItem(moveX, moveY);
		
		//GridView自动滚动
		mHandler.post(mScrollRunnable);
	}
	
	/**
	 * 当moveY的值大于向上滚动的边界值，触发GridView自动向上滚动
	 * 当moveY的值小于向下滚动的边界值，触犯GridView自动向下滚动
	 * 否则不进行滚动
	 */
	private Runnable mScrollRunnable = new Runnable() {
		
		@Override
		public void run() {
			int scrollY;
			if(moveY > mUpScrollBorder){
				 scrollY = -speed;
				 mHandler.postDelayed(mScrollRunnable, 25);
			}else if(moveY < mDownScrollBorder){
				scrollY = speed;
				 mHandler.postDelayed(mScrollRunnable, 25);
			}else{
				scrollY = 0;
				mHandler.removeCallbacks(mScrollRunnable);
			}
			
			//当我们的手指到达GridView向上或者向下滚动的偏移量的时候，可能我们手指没有移动，但是DragGridView在自动的滚动
			//所以我们在这里调用下onSwapItem()方法来交换item
			onSwapItem(moveX, moveY);
			
//			smoothScrollToPositionFromTop(mDragPosition, view.getTop() + scrollY);
			
			//实现GridView的自动滚动
			smoothScrollBy(-scrollY, 10);
		}
	};
	
	/**
	 * 交换item,并且控制item之间的显示与隐藏效果
	 * @param moveX
	 * @param moveY
	 */
	private void onSwapItem(int moveX, int moveY){
		//获取我们手指移动到的那个item的position
		int tempPosition = pointToPosition(moveX, moveY);
		
		//假如tempPosition 改变了并且tempPosition不等于-1,则进行交换
		if(tempPosition != mDragPosition && tempPosition != AdapterView.INVALID_POSITION){
//			setChildVisible(tempPosition, View.INVISIBLE);//拖动到了新的item,新的item隐藏掉
//			setChildVisible(mDragPosition, View.VISIBLE);//之前的item显示出来
			
//			if(mOnChanageListener != null){
//				mOnChanageListener.onChange(mDragPosition, tempPosition);
//			}
//			mDragPosition = tempPosition;

			doItemAnimation(mDragPosition, tempPosition);
		}
	}
	
	/**
	 * 停止拖拽我们将之前隐藏的item显示出来，并将镜像移除
	 */
	private void onStopDrag(){
		if (mOnChanageListener != null) {
			mOnChanageListener.onStopDrag();
		}
		setChildVisible(mDragPosition, View.VISIBLE);
		removeDragImage();
	}
	
	/**
	 * 获取状态栏的高度
	 * @param context
	 * @return
	 */
	private static int getStatusHeight(Context context){
        int statusHeight = 0;
        Rect localRect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight){
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            } 
        }
        return statusHeight;
    }
	
	private void setChildVisible(int position, int visable) {
		getChildAt(getIndex(position)).setVisibility(visable);
	}
	
	private int getIndex(int position) {
		return position - getFirstVisiblePosition();
	}
	
	public interface OnChanageListener{
		void onStartDrag();
		void onStopDrag();
		
		/**
		 * 当item交换位置的时候回调的方法，我们只需要在该方法中实现数据的交换即可
		 * @param from
		 * 			开始的position
		 * @param to 
		 * 			拖拽到的position
		 */
		void onChange(int from, int to);
	}
	
	//-----------------------------
	private static final int ANIMATION_TIME = 300;
	private boolean isMoving;
	private int mColumns;
	private int mTempPosition;
	private String LastAnimationID;
	private float mXscale, mYscale;
	
	private void initAnimArgs(View item) {
		isMoving = false;
		mTempPosition = -1;
		mColumns = getNumColumns();
		
		mXscale = 1.0f + (1.0f*getHorizontalSpacing() / item.getWidth());
		mYscale = 1.0f + (1.0f*getVerticalSpacing() / item.getHeight());
	}
	
	private void doItemAnimation(int from, int to) {
		int moveNum;
		int dst, src;
		int vector;
		View item = null;
		Animation anim = null;
		
		if (isMoving) {
			return;
		}
		
		moveNum = to - from;
		if (moveNum == 0) {
			return;
		}
		
		vector = moveNum>0 ? 1 : -1;

		moveNum = Math.abs(moveNum);
		dst = from;
		for (int i=0; i<moveNum; i++) {
			src = dst + vector;
			MLog.i("" + src + " -> " + dst);
			item = getChildAt(getIndex(src)); 
			anim = startItemAnimation(src, dst);
			item.startAnimation(anim);
			dst = src;
		}
		LastAnimationID = anim.toString();
		mTempPosition = to;
	}
	
    public Animation startItemAnimation(int src, int dst){
    	float Xoffset,Yoffset;
    	
    	Xoffset = ((dst % mColumns) - (src % mColumns))*mXscale;
    	Yoffset = ((dst / mColumns) - (src / mColumns))*mYscale;
    	
        TranslateAnimation anim = new TranslateAnimation(
        		Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, Xoffset, 
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, Yoffset);
        //是否执行终止填充效果，true表示使能该效果，false表示禁用该效果。    
        anim.setFillAfter(false);
        anim.setDuration(ANIMATION_TIME);
        anim.setAnimationListener(mAnimListener);
        return anim;
    }
    
	private AnimationListener mAnimListener = new Animation.AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			isMoving = true;
		}

		@Override
		public void onAnimationRepeat(Animation animation) {}

		@Override
		public void onAnimationEnd(Animation animation) {
			String animaionID = animation.toString();
			if (animaionID.equalsIgnoreCase(LastAnimationID)) {
				// adapter.exchange(startPosition, dropPosition);
				// startPosition = dropPosition;
				if (mOnChanageListener != null) {
					mOnChanageListener.onChange(mDragPosition, mTempPosition);
				}
				setChildVisible(mTempPosition, View.INVISIBLE);//拖动到了新的item,新的item隐藏掉
				setChildVisible(mDragPosition, View.VISIBLE);//之前的item显示出来
				mDragPosition = mTempPosition;
				isMoving = false;
			}
		}
	};
}
