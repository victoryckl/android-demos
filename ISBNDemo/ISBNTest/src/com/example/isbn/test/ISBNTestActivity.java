package com.example.isbn.test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ISBNTestActivity extends Activity {
	private static final String TAG = ISBNTestActivity.class.getSimpleName();
	
	private static final String API_URL = "https://api.douban.com/v2/book/isbn/:";
	
	private EditText mEtIsbn;
	private TextView mTvUrl, mTvInfo;
	
	private ProgressDialog mProgress;
	private String mResult;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_isbntest);
		
		init();
	}

	private void init() {
		
		mEtIsbn = (EditText) findViewById(R.id.et_isbn);
		mTvUrl = (TextView) findViewById(R.id.tv_url);
		mTvInfo = (TextView) findViewById(R.id.tv_book_info);
		
		mTvUrl.setText(API_URL);
		
		findViewById(R.id.btn_scan).setOnClickListener(mClickListener);
		findViewById(R.id.btn_get_info).setOnClickListener(mClickListener);
		findViewById(R.id.btn_show_book).setOnClickListener(mClickListener);
	}
	
	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_scan:
				break;
			case R.id.btn_get_info:
				getInfo();
				break;
			case R.id.btn_show_book:
				showBook();
				break;
			default:
				break;
			}
		}
	};
	
	private void getInfo() {
		String isbn = mEtIsbn.getText().toString();
		if (TextUtils.isEmpty(isbn)) {
			showToast("ISBN号不能为空");
			return;
		}
		
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("请稍候，正在读取信息...");
        mProgress.show();
        
        String url = API_URL + isbn;
        mTvUrl.setText(url);
        new DownloadThread(url).start();
	}
	
	private void showBook() {
		String isbn = mEtIsbn.getText().toString();
		if (TextUtils.isEmpty(isbn)) {
			showToast("ISBN号不能为空");
			return;
		}
		
        mProgress=new ProgressDialog(this);
        mProgress.setMessage("请稍候，正在读取信息...");
        mProgress.show();
        
        String url = API_URL + isbn;
        mTvUrl.setText(url);
        new DownloadParseThread(url).start();
	}
	
    private class DownloadThread extends Thread {
    	String url=null;
    	public DownloadThread(String urlstr) {
    		url=urlstr;
    	}
    	
    	public void run() {
    		mResult = Util.Download(url);
    		Log.i(TAG, "download over");
    		mHandler.sendEmptyMessage(Msg.SHOW_INFO);
    	}
    }

    private class DownloadParseThread extends Thread {
    	String url=null;
    	public DownloadParseThread(String urlstr) {
    		url=urlstr;
    	}
    	
    	public void run()
    	{
    		mResult = Util.Download(url);
    		Log.i(TAG, "download over");
    		if(TextUtils.isEmpty(mResult)) {
    			mHandler.sendEmptyMessage(Msg.SHOW_INFO);
    			return ;
    		}
    		
            BookInfo book = new Util().parseBookInfo(mResult);
            Log.i(TAG, "parse over");
            
            Message msg=Message.obtain();
            msg.what = Msg.SHOW_VIEW;
            msg.obj = book;
            mHandler.sendMessage(msg);
            Log.i(TAG,"send over");
    	}
    }
    
    private interface Msg {
    	int SHOW_INFO = 0;
    	int SHOW_VIEW = 1;
    }
    
    private static final String TEXT_FAILED = "get info failed!!!";
    
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
        	mProgress.dismiss();
            switch (msg.what) {
			case Msg.SHOW_INFO:
				if (TextUtils.isEmpty(mResult)) {
					mTvInfo.setText(TEXT_FAILED);
					showToast(TEXT_FAILED);
				} else {
					mTvInfo.setText(mResult);
				}
				break;
			case Msg.SHOW_VIEW:
				BookInfo book = (BookInfo) msg.obj;
				if (book == null) {
					mTvInfo.setText(TEXT_FAILED);
					showToast(TEXT_FAILED);
				} else {
					Intent intent = new Intent(ISBNTestActivity.this, BookView.class);
					intent.putExtra(BookInfo.class.getName(), book);
					startActivity(intent);
				}
				break;
			default:
				break;
			}
        }
    };
    
	private void showToast(String msg) {
		Toast.makeText(ISBNTestActivity.this, msg, Toast.LENGTH_SHORT).show();
	}
}
