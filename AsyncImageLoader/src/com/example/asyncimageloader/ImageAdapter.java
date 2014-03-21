package com.example.asyncimageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.asyncimageloader.ImageDownLoader.onImageLoaderListener;

public class ImageAdapter extends BaseAdapter implements OnScrollListener{
	/**
	 * 上下文对象的引用
	 */
	private Context context;
	
	/**
	 * Image Url的数组
	 */
	private String [] imageThumbUrls;
	
	/**
	 * GridView对象的应用
	 */
	private GridView mGridView;
	
	/**
	 * Image 下载器
	 */
	private ImageDownLoader mImageDownLoader;
	
	/**
	 * 记录是否刚打开程序，用于解决进入程序不滚动屏幕，不会下载图片的问题。
	 * 参考http://blog.csdn.net/guolin_blog/article/details/9526203#comments
	 */
	private boolean isFirstEnter = true;
	
	/**
	 * 一屏中第一个item的位置
	 */
	private int mFirstVisibleItem;
	
	/**
	 * 一屏中所有item的个数
	 */
	private int mVisibleItemCount;
	
	
	public ImageAdapter(Context context, GridView mGridView, String [] imageThumbUrls){
		this.context = context;
		this.mGridView = mGridView;
		this.imageThumbUrls = imageThumbUrls;
		mImageDownLoader = new ImageDownLoader(context);
		mGridView.setOnScrollListener(this);
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		//仅当GridView静止时才去下载图片，GridView滑动时取消所有正在下载的任务  
		if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
			showImage(mFirstVisibleItem, mVisibleItemCount);
		}else{
			cancelTask();
		}
		
	}


	/**
	 * GridView滚动的时候调用的方法，刚开始显示GridView也会调用此方法
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mFirstVisibleItem = firstVisibleItem;
		mVisibleItemCount = visibleItemCount;
		// 因此在这里为首次进入程序开启下载任务。 
		if(isFirstEnter && visibleItemCount > 0){
			showImage(mFirstVisibleItem, mVisibleItemCount);
			isFirstEnter = false;
		}
	}
	

	@Override
	public int getCount() {
		return imageThumbUrls.length;
	}

	@Override
	public Object getItem(int position) {
		return imageThumbUrls[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView mImageView;
		final String mImageUrl = imageThumbUrls[position];
		if(convertView == null){
			mImageView = new ImageView(context);
		}else{
			mImageView = (ImageView) convertView;
		}
		
		mImageView.setLayoutParams(new GridView.LayoutParams(150, 150));
		mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		
		//给ImageView设置Tag,这里已经是司空见惯了
		mImageView.setTag(mImageUrl);
		
		
		/*******************************去掉下面这几行试试是什么效果****************************/
		Bitmap bitmap = mImageDownLoader.showCacheBitmap(mImageUrl.replaceAll("[^\\w]", ""));
		if(bitmap != null){
			mImageView.setImageBitmap(bitmap);
		}else{
			mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_empty));
		}
		/**********************************************************************************/
		
		
		return mImageView;
	}
	
	/**
	 * 显示当前屏幕的图片，先会去查找LruCache，LruCache没有就去sd卡或者手机目录查找，在没有就开启线程去下载
	 * @param firstVisibleItem
	 * @param visibleItemCount
	 */
	private void showImage(int firstVisibleItem, int visibleItemCount){
		Bitmap bitmap = null;
		for(int i=firstVisibleItem; i<firstVisibleItem + visibleItemCount; i++){
			String mImageUrl = imageThumbUrls[i];
			final ImageView mImageView = (ImageView) mGridView.findViewWithTag(mImageUrl);
			bitmap = mImageDownLoader.downloadImage(mImageUrl, new onImageLoaderListener() {
				
				@Override
				public void onImageLoader(Bitmap bitmap, String url) {
					if(mImageView != null && bitmap != null){
						mImageView.setImageBitmap(bitmap);
					}
					
				}
			});
			
			if(bitmap != null){
				mImageView.setImageBitmap(bitmap);
			}else{
				mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_empty));
			}
		}
	}

	/**
	 * 取消下载任务
	 */
	public void cancelTask(){
		mImageDownLoader.cancelTask();
	}


}
