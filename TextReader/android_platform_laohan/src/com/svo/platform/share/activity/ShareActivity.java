package com.svo.platform.share.activity;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.renn.rennsdk.RennExecutor.CallBack;
import com.renn.rennsdk.RennResponse;
import com.svo.platform.R;
import com.svo.platform.share.model.Qzone;
import com.svo.platform.share.model.Ren;
import com.svo.platform.share.model.Sina;
import com.svo.platform.share.model.Tencent;
import com.svo.platform.share.model.WeiXin;
import com.svo.platform.utils.Constants;
import com.svo.platform.utils.PicUtil;
import com.svo.platform.utils.SvoToast;
import com.svo.platform.widget.LoadDialog;
import com.tencent.mm.sdk.platformtools.NetStatusUtil;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.net.RequestListener;

public class ShareActivity extends Activity implements OnClickListener{
	protected static final String TAG = "ShareActivity";
	//腾讯的微博字数限制
	private int WEIBO_MAX_LENGTH = 135;
	private TextView mTextNum;
	private Button mSend; //发送按钮
	private EditText mEdit;
	private String from = "tencent";
	private FrameLayout mPiclayout;
	private String mContent; ///传送过来的分享内容
	private String picPath; //本地图片路径
	private ImageView ivDelPic; // 删除图片,叉叉
	
	private String title; //分享内容的标题
	private String webpageUrl; //分享内容的原文链接
	private String imageUrl; //分享时显示的图片(QQ空间需要)
	private String extra_begin = ""; //加在分享内容前面的文字
	private String extra_end = ""; //加在分享内容后面的文字
	private Sina sina;
	@SuppressLint("NewApi")
	private Bitmap getBitmap(File file) {
		return PicUtil.parseBitmap(file);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.share);
		WEIBO_MAX_LENGTH = 135;
		Bundle bundle = getIntent().getBundleExtra("bundle");
		mContent = bundle.getString("content");
		picPath = bundle.getString("pic");
		title = bundle.getString("title");
		webpageUrl = bundle.getString("url");
		imageUrl = bundle.getString("imageUrl");
		
		from = bundle.getString("from");
		
		TextView tencentTv = (TextView) findViewById(R.id.tencent);
		Button close = (Button) this.findViewById(R.id.btnClose);
		ivDelPic = (ImageView) findViewById(R.id.ivDelPic);
        mSend = (Button) this.findViewById(R.id.btnSend);
        LinearLayout total = (LinearLayout) this.findViewById(R.id.ll_text_limit_unit);
        
        ivDelPic.setOnClickListener(this);
        close.setOnClickListener(this);
        mSend.setOnClickListener(this);
        total.setOnClickListener(this);
        
        mTextNum = (TextView) this.findViewById(R.id.tv_text_limit);
        mEdit = (EditText) this.findViewById(R.id.etEdit);
        //图片展示
        mPiclayout = (FrameLayout) ShareActivity.this.findViewById(R.id.flPic);
        if (TextUtils.isEmpty(picPath)) {
            mPiclayout.setVisibility(View.GONE);
        } else {
            mPiclayout.setVisibility(View.VISIBLE);
            File file = new File(picPath);
            if (file.exists()) {
            	Bitmap pic =getBitmap(file);
                ImageView image = (ImageView) this.findViewById(R.id.ivImage);
                if (pic != null) {
                	image.setImageBitmap(pic);
				}else {
					mPiclayout.setVisibility(View.GONE);
				}
            } else {
                mPiclayout.setVisibility(View.GONE);
            }
        }
        if ("weixin".equals(from)) {
			tencentTv.setText(R.string.weixin);
			//微信的字数限制
			WEIBO_MAX_LENGTH = 5100;
		}else if ("tencent".equals(from)) {
			WEIBO_MAX_LENGTH = 120;
			extra_begin = bundle.getString("extra_begin")==null?"":bundle.getString("extra_begin");
			extra_end = bundle.getString("extra_end")==null?"":bundle.getString("extra_end");
			tencentTv.setText(R.string.tencent);
			Tencent tencent = new Tencent(this);
			if (!tencent.isBinded()) {
				tencent.bind();
			}
		}else if ("sina".equals(from)) {
			WEIBO_MAX_LENGTH = 120;
			extra_begin = bundle.getString("extra_begin")==null?"":bundle.getString("extra_begin");
			extra_end = bundle.getString("extra_end")==null?"":bundle.getString("extra_end");
			tencentTv.setText(R.string.sina_weibo);
			sina = new Sina(this);
			if (!sina.isBinded()) {
				sina.bind2();
			}
		}else if ("Qzone".equals(from)) {
			tencentTv.setText(R.string.qzone);
			Qzone qzone = new Qzone(this);
			if (!qzone.isBinded()) {
				qzone.bind();
			}
		}else if ("renren".equals(from)) {
			tencentTv.setText(R.string.renren);
			Ren ren = new Ren(this);
			if (!ren.isBinded()) {
				ren.bind();
			}
		}else if ("weixin_friend".equals(from)) {
			tencentTv.setText(R.string.weixin_friend);
			//微信的字数限制
			WEIBO_MAX_LENGTH = 5100;
		}
        mEdit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            	String mText = mEdit.getText().toString();
				int len = 0;
				try {
					len = mText.getBytes("GBK").length/2;
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					len = mText.length();
				}
                 if (len <= WEIBO_MAX_LENGTH) {
                     len = WEIBO_MAX_LENGTH - len;
                     mTextNum.setTextColor(getResources().getColor(R.color.text_num_gray));
                     if (!mSend.isEnabled()){
                     	mSend.setEnabled(true);
                     	mSend.setBackgroundResource(R.drawable.btn_press);
                     }
                 } else {
                     len = len - WEIBO_MAX_LENGTH;
                     mTextNum.setTextColor(Color.RED);
                     SvoToast.showHint(ShareActivity.this, "分享字数已超过上限", Toast.LENGTH_LONG);
                     if (mSend.isEnabled()){
                     	mSend.setEnabled(false);
                     	mSend.setBackgroundResource(R.drawable.bg_btn_pressed);
                     }
                 }
                 mTextNum.setText(String.valueOf(len));
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        mEdit.setText(mContent);
	}
	public static Bitmap decodeScaledBitmap(String filename,int scale) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filename, options);

		// Calculate inSampleSize
		options.inSampleSize = scale;

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filename, options);
	}
	LoadDialog loadDialog;
	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		mContent = mEdit.getEditableText().toString();
        if (viewId == R.id.btnClose) {
            finish();
        } else if (viewId == R.id.btnSend) {
        	if (TextUtils.isEmpty(mContent)) {
				SvoToast.showHint(this, "分享内容不可为空", Toast.LENGTH_SHORT);
				return;
			}
        	if (!NetStatusUtil.isConnected(this)) {
        		SvoToast.showHint(this,"网络不可用", Toast.LENGTH_SHORT);
				return;
			}
        	if ("weixin".equals(from) || "weixin_friend".equals(from)) { // 微信
        		WeiXin weiXin = new WeiXin(this);
        		if (mPiclayout.getVisibility() == View.VISIBLE) {
        			weiXin.share(title,mContent,picPath,webpageUrl,from);
				} else {
					weiXin.share(title,mContent,"",webpageUrl,from);
				}
			} else if ("tencent".equals(from)){ // 腾讯微博
				mContent = extra_begin + mContent + extra_end;
				Tencent tencent = new Tencent(this);
				if (mPiclayout.getVisibility() == View.VISIBLE) {
					tencent.share(mContent, picPath);
				} else {
					tencent.share(mContent, "");
				}
			}else if ("renren".equals(from)){ // 人人网
				mContent = extra_begin + mContent + extra_end;
				Ren ren = new Ren(this);
				final Handler handler = new Handler(getMainLooper()); 
				CallBack callBack = new CallBack() {
					
					@Override
					public void onSuccess(RennResponse arg0) {
						renHint(handler,R.string.share_renren_success);
					}
					
					@Override
					public void onFailed(String arg0, String arg1) {
						renHint(handler,R.string.share_fail);
					}
				};
				Bundle bundle = getIntent().getBundleExtra("bundle");
				loadDialog = new LoadDialog(this);
				loadDialog.show("分享中...");
				if (mPiclayout.getVisibility() == View.VISIBLE) {
					ren.share(bundle.getString("title"),mContent, bundle.getString("url"),bundle.getString("imageUrl"),callBack);
				} else {
					ren.shareStatus(mContent, callBack);
				}
			}else if ("sina".equals(from)){ //新浪微博
				mContent = extra_begin+mContent+extra_end;
				final LoadDialog loadDialog = new LoadDialog(this);
				loadDialog.show("分享中...");
				final Handler handler = new Handler(){
					@Override
					public void handleMessage(Message msg) {
						super.handleMessage(msg);
						loadDialog.dismiss();
						if (msg.what == 1) {
							SvoToast.showHint(ShareActivity.this, R.string.share_sina_success, Toast.LENGTH_SHORT);
							finish();
						}else {
							SvoToast.showHint(ShareActivity.this, R.string.share_fail, Toast.LENGTH_SHORT);
						}
					}
				};
				RequestListener listener = new RequestListener() {
					@Override
					public void onIOException(IOException e) {
						e.printStackTrace();
						handler.sendEmptyMessage(0);
					}
					@Override
					public void onError(WeiboException e) {
						e.printStackTrace();
						handler.sendEmptyMessage(0);
					}
					@Override
					public void onComplete(String response) {
						handler.sendEmptyMessage(1);
					}
				};
				if (mPiclayout.getVisibility() != View.VISIBLE) {
					sina.shareContent(mContent, listener);
				} else {
					if (!TextUtils.isEmpty(imageUrl) && Constants.IS_SUPPORT_HTTP_PIC) {
						sina.shareHttpPic(mContent, imageUrl, listener);
					} else {
						sina.sharePic(mContent, picPath, listener);
					}
				}
			} else	if ("Qzone".equals(from)) { // QQ空间
				final Qzone qzone = new Qzone(this);
				Bundle bundle = new Bundle();
				bundle.putString("title", title);
				bundle.putString("targetUrl", Constants.REN_FROM_LINK);
				bundle.putString("imageUrl", imageUrl);
				bundle.putString("summary", mContent);
//				bundle.putString("site", imageUrl);
				bundle.putString("appName", "唯图");
				qzone.share(bundle, new IUiListener() {
					
					@Override
					public void onError(UiError arg0) {
						qzone.dismissDialog();
						ShareActivity.this.finish();
					}
					
					@Override
					public void onComplete(JSONObject arg0) {
						qzone.dismissDialog();
						ShareActivity.this.finish();
					}
					
					@Override
					public void onCancel() {
						qzone.dismissDialog();
						ShareActivity.this.finish();
					}
				});
			}
        	Intent data = new Intent();
        	data.putExtra("from", from);
			setResult(RESULT_OK, data );
        } else if (viewId == R.id.ll_text_limit_unit) {
            Dialog dialog = new AlertDialog.Builder(ShareActivity.this).setTitle(R.string.attention)
                    .setMessage(R.string.delete_all)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mEdit.setText("");
                        }
                    }).setNegativeButton(R.string.cancel, null).create();
            dialog.show();
        }else if (viewId == R.id.ivDelPic) {
            Dialog dialog = new AlertDialog.Builder(this).setTitle(R.string.attention)
                    .setMessage(R.string.del_pic)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mPiclayout.setVisibility(View.GONE);
                        }
                    }).setNegativeButton(R.string.cancel, null).create();
            dialog.show();
        }
	}
	private void renHint(final Handler handler,final int resId) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (loadDialog != null) {
					loadDialog.dismiss();
				}
				SvoToast.showHint(ShareActivity.this, resId, Toast.LENGTH_SHORT);
				finish();
			}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode==1)	{
			if (data == null) {
				finish();
				return;
			}
			//登录成功后保存相关信息,下次不再登录
			Tencent tencent = new Tencent(this);
			tencent.saveInfo(resultCode, data);
			tencent.addShare(tencent);
		}
		
		 if (null != Qzone.mTencent)    
			 Qzone.mTencent.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			if (("sina".equals(from) && !sina.isBinded()) || ("renren".equals(from) && !new Ren(this).isBinded())) {
				finish();
			}
		}
	}
}
