package com.example.webviewplayvedio;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

@SuppressLint("ResourceAsColor")
public class WebViewPlayVedioActivity extends Activity {
	private static final String TAG = WebViewPlayVedioActivity.class.getSimpleName();
	private WebView mWebView;
	private EditText mEtPath;
	private FrameLayout mFrameLayout;
	private MyChromeClient mWebChromeClient;
	private WebChromeClient.CustomViewCallback mCustomViewCallback;
    protected FrameLayout mCustomViewContainer;
    protected FrameLayout mFullscreenContainer;
	private View mCustomView;
    private int mOriginalOrientation;
    private Activity mActivity;
    
    protected static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS =
            new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);

    protected static final FrameLayout.LayoutParams COVER_SCREEN_GRAVITY_CENTER =
            new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            Gravity.CENTER);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview_play_vedio);
		
		init(savedInstanceState);
	}
	
	private void init(Bundle savedInstanceState) {
		mActivity = this;
		
		findViewById(R.id.btn_choose_html).setOnClickListener(mBtnClickListener);
		findViewById(R.id.btn_play).setOnClickListener(mBtnClickListener);
		mEtPath = (EditText)findViewById(R.id.et_html_path);
		
		mFrameLayout = (FrameLayout)findViewById(R.id.framelayout);
		mWebView = (WebView)findViewById(R.id.wv_webview);
		settings(mWebView);
		if(savedInstanceState != null){
			mWebView.restoreState(savedInstanceState);
		}
		playVedio();
	}
	
	public void settings(WebView webView) {
		final String USER_AGENT_STRING = webView.getSettings().getUserAgentString() + " Rong/2.0";

		WebSettings s = webView.getSettings();
		s.setJavaScriptCanOpenWindowsAutomatically(true);
		s.setTextZoom(100);
		s.setUserAgentString( USER_AGENT_STRING );
		s.setSupportZoom(false);
		s.setPluginState(WebSettings.PluginState.ON);
		s.setLoadWithOverviewMode(true);
		
		webView.setBackgroundColor(android.R.color.holo_green_light);
		webView.setWebViewClient(new MyWebviewCient());
		
		mWebChromeClient = new MyChromeClient();
		webView.setWebChromeClient(mWebChromeClient);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

		webView.setHorizontalScrollBarEnabled(false);
		webView.setVerticalScrollBarEnabled(false);	
	}

	static class FullscreenHolder extends FrameLayout {
		public FullscreenHolder(Context ctx) {
			super(ctx);
			setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
		}
		@Override
		public boolean onTouchEvent(MotionEvent evt) {
			return true;
		}
	}
	
	@Override
	public void onBackPressed() {
		if(mCustomView == null){
			super.onBackPressed();
		}
		else{
			mWebChromeClient.onHideCustomView();
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		mWebView.saveState(outState);
	}
	
	public class MyWebviewCient extends WebViewClient{
		@Override
		public WebResourceResponse shouldInterceptRequest(WebView view,
				String url) {
			WebResourceResponse response = null;
			response = super.shouldInterceptRequest(view, url);
			return response;
		}
	}
	
	public class MyChromeClient extends WebChromeClient{
        @Override
        public void onShowCustomView(View view,
                WebChromeClient.CustomViewCallback callback) {
        	if (mActivity != null) {
        		onShowCustomView(view, mActivity.getRequestedOrientation(), callback);
        	}
        }

	    @Override
	    public void onShowCustomView(View view, int requestedOrientation,
	            WebChromeClient.CustomViewCallback callback) {
	        // if a view already exists then immediately terminate the new one
	        if (mCustomView != null) {
	            callback.onCustomViewHidden();
	            return;
	        }

	        mOriginalOrientation = mActivity.getRequestedOrientation();
	        FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
	        mFullscreenContainer = new FullscreenHolder(mActivity);
	        mFullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
	        decor.addView(mFullscreenContainer, COVER_SCREEN_PARAMS);
	        mCustomView = view;
	        setFullscreen(true);
	        mWebView.setVisibility(View.INVISIBLE);
	        mCustomViewCallback = callback;
	        mActivity.setRequestedOrientation(requestedOrientation);
	    }
	    
	    @Override
	    public void onHideCustomView() {
	        mWebView.setVisibility(View.VISIBLE);
	        if (mCustomView == null)
	            return;
	        setFullscreen(false);
	        FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
	        decor.removeView(mFullscreenContainer);
	        mFullscreenContainer = null;
	        mCustomView = null;
	        mCustomViewCallback.onCustomViewHidden();
	        // Show the content view.
	        mActivity.setRequestedOrientation(mOriginalOrientation);
	    }
		
		@Override
		public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
			Log.d(TAG, consoleMessage.message()+" at "+consoleMessage.sourceId()+":"+consoleMessage.lineNumber());
			return super.onConsoleMessage(consoleMessage);
		}
	}
	
    public void setFullscreen(boolean enabled) {
        Window win = mActivity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        if (enabled) {
            winParams.flags |=  bits;
        } else {
            winParams.flags &= ~bits;
            if (mCustomView != null) {
                mCustomView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
            else {
                mActivity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }
        win.setAttributes(winParams);
    }
	
    public boolean isCustomViewShowing() {
        return mCustomView != null;
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    
	private OnClickListener mBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_choose_html:
				chooseFile();
				break;
			case R.id.btn_play:
				playVedio();
				break;
			default:
				break;
			}
		}
	};
	
	private void playVedio() {
		String path = getPath();
		if (path != null) {
			mWebView.loadUrl(path);
		}
	}
	
	private String getPath() {
		String path = null;

		path = mEtPath.getText().toString();
		if (path == null || path.length() <= 0) {
			Toast.makeText(getApplicationContext(), "请选择文件", Toast.LENGTH_SHORT).show();
			path = null;
		} else {
			Log.i(TAG, "getPath(): " + path);
		}
		
		return path;
	}
	
	private static final int FILE_SELECT_CODE = 0;
	private void chooseFile() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try {
			startActivityForResult(Intent.createChooser(intent, "请选择多html文件"), FILE_SELECT_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(getApplicationContext(), "亲，木有文件管理器-_-!", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			Log.e(TAG, "onActivityResult() error, resultCode: " + resultCode);
			super.onActivityResult(requestCode, resultCode, data);
			return;
		}
		if (requestCode == FILE_SELECT_CODE) {
			Uri uri = data.getData();
			Log.i(TAG, "------->" + uri.getPath());
			mEtPath.setText(uri.getPath());
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
