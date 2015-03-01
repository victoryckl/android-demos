package com.brandontate.androidwebviewselection;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.webkit.WebView;

public class WebViewCopyActivity extends Activity {

	private WebView webview;
	private WebViewCopy copy;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_webview_copy);
		
		webview = (WebView) findViewById(R.id.wv_webview);
		webview.loadUrl("file:///android_asset/content-no-js.html");
		
		copy = new WebViewCopy(this, webview);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		copy.onCreateContextMenu(menu, v, menuInfo, 0, "¸´ÖÆ");
		super.onCreateContextMenu(menu, v, menuInfo);
	}
}
