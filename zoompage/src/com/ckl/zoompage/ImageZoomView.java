package com.ckl.zoompage;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ImageZoomView extends View implements Observer {

	private final boolean debugOn = true;
	private final String TAG = "ImageZoomView";
	private final Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
    private final Rect mRectSrc = new Rect();
    private final Rect mRectDst = new Rect();
    
	private Bitmap mBitmap;
    private ZoomState mState;

    public ImageZoomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setZoomState(ZoomState state) {
        if (mState != null) {
            mState.deleteObserver(this);
        }
        mState = state;
        mState.addObserver(this);
        invalidate();
    }
    
    protected void onDraw(Canvas canvas) {
    	if (mBitmap != null && mState != null) {
    		//计算图片映射区域和输出区域
    		calculateRect();
            //绘图
            canvas.drawBitmap(mBitmap, mRectSrc, mRectDst, mPaint);
            //绘制点击信息，用于调试
            drawTouchPiont(canvas);
            //回弹
            mState.drawReturnAnimation();
    	}
    }

    //计算图片映射区域和输出区域
    public void calculateRect() {
    	if (mBitmap != null && mState != null) {
    		if (mState.isNeedCalculateRect()) {
	            final int viewWidth = getWidth();
	            final int viewHeight = getHeight();
	            final int bitmapWidth = mBitmap.getWidth();
	            final int bitmapHeight = mBitmap.getHeight();
	
	            final float panX = mState.getPanX();
	            final float panY = mState.getPanY();
	            final float zoomX = mState.getZoomX();
	            final float zoomY = mState.getZoomY();
	
	            // 计算图片映射区域和输出区域
	            mRectSrc.left = (int)(panX * bitmapWidth - viewWidth / (zoomX * 2));
	            mRectSrc.top = (int)(panY * bitmapHeight - viewHeight / (zoomY * 2));
	            mRectSrc.right = (int)(mRectSrc.left + viewWidth / zoomX);
	            mRectSrc.bottom = (int)(mRectSrc.top + viewHeight / zoomY);
	            mRectDst.left = getLeft();
	            mRectDst.top = getTop();
	            mRectDst.right = getRight();
	            mRectDst.bottom = getBottom();
	            
//				logd("<-------------------------------------------->");
//				logd("view:w,h = " + viewWidth + "," + viewHeight);
//				logd("bmp:w,h = " + bitmapWidth + "," + bitmapHeight);
//				logd("zoomX,zoomY = " + zoomX + "," + zoomY);
//				logd("panX,panY = " + panX + "," + panY);
//				logd("mRectSrc(" + mRectSrc.left + "," + mRectSrc.top + ","
//						+ mRectSrc.right + "," + mRectSrc.bottom + ")");
//				logd("mRectDst(" + mRectDst.left + "," + mRectDst.top + ","
//						+ mRectDst.right + "," + mRectDst.bottom + ")");
	            
	            // 调整图片映射区域和输出区域.
	            if (mRectSrc.left < 0) {
	                mRectDst.left += -mRectSrc.left * zoomX;
	                mRectSrc.left = 0;
	            }
	            if (mRectSrc.right > bitmapWidth) {
	                mRectDst.right -= (mRectSrc.right - bitmapWidth) * zoomX;
	                mRectSrc.right = bitmapWidth;
	            }
	            if (mRectSrc.top < 0) {
	                mRectDst.top += -mRectSrc.top * zoomY;
	                mRectSrc.top = 0;
	            }
	            if (mRectSrc.bottom > bitmapHeight) {
	                mRectDst.bottom -= (mRectSrc.bottom - bitmapHeight) * zoomY;
	                mRectSrc.bottom = bitmapHeight;
	            }
	
//				logd("mRectSrc(" + mRectSrc.left + "," + mRectSrc.top + ","
//						+ mRectSrc.right + "," + mRectSrc.bottom + ")");
//				logd("mRectDst(" + mRectDst.left + "," + mRectDst.top + ","
//						+ mRectDst.right + "," + mRectDst.bottom + ")");
				
	            mState.setEnableCalculateRect(false);
			}
    	}
    }
    
    public void update(Observable observable, Object data) {
        invalidate();
    }
    
    private void calculateAspectQuotient() {
    	if (mState != null) {
    		mState.resetZoomState(this, mBitmap);
		}
    }

    public void setImage(Bitmap bitmap) {
        mBitmap = bitmap;
        calculateAspectQuotient();
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        calculateAspectQuotient();
    }
    
    public Rect getSrcRect() {
    	return mRectSrc;
//    	return new Rect(mRectSrc);
    }
    
    public Rect getDstRect() {
    	return mRectDst;
//    	return new Rect(mRectDst);
    }
    
    //调试
    private int logd(String msg) {
    	int retVal = 0;
    	if (debugOn) {
    		retVal = Log.i(TAG, msg);
		}
    	return retVal;
    }
    
    //绘制触摸点
    ArrayList<Float> mXs = null, mYs = null;
    private boolean mDrawn = true;
    private Paint mPointPaint = null;
    private Paint mRectPaint = null;
    private Paint mTextPaint = null;
    public void setPoints(ArrayList<Float> mXs, ArrayList<Float> mYs) {
    	if (debugOn) {
        	if (mPointPaint == null) {
        		mPointPaint = new Paint();
        		mPointPaint.setAntiAlias(false);
        		mPointPaint.setARGB(255, 0, 96, 255);
        		
        		mRectPaint = new Paint();
        		mRectPaint.setARGB(0x88, 0x44, 0x44, 0x44);
        		
        		mTextPaint = new Paint();
        		mTextPaint.setTextSize(15);
        		mTextPaint.setARGB(0xff, 0xff, 0xff, 0xff);
    		}
        	this.mXs = mXs;
        	this.mYs = mYs;
        	mDrawn = false;
        	invalidate();
		}
    }
    
    private void drawTouchPiont(Canvas canvas) {
    	if (debugOn) {
	    	if (!mDrawn) {
	    		float x,y, rx, ry;
	    		float dx = 80, dy = 80;
	    		for (int i = 0; i < mXs.size(); i++) {
	    			x = mXs.get(i);
	    			y = mYs.get(i);
		            canvas.drawLine(x, y - dy, x, y + dy, mPointPaint);
		            canvas.drawLine(x - dx, y, x + dx, y, mPointPaint);
		            
		            rx = x;
		            ry = y - 40;
		            if (x + 75 > getRight()) {
		            	rx = x - 76;
					}
		            if (ry < getTop()) {
		            	ry = y + 20;
					}
		            
		            canvas.drawRect(rx, ry, rx + 75, ry + 20, mRectPaint);
		            canvas.drawText("" + (int)x + "," + (int)y, rx, ry + 15, mTextPaint);
				}
	            mDrawn = true;
			}
    	}
    }
}
