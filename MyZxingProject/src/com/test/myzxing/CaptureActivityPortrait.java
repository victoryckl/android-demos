package com.test.myzxing;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.CaptureActivityHandler;
import com.google.zxing.client.android.FinishListener;
import com.google.zxing.client.android.InactivityTimer;
import com.google.zxing.client.android.ViewfinderView;
import com.google.zxing.client.android.camera.CameraManager;

public final class CaptureActivityPortrait extends Activity implements
		SurfaceHolder.Callback {

	private static final float BEEP_VOLUME = 0.10f;
	private static final long VIBRATE_DURATION = 200L;
	private String tag = CaptureActivityPortrait.this.getClass().getSimpleName();

	private static final Set<ResultMetadataType> DISPLAYABLE_METADATA_TYPES;

	static {
		DISPLAYABLE_METADATA_TYPES = new HashSet<ResultMetadataType>(5);
		DISPLAYABLE_METADATA_TYPES.add(ResultMetadataType.ISSUE_NUMBER);
		DISPLAYABLE_METADATA_TYPES.add(ResultMetadataType.SUGGESTED_PRICE);
		DISPLAYABLE_METADATA_TYPES
				.add(ResultMetadataType.ERROR_CORRECTION_LEVEL);
		DISPLAYABLE_METADATA_TYPES.add(ResultMetadataType.POSSIBLE_COUNTRY);
	}

	private enum Source {
		NATIVE_APP_INTENT, PRODUCT_SEARCH_LINK, ZXING_LINK, NONE
	}

	private CaptureActivityHandler handler;

	private ViewfinderView viewfinderView;
	private MediaPlayer mediaPlayer;
	private Result lastResult;
	private boolean hasSurface;
	private boolean playBeep = true;
	private Source source;
	private String returnUrlTemplate;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private Button mButtonChangeLaser;
	private ViewfinderView mVviewfinder_view;

	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.capture);
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		handler = null;
		lastResult = null;
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		
		mButtonChangeLaser=(Button)findViewById(R.id.buttonLaser);
		
		mButtonChangeLaser.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mVviewfinder_view.changeLaser();
			}
		});
		mVviewfinder_view=(ViewfinderView)findViewById(R.id.viewfinder_view);

	}

	@Override
	protected void onResume() {
		// 初始化扫描界面
		super.onResume();
		resetStatusView();

		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);

		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}

		source = Source.NONE;
		decodeFormats = null;
		characterSet = null;
		initBeepSound();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (source == Source.NATIVE_APP_INTENT) {
				setResult(RESULT_CANCELED);
				finish();
				return true;
			} else if ((source == Source.NONE || source == Source.ZXING_LINK)
					&& lastResult != null) {
				resetStatusView();
				if (handler != null) {
					handler.sendEmptyMessage(R.id.restart_preview);
				}
				return true;
			}
		} else if (keyCode == KeyEvent.KEYCODE_FOCUS
				|| keyCode == KeyEvent.KEYCODE_CAMERA) {
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	/**
	 * 成功获取二维码，在handler中调用这个方法，回传bitmap
	 * 
	 * @param rawResult
	 * @param barcode
	 */
	public void handleDecode(Result rawResult, Bitmap barcode) {
		inactivityTimer.onActivity();
		lastResult = rawResult;
		if (barcode == null) {
			handleDecodeInternally(rawResult, null);
		} else {
			playBeepSoundAndVibrate();// 播放声音和振动代表成功获取二维码
			drawResultPoints(barcode, rawResult);

			switch (source) {
			case NATIVE_APP_INTENT:
			case PRODUCT_SEARCH_LINK:
				handleDecodeExternally(rawResult, barcode);
				break;
			case ZXING_LINK:
				if (returnUrlTemplate == null) {
					handleDecodeInternally(rawResult, barcode);
				} else {
					handleDecodeExternally(rawResult, barcode);
				}
				break;
			case NONE: {
				handleDecodeInternally(rawResult, barcode);
			}
				break;
			}
		}
	}

	/**
	 * 把图片截图下来之后,标记二维码所在的点 Superimpose a line for 1D or dots for 2D to highlight
	 * the key features of the barcode.
	 * 
	 * @param barcode
	 *            A bitmap of the captured image.
	 * @param rawResult
	 *            The decoded results which contains the points to draw.
	 */
	private void drawResultPoints(Bitmap barcode, Result rawResult) {
		ResultPoint[] points = rawResult.getResultPoints();
		if (points != null && points.length > 0) {
			Canvas canvas = new Canvas(barcode);
			Paint paint = new Paint();
			paint.setColor(getResources().getColor(R.color.result_image_border));
			paint.setStrokeWidth(3.0f);
			paint.setStyle(Paint.Style.STROKE);
			Rect border = new Rect(2, 2, barcode.getWidth() - 2,
					barcode.getHeight() - 2);
			canvas.drawRect(border, paint);
			paint.setColor(getResources().getColor(R.color.result_points));
			if (points.length == 2) {
				paint.setStrokeWidth(4.0f);
				drawLine(canvas, paint, points[0], points[1]);
			} else if (points.length == 4
					&& (rawResult.getBarcodeFormat()
							.equals(BarcodeFormat.UPC_A))
					|| (rawResult.getBarcodeFormat()
							.equals(BarcodeFormat.EAN_13))) {
				drawLine(canvas, paint, points[0], points[1]);
				drawLine(canvas, paint, points[2], points[3]);
			} else {
				paint.setStrokeWidth(10.0f);
				for (ResultPoint point : points) {
					canvas.drawPoint(point.getX(), point.getY(), paint);
				}
			}
		}
	}

	private static void drawLine(Canvas canvas, Paint paint, ResultPoint a,
			ResultPoint b) {
		canvas.drawLine(a.getX(), a.getY(), b.getX(), b.getY(), paint);
	}

	/**
	 * 二维码处理成功
	 * 
	 * @param rawResult
	 * @param barcode
	 */
	private void handleDecodeInternally(Result rawResult, Bitmap barcode) {
		viewfinderView.setVisibility(View.GONE);
		String content = rawResult.getText();
		//================
		View r = findViewById(R.id.relativeLayoutResult);
		TextView tv = (TextView)r.findViewById(R.id.textViewResult);
		tv.setText("(按back键重扫)结果:"+content);
		r.setVisibility(View.VISIBLE);
		
		Log.i(tag, "result:" + content);
	}

	private void handleDecodeExternally(Result rawResult, Bitmap barcode) {
		viewfinderView.drawResultBitmap(barcode);
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);
			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(VIBRATE_DURATION);
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			displayFrameworkBugMessageAndExit();
			return;
		} catch (RuntimeException e) {
			displayFrameworkBugMessageAndExit();
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.msg_camera_framework_bug));
		builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
		builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}

	private void resetStatusView() {
		viewfinderView.setVisibility(View.VISIBLE);
		lastResult = null;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}
}
