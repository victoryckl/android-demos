package com.svo.platform.share.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;

import com.svo.platform.R;
import com.tencent.tauth.Constants;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * @author duweibin 
 * @version 创建时间：2012-11-5 上午10:16:09
 * QQ空间帮助类
 */
public class Qzone implements IShare{
	protected static final String TAG = "Qzone";
	// QQ空间授权范围
	private static final String SCOPE = "all";
	private Activity activity;
	private ProgressDialog dialog;
	public static Tencent mTencent;
	
	public Qzone(Activity shareActivity) {
		this.activity = shareActivity;
		mTencent = Tencent.createInstance(com.svo.platform.utils.Constants.QZONE_ID, shareActivity.getApplicationContext());
	}
	
	/**
	 * 去绑定QQ空间
	 */
	@Override
	public void bind() {
		mTencent.login(activity, SCOPE, new IUiListener() {

			@Override
			public void onError(UiError arg0) {
				Log.i(TAG, "onError");
				activity.finish();
			}

			@Override
			public void onComplete(JSONObject arg0) {
				Log.i(TAG, "onComplete");
				try {
					String access_token = arg0.getString("access_token");
					long expires_in = arg0.getLong("expires_in");
					String openId = arg0.getString("openid");
					saveInfo(access_token,expires_in,openId);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onCancel() {
				activity.finish();
				Log.i(TAG, "onCancel");
			}
		});
	}
	/**
	 * 去绑定QQ空间
	 */
	public void bind(IUiListener listener) {
		mTencent.login(activity, SCOPE, listener);
	}
	
	/**
	 * 取消绑定
	 */
	@Override
	public void cancelBind() {
		mTencent.logout(activity);
		SharedPreferences preferences = activity.getSharedPreferences("qzone", 0);
		preferences.edit().clear().commit();
	}
	
	/**
	 * 判断是否已绑定
	 * @return
	 */
	@Override
	public boolean isBinded() {
		SharedPreferences preferences = activity.getSharedPreferences("qzone", 0);
		String access_token = preferences.getString("qzone_access_token", ""); 
		long expires_in = preferences.getLong("qzone_expires_in", 1);  
		String openId = preferences.getString("qzone_openId", ""); 
		if (!"".equals(access_token)  && !"".equals(openId)) {
			if (expires_in > System.currentTimeMillis()) {
				mTencent.setOpenId(openId);
				mTencent.setAccessToken(access_token, expires_in+"");
				return true;
			}
		}
		return false;
	}
	/**
	 * 保存登录后的信息,下次免登录
	 * @param access_token
	 * @param expires_in access_token失效日期
	 * @param openId
	 */
	public void saveInfo(String access_token, long expires_in, String openId) {
		SharedPreferences preferences = activity.getSharedPreferences("qzone", 0);
		Editor editor = preferences.edit();
		editor.putString("qzone_access_token", access_token);
		//保存Access_token失效日期
		editor.putLong("qzone_expires_in", System.currentTimeMillis() + expires_in * 1000);
		editor.putString("qzone_openId", openId);
		editor.commit();
	}
	/**
	 * 
	 * @param bundle
	 * @param listener
	 */
	public void share(Bundle bundle,IUiListener listener) {
		mTencent.shareToQQ(activity, bundle, listener);
        showDialog();
	}
	/**
	 * 分享进度框
	 * @return
	 */
	protected Dialog showDialog() {
		dialog = ProgressDialog.show(activity, null,
				activity.getString(R.string.sharing), true, true);
		return dialog;
	}
	
	/**
	 * 使分享进度框消失
	 */
	public void dismissDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}
	/**
	 * 请求用户基本信息(昵称和头像的URI地址)
	 * @param callback
	 */
    public void reqUserInfo(IRequestListener callback) {
    	//判断用户是否登录的同时会对mTencent对象初始化
    	isBinded();
		mTencent.requestAsync(Constants.GRAPH_SIMPLE_USER_INFO, null,
                Constants.HTTP_GET, callback, null);
	}
}
