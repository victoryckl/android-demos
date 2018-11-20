package com.example.screenshot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.screenshot.ShellUtils.*;

/**
 * 截屏
 * 需要系统签名，和android:sharedUserId="android.uid.system"
 * 否则会失败 E/SurfaceFlinger: Permission Denial: can't read framebuffer
 *
 * 代码来自 https://github.com/Android-ScreenShot/AndroidScreenShotService
 * 稍有改动，目前在android7.1上测试成功
 */

public class ScreenShotUtil
{
	private static final String TAG = "ScreenShotUtil";
	private static final String CLASS1_NAME = "android.view.SurfaceControl";
	private static final String CLASS2_NAME = "android.view.Surface";
	private static final String METHOD_NAME = "screenshot";
	private static ScreenShotUtil instance;
	private Display mDisplay;
	private DisplayMetrics mDisplayMetrics;
	private Matrix mDisplayMatrix;
	private WindowManager wm;
	private SimpleDateFormat format;

	private ScreenShotUtil() {}

	public static ScreenShotUtil getInstance() {
		synchronized (ScreenShotUtil.class) {
			if (instance == null) {
				instance = new ScreenShotUtil();
			}
		}
		return instance;
	}

	private Bitmap screenShot(int width, int height) {
		Log.i(TAG, "android.os.Build.VERSION.SDK : " + android.os.Build.VERSION.SDK_INT);
		Class<?> surfaceClass = null;
		Method method = null;
		try {
			Log.i(TAG, "width : " + width);
			Log.i(TAG, "height : " + height);
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
				surfaceClass = Class.forName(CLASS1_NAME);
			} else {
				surfaceClass = Class.forName(CLASS2_NAME);
			}
			method = surfaceClass.getDeclaredMethod(METHOD_NAME, int.class, int.class);
			method.setAccessible(true);
			return (Bitmap) method.invoke(null, width, height);
		} catch (NoSuchMethodException e) {
			Log.e(TAG, e.toString());
		} catch (IllegalArgumentException e) {
			Log.e(TAG, e.toString());
		} catch (IllegalAccessException e) {
			Log.e(TAG, e.toString());
		} catch (InvocationTargetException e) {
			Log.e(TAG, e.toString());
		} catch (ClassNotFoundException e) {
			Log.e(TAG, e.toString());
		}
		return null;
	}

	/**
	 * Takes a screenshot of the current display.
     * return png path, null if fail
	 */
	@SuppressLint("NewApi")
	public String takeScreenshot(Context context, String fileFullPath)
	{
		if(fileFullPath == null || fileFullPath.isEmpty()) {
			format = new SimpleDateFormat("yyyyMMddHHmmss");
			String fileName = format.format(new Date(System.currentTimeMillis())) + ".png";
			fileFullPath = "/data/local/tmp/" + fileName;
		}
		
		if(ShellUtils.checkRootPermission()){
			if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH){
                CommandResult result = ShellUtils.execCommand("/system/bin/screencap -p "+ fileFullPath,true);
                if (result.result != 0) {
                    Toast.makeText(context, "screen shot fail", Toast.LENGTH_SHORT).show();
                    return null;
                }
			}
		} else {
			if(/*android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2 &&*/
					android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH){
				wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
				mDisplay = wm.getDefaultDisplay();
				mDisplayMatrix = new Matrix();
				mDisplayMetrics = new DisplayMetrics();
				// We need to orient the screenshot correctly (and the Surface api seems to take screenshots
				// only in the natural orientation of the device :!)
				mDisplay.getRealMetrics(mDisplayMetrics);
				float[] dims = {
						mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels
				};
				float degrees = getDegreesForRotation(mDisplay.getRotation());
				boolean requiresRotation = (degrees > 0);
				if (requiresRotation) {
					// Get the dimensions of the device in its native orientation
					mDisplayMatrix.reset();
					mDisplayMatrix.preRotate(-degrees);
					mDisplayMatrix.mapPoints(dims);
					dims[0] = Math.abs(dims[0]);
					dims[1] = Math.abs(dims[1]);
				}

				Bitmap mScreenBitmap = screenShot((int) dims[0], (int) dims[1]);
				if (mScreenBitmap == null) {
                    Toast.makeText(context, "screen shot fail", Toast.LENGTH_SHORT).show();
                    return null;
                }
				if (requiresRotation) {
					// Rotate the screenshot to the current orientation
					Bitmap ss = Bitmap.createBitmap(mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels,
							Bitmap.Config.ARGB_8888);
					Canvas c = new Canvas(ss);
					c.translate(ss.getWidth() / 2, ss.getHeight() / 2);
					c.rotate(degrees);
					c.translate(-dims[0] / 2, -dims[1] / 2);
					c.drawBitmap(mScreenBitmap, 0, 0, null);
					c.setBitmap(null);
					if (mScreenBitmap != null && !mScreenBitmap.isRecycled()) {
						mScreenBitmap.recycle();
					}
					mScreenBitmap = ss;
				}

				// Optimizations
				mScreenBitmap.setHasAlpha(false);
				mScreenBitmap.prepareToDraw();

				return saveBitmap2file(mScreenBitmap, fileFullPath);
			} else {
				Log.e(TAG, "not support, request Build.VERSION >= "+android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH);
			}
		}
        return null;
	}

	private String saveBitmap2file(Bitmap bmp, String fileName) {
		File file = new File(fileName);
		File dir = file.getParentFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (file.exists()) {
			file.delete();
		}

		int quality = 100;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, quality, baos);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		byte[] buffer = new byte[10*1024];

		FileOutputStream os = null;
		try {
			int len;
			os = new FileOutputStream(file);
			while ((len = is.read(buffer)) != -1) {
				os.write(buffer, 0, len);
			}
			os.flush();
		} catch (FileNotFoundException e) {
			Log.i(TAG, e.toString());
            return null;
		} catch (IOException e) {
			Log.i(TAG, e.toString());
			return null;
		} finally {
			try { if (is != null) { is.close(); } } catch (IOException e) {}
			try { if (os != null) { os.close(); } } catch (IOException e) {}
		}
		if (bmp != null && !bmp.isRecycled()) {
			bmp.recycle();
		}
		return fileName;
	}

	/**
	 * @return the current display rotation in degrees
	 */
	private float getDegreesForRotation(int value) {
		switch (value) {
			case Surface.ROTATION_90:
				return 360f - 90f;
			case Surface.ROTATION_180:
				return 360f - 180f;
			case Surface.ROTATION_270:
				return 360f - 270f;
		}
		return 0f;
	}
}
