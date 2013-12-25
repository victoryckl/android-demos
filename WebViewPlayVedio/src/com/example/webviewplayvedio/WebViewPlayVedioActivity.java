package com.example.webviewplayvedio;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview_play_vedio);
		
		init(savedInstanceState);
	}
	
	private void init(Bundle savedInstanceState) {
		findViewById(R.id.btn_choose_html).setOnClickListener(mBtnClickListener);
		findViewById(R.id.btn_play).setOnClickListener(mBtnClickListener);
		mEtPath = (EditText)findViewById(R.id.et_html_path);
		
		mFrameLayout = (FrameLayout)findViewById(R.id.framelayout);
		mWebView = (WebView)findViewById(R.id.wv_webview);
		settings(mWebView);
		if(savedInstanceState != null){
			mWebView.restoreState(savedInstanceState);
		}
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
		
		webView.setWebChromeClient(new MyChromeClient());
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

		webView.setHorizontalScrollBarEnabled(false);
		webView.setVerticalScrollBarEnabled(false);	
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
		private WebChromeClient.CustomViewCallback mCallback = null;
		private View mView = null;
		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			if(mView != null){
				callback.onCustomViewHidden();
				return;
			}
			mFrameLayout.removeView(mWebView);
			mFrameLayout.addView(view);
			mView = view;
			mCallback = callback;
		}
		
		@Override
		public void onHideCustomView() {
			if(mView == null){
				return;
			}
			mFrameLayout.removeView(mView);
			mView = null;
			mFrameLayout.addView(mWebView);
			mCallback.onCustomViewHidden();
		}
		
		@Override
		public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
			Log.d(TAG, consoleMessage.message()+" at "+consoleMessage.sourceId()+":"+consoleMessage.lineNumber());
			return super.onConsoleMessage(consoleMessage);
		}
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
