package com.ckl.zoompage;

import java.util.ArrayList;
import java.util.Observable;

import android.R.anim;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

public class ZoomState extends Observable {	
	
	//状态标志
	public static final class ControlType {
		public static final int UNKNOW = -1;
		public static final int MOVE = 0;
		public static final int ZOOM = 1;
	}
	
	private int mControlType = ControlType.UNKNOW;
	public int setControlType(int controlType) {
		int old = mControlType;
		mControlType = controlType;
		return old;
	}
	public int getControlType() {
		return mControlType;
	}

    private float mZoom, mMinZoom, mMaxZoom, mMidZoom;//控制缩放比例
    private float mStdMinZoom, mStdMaxZoom;
    private float mPanX, mMinPanX, mMaxPanX;//控制横行移动
    private float mStdMinPanX, mStdMaxPanX;
    private float mPanY, mMinPanY, mMaxPanY;//控制纵向移动
    private float mStdMinPanY, mStdMaxPanY;
    
    /**
     * mAspectQuotient 表示图片与VIEW的胖瘦关系
     * mAspectQuotient > 1时，图片比VIEW胖
     * mAspectQuotient < 1时，图片比VIEW瘦
     */
    private float mAspectQuotient;
    
    private ImageZoomView mView;
    private Bitmap mBitmap;
    
    public ZoomState() {
    	mPanX = 0.5f;
    	mPanY = 0.5f;
    }
    
    public float getPanX() {
        return mPanX = adjustPanX(mPanX);
    }

    public float getPanY() {
        return mPanY = adjustPanY(mPanY);
    }

    public float getZoom() {
        return mZoom = adjustZoom(mZoom);
    }

    public void setPanX(float panX) {
    	panX = adjustPanX(panX);
        if (!floatEquel(panX, mPanX)) {
            mPanX = panX;
            logd("----> mPanX = " + mPanX);
            setZoomChanged();
        }
    }

    public void setPanY(float panY) {
    	panY = adjustPanY(panY);
        if (!floatEquel(panY, mPanY)) {
            mPanY = panY;
            logd("----> mPanY = " + mPanY);
            setZoomChanged();
        }
    }
    
    public void setZoom(float zoom) {
    	zoom = adjustZoom(zoom);
        if (!floatEquel(zoom, mZoom)) {
            mZoom = zoom;
            logd("----> mZoom = " + mZoom);
            calculateMinMaxPanX();
            calculateMinMaxPanY();
            setZoomChanged();
        }
    }
    
    public float getZoomX() {
    	mZoom = adjustZoom(mZoom);
    	return Math.min(mZoom, mZoom * mAspectQuotient) * mView.getWidth() / mBitmap.getWidth();
    }

    public float getZoomY() {
    	mZoom = adjustZoom(mZoom);
    	return Math.min(mZoom, mZoom / mAspectQuotient) * mView.getHeight() / mBitmap.getHeight();
    }
    
    //--双击处理---------------------------------------------------------------
    public void doubleClick() {
    	float z = mZoom;
    	if (floatEquel(mZoom, mStdMinZoom)) {
    		z = mStdMaxZoom;
    	} else {
    		z = mStdMinZoom;
    	}
    	setZoom(z);
    	notifyObservers();
    }
    //--双击处理---------------------------------------------------------------
    
    //--回弹处理---------------------------------------------------------------
    private class AnimationFlag {
    	private static final int NoAnimation = 0x00;
    	private static final int ZOOM = 0x01;
    	private static final int PANX = 0x02;
    	private static final int PANY = 0x04;
    	private static final int MASK = ZOOM | PANX | PANY;
    }
    private int mAnimationFlag = AnimationFlag.NoAnimation;
    private float mNewZoom, mNewPanX, mNewPanY;
    private float mDeltaZoom, mDeltaPanX, mDeltaPanY;
    
    public void startReturnAnimation() {
    	int flag = AnimationFlag.NoAnimation;
    	
    	flag = calculateNewZoom(flag);
    	mAnimationFlag = calculateNewPanXY(flag);
    	setZoomChanged();
    	notifyObservers();
    	
    	//logd("******* mAnimationFlag = " + mAnimationFlag + " *******");
    	//logd("******* AnimationFlag.MASK = " + AnimationFlag.MASK + " *******");
    }
    
    private int calculateNewZoom(int flag) {
    	mNewZoom = mZoom;
    	if (mZoom < mStdMinZoom) {
    		mNewZoom = mStdMinZoom;
    		flag |= AnimationFlag.ZOOM;
    	} else if (mZoom > mStdMaxZoom) {
    		mNewZoom = mStdMaxZoom;
    		flag |= AnimationFlag.ZOOM;
    	}
    	mDeltaZoom = (mNewZoom - mZoom) / 3;
    	logd("mZoom = " + mZoom);
    	logd("mStdMinZoom,mStdMaxZoom =" + mStdMinZoom + "," + mStdMaxZoom);
    	
    	return flag;
    }
    
    private int calculateNewPanXY(int flag) {
    	
    	mNewPanX = mPanX;
    	if (mPanX < mStdMinPanX) {
    		mNewPanX = mStdMinPanX;
    		flag |= AnimationFlag.PANX;
    	} else if (mPanX > mStdMaxPanX) {
    		mNewPanX = mStdMaxPanX;
    		flag |= AnimationFlag.PANX;
    	}
    	mDeltaPanX = (mNewPanX - mPanX) / 3;
    	//logd("mPanX = " + mPanX);
    	//logd("mStdMinPanX,mStdMaxPanX =" + mStdMinPanX + "," + mStdMaxPanX);
    	
    	mNewPanY = mPanY;
    	if (mPanY < mStdMinPanY) {
    		mNewPanY = mStdMinPanY;
    		flag |= AnimationFlag.PANY;
    	} else if (mPanY > mStdMaxPanY) {
    		mNewPanY = mStdMaxPanY;
    		flag |= AnimationFlag.PANY;
    	}
    	mDeltaPanY = (mNewPanY - mPanY) / 3;
    	//logd("mPanY = " + mPanY);
    	//logd("mStdMinPanY,mStdMaxPanY =" + mStdMinPanY + "," + mStdMaxPanY);
    	
    	return flag;
    }
    
    public void drawReturnAnimation() {
    	//logd("******* drawReturnAnimation mAnimationFlag = " + mAnimationFlag + " *******");
    	if ((mAnimationFlag & AnimationFlag.MASK) != 0) {
    		if ((mAnimationFlag & AnimationFlag.ZOOM) != 0) {
    			setZoom(mZoom + mDeltaZoom);
    			//logd("mStdMinZoom,mStdMaxZoom =" + mStdMinZoom + "," + mStdMaxZoom);
    			if (mZoom >= mStdMinZoom && mZoom <= mStdMaxZoom) {
    				setZoom(mNewZoom);
    				mAnimationFlag &= ~AnimationFlag.ZOOM;
    			}
    			mAnimationFlag = calculateNewPanXY(mAnimationFlag);
    		}
    		
    		if ((mAnimationFlag & AnimationFlag.PANX) != 0) {
    			setPanX(mPanX + mDeltaPanX);
    			//logd("mStdMinPanX,mStdMaxPanX =" + mStdMinPanX + "," + mStdMaxPanX);
    			if (floatEquel(mStdMinPanX, mStdMaxPanX)) {
    				if (mPanX < mStdMinPanX) {
        				setPanX(mNewPanX);
        				mAnimationFlag &= ~AnimationFlag.PANX;
    				}
    			}
    			if (mPanX >= mStdMinPanX && mPanX <= mStdMaxPanX) {
    				setPanX(mNewPanX);
    				mAnimationFlag &= ~AnimationFlag.PANX;
    			}
    		}
    		
    		if ((mAnimationFlag & AnimationFlag.PANY) != 0) {
    			setPanY(mPanY + mDeltaPanY);
    			//logd("mStdMinPanY,mStdMaxPanY =" + mStdMinPanY + "," + mStdMaxPanY);
    			if (floatEquel(mStdMinPanY, mStdMaxPanY)) {
    				if (mPanY < mStdMinPanY) {
        				setPanY(mNewPanY);
        				mAnimationFlag &= ~AnimationFlag.PANY;
    				}
    			}
    			if (mPanY >= mStdMinPanY && mPanY <= mStdMaxPanY) {
    				setPanY(mNewPanY);
    				mAnimationFlag &= ~AnimationFlag.PANY;
    			}
    		}
    		
    		notifyObservers();
    	}
    }
    //--回弹处理---------------------------------------------------------------
    
    //--参数计算---------------------------------------------------------------
    public void resetZoomState(ImageZoomView view, Bitmap bitmap) {
    	mView = view;
    	mBitmap = bitmap;
    	mAspectQuotient = -1;
    	
    	do {
    		if (null == mView || null == mBitmap) {
				break;
			}
    		
    		if (   mView.getHeight() <= 0 
    			|| mView.getWidth() <= 0
    			|| mBitmap.getHeight() <= 0
    			|| mBitmap.getWidth() <= 0) {
				break;
			}
    		
            mAspectQuotient =
                (((float)mBitmap.getWidth()) / mBitmap.getHeight()) /
                (((float)mView.getWidth()) / mView.getHeight());
            
            logd("mAspectQuotient = " + mAspectQuotient);
            calculateMinMaxZoom();
            calculateMinMaxPanX();
            calculateMinMaxPanY();

            //初始化OK，通知外部，显示小图标
            setZoomChanged();
            notifyObservers();
    	}while(false);
    }
    
    private void calculateMinMaxZoom() {
    	if (mAspectQuotient > 0) {
    		float bmpW = mBitmap.getWidth();
    		float viewW = mView.getWidth();
    		float bmpH = mBitmap.getHeight();
    		float viewH = mView.getHeight();
    		
            if (mAspectQuotient > 1) {
            	mStdMinZoom = 1;
            	mMidZoom = mAspectQuotient;
            	mStdMaxZoom = bmpW / viewW;
            } else {
            	mStdMinZoom = 1;
            	mMidZoom = 1 / mAspectQuotient;
            	mStdMaxZoom = bmpH / viewH;
            }
            mZoom = mStdMinZoom;
            mMinZoom = mStdMinZoom * (1 - 0.35f);
            mMaxZoom = mStdMaxZoom * (1 + 0.2f);
            logd("mStdMinZoom,mStdMaxZoom =" + mStdMinZoom + "," + mStdMaxZoom);
            logd("mMinZoom,mMidZoom,mMaxZoom =" + mMinZoom + "," + mMidZoom + "," + mMaxZoom);
		}
    }
    
    private void calculateMinMaxPanX() {
    	if (mAspectQuotient > 0) {
    		float bmpW = mBitmap.getWidth();
    		float viewW = mView.getWidth();
    		
    		float zoomX = getZoomX();
			mStdMinPanX = viewW / (zoomX * 2 * bmpW);
			mStdMaxPanX = 1 - mStdMinPanX;
    		if (mAspectQuotient <= 1) {
				if (mZoom <= mMidZoom){//左右两边出现空隙时，居中显示
					mStdMinPanX = mStdMaxPanX = 0.5f;
				}
			} else {
				if (mZoom <= mStdMinZoom) {
					mStdMinPanX = mStdMaxPanX = 0.5f;
				}
			}
    		mMinPanX = (viewW / 2 + 10) / (zoomX * 2 * bmpW);
    		mMaxPanX = 1 - mMinPanX;
    		//logd("mStdMinPanX,mStdMaxPanX =" + mStdMinPanX + "," + mStdMaxPanX);
    		//logd("mMinPanX,mMaxPanX =" + mMinPanX + "," + mMaxPanX);
		}
    }
    
    private void calculateMinMaxPanY() {
    	if (mAspectQuotient > 0) {
    		float bmpH = mBitmap.getHeight();
    		float viewH = mView.getHeight();
    		
    		float zoomY = getZoomY();
			mStdMinPanY = viewH / (zoomY * 2 * bmpH);
			mStdMaxPanY = 1 - mStdMinPanY;
    		if (mAspectQuotient > 1) {
    			if (mZoom <= mMidZoom) {//上下两边出现空隙时，居中显示
					mStdMinPanY = mStdMaxPanY = 0.5f;
				}
			} else {
				if (mZoom <= mStdMinZoom) {
					mStdMinPanY = mStdMaxPanY = 0.5f;
				}
			}
    		mMinPanY = (viewH / 2 + 10) / (zoomY * 2 * bmpH);
    		mMaxPanY = 1 - mMinPanY;
    		logd("mStdMinPanY,mStdMaxPanY =" + mStdMinPanY + "," + mStdMaxPanY);
    		logd("mMinPanY,mMaxPanY =" + mMinPanY + "," + mMaxPanY);
		}
    }
    
    private float adjustZoom(float zoom) {
        if (zoom < mMinZoom) {
        	zoom = mMinZoom;
		} else if (zoom > mMaxZoom) {
			zoom = mMaxZoom;
		}
        return zoom;
    }
    
    private float adjustPanX(float panX) {
        if (panX < mMinPanX) {
        	panX = mMinPanX;
		} else if (panX > mMaxPanX) {
			panX = mMaxPanX;
		}
        return panX;
    }
    
    private float adjustPanY(float panY) {
        if (panY < mMinPanY) {
        	panY = mMinPanY;
		} else if (panY > mMaxPanY) {
			panY = mMaxPanY;
		}
        return panY;
    }
  //--参数计算---------------------------------------------------------------
    
    //控制是否需要重新计算区域
    private boolean mIsNeedCalculateRect = true;
    
    public boolean isNeedCalculateRect() {
    	return mIsNeedCalculateRect;
    }
    
    public void setEnableCalculateRect(boolean calculate) {
    	mIsNeedCalculateRect = calculate;
    }
    
    private void setZoomChanged() {
    	setChanged();
    	setEnableCalculateRect(true);
    }
    
    //坐标转换
    /**
     * 图片坐标转为屏幕坐标
     * 返回 屏幕坐标，当imageX或imageY超出图片范围时，返回值  < 0
     */
    public float toScreenX(float imageX) {
    	float screenX = -1;

    	//先计算一次，避免下面引用区域时不正确
    	mView.calculateRect();
    	Rect mRectSrc = mView.getSrcRect();
    	Rect mRectDst = mView.getDstRect();
    	
    	do {
        	//是否超出图片范围
    		if (imageX < 0 || imageX > mBitmap.getWidth()) {
    			break;
    		}
    		//转换
    		screenX = (imageX - mRectSrc.left) * getZoomX() + mRectDst.left;
    	}while(false);
    	
    	//logd("toScreenX " + imageX + " -> " + screenX);
    	
    	return screenX;
    }
    public float toScreenY(float imageY) {
    	float screenY = -1;
    	
    	mView.calculateRect();
    	Rect mRectSrc = mView.getSrcRect();
    	Rect mRectDst = mView.getDstRect();
    	
    	do {
    		if (imageY < 0 || imageY > mBitmap.getHeight()) {
    			break;
    		}
    		screenY = (imageY - mRectSrc.top) * getZoomY() + mRectDst.top;
    	}while(false);
    	
    	//logd("toScreenY " + imageY + " -> " + screenY);
    	
    	return screenY;
    }
    
    /**
     * 图片坐标转为屏幕坐标
     * 返回 屏幕坐标，当screenX或screenY超出图片范围时，返回值  < 0
     */
    public float toImageX(float screenX) {
    	float imageX = -1;

    	mView.calculateRect();
    	Rect mRectSrc = mView.getSrcRect();
    	Rect mRectDst = mView.getDstRect();
    	
    	do {
    		if (screenX < mRectDst.left || screenX > mRectDst.right) {
    			break;
    		}
    		imageX = (screenX - mRectDst.left) / getZoomX() + mRectSrc.left;
    	}while(false);
    	
    	//logd("toImageX " + screenX + " -> " + imageX);
    	
    	return imageX;
    }
    public float toImageY(float screenY) {
    	float imageY = -1;

    	mView.calculateRect();
    	Rect mRectSrc = mView.getSrcRect();
    	Rect mRectDst = mView.getDstRect();
    	
    	do {
    		if (screenY < mRectDst.top || screenY > mRectDst.bottom) {
    			break;
    		}
    		imageY = (screenY - mRectDst.top) / getZoomY() + mRectSrc.top;
    	}while(false);
    	
    	//logd("toImageY " + screenY + " -> " + imageY);
    	
    	return imageY;
    }
    
    //获取图片缩放后的尺寸
    public float getBitmapScaleWidth() {
    	return mBitmap.getWidth() * getZoomX();
    }
    public float getBitmapScaleHeight() {
    	return mBitmap.getHeight() * getZoomY();
    }
    
    private boolean floatEquel(float f1, float f2) {
    	return Math.abs(f1 - f2) < 0.00001f;
    }
    
    //调试
	private final boolean debugOn = true;
	private final String TAG = "ZoomState";
    private int logd(String msg) {
    	int retVal = 0;
    	if (debugOn) {
    		retVal = Log.i(TAG, msg);
		}
    	return retVal;
    }
    
    public void setPoints(ArrayList<Float> mXs, ArrayList<Float> mYs) {
    	mView.setPoints(mXs, mYs);
    }
}
