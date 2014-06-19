package com.svo.platform.utils;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * 图片工具类
 * @author duweibin
 * @version 创建时间：2013年9月6日  图片工具类
 */
public class PicUtil {
	/**
	 * 使用默认方法展示图片
	 * @param context
	 * @param uri 图片的uri.可接收的uri如下：
	 *  String imageUri = "http://site.com/image.png"; // 从网络加载
		String imageUri = "/mnt/sdcard/image.png"; // 从本地sd卡加载
		String imageUri = "content://media/external/audio/albumart/13"; // 从content provider中加载
		String imageUri = "assets://image.png"; // 从assets中加载
	 * @param imageView
	 */
	public static void displayImage(Context context,String uri, ImageView imageView)
	{
		displayImage(context, uri, imageView, new SimpleImageLoadingListener());
	}
	/**
	 * 带回调方法的图片展示方法
	 * @param context
	 * @param uri
	 * @param imageView
	 * @param imageLoadingListener 回调接口
	 */
	public static void displayImage(Context context,String uri, ImageView imageView,ImageLoadingListener imageLoadingListener)
	{
		displayImage(context, uri, imageView, imageLoadingListener,false);
	}
	/**
	* 带回调方法的图片展示方法
	 * @param context
	 * @param uri
	 * @param imageView
	 * @param imageLoadingListener 回调接口
	 * @param isFadeIn 展示图片时是否使用动画，默认不使用
	 */
	public static void displayImage(Context context,String uri, ImageView imageView,ImageLoadingListener imageLoadingListener,boolean isFadeIn)
	{
		ImageLoader imageLoader = ImageLoader.getInstance();
		if (!imageLoader.isInited()) {
			ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(context);
			imageLoader.init(config);
		}
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
		if (isFadeIn) {
			builder.displayer(new FadeInBitmapDisplayer(1200));
		}
		DisplayImageOptions options = builder.cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		if (!TextUtils.isEmpty(uri) && (uri.startsWith("/mnt/sdcard")||uri.startsWith("/storage"))) {
			uri = "file://".concat(uri);
		}
		imageLoader.displayImage(uri, imageView,options,imageLoadingListener);
	}
	public static void displayImage(Context context,String uri, ImageView imageView,boolean isFadeIn)
	{
		displayImage(context, uri, imageView, new SimpleImageLoadingListener(){},isFadeIn);
	}
	/**
	 * 根据图片的uri得到图片在sd卡中的路径。
	 * @param uri 图片的uri
	 * @return 以文件对象返回
	 */
	public static File getPicFile(String uri){
		ImageLoader imageLoader = ImageLoader.getInstance();
		return imageLoader.getDiscCache().get(uri);
	}
	 /**
	  * 解析文件对象得到位图
	  * @param file 文件对象
	  * @return 位图
	  */
    public static Bitmap parseBitmap(File file) {
    	Bitmap d;
    	try {
			d = BitmapFactory.decodeFile(file.getAbsolutePath());
		} catch (Throwable e) {
			System.err.println("PicUtil   内存溢出");
			try {
				d = decodeScaledBitmap(file.getAbsolutePath(),4);
			} catch (Throwable e1) {
				try {
				d = decodeScaledBitmap(file.getAbsolutePath(),8);
				} catch (Throwable e2) {
					System.err.println("PicUtil 内存溢出2");
					d = null;
				}
			}
		}
    	return d;
	}
    /**
     * 得到图片的位图
     * @param filename 文件名
     * @param scale 缩小值.2表示长和宽各缩小1/2
     * @return Bitmap
     */
	public static Bitmap decodeScaledBitmap(String filename,int scale) {

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filename, options);
		options.inSampleSize = scale;
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filename, options);
	}
}
