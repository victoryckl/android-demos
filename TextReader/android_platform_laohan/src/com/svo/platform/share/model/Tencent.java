package com.svo.platform.share.model;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import com.svo.platform.R;
import com.svo.platform.utils.Constants;
import com.svo.platform.widget.LoadDialog;
import com.tencent.weibo.api.FriendsAPI;
import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.constants.OAuthConstants;
import com.tencent.weibo.oauthv1.OAuthV1;
import com.tencent.weibo.oauthv1.OAuthV1Client;
import com.tencent.weibo.utils.QHttpClient;
import com.tencent.weibo.webview.OAuthV1AuthorizeWebView;

public class Tencent implements IShare {

	public static OAuthV1 oAuth;
	private String APP_KEY = Constants.TENCENT_APPKEY;
	private String APP_SECRET = Constants.TENCENT_APPSECRET;
	private String Callback = "null";
	private boolean flag;
	private Context context;

	public Tencent(Context context) {
		this.context = context;

		oAuth = new OAuthV1(Callback);
		oAuth.setOauthConsumerKey(APP_KEY);
		oAuth.setOauthConsumerSecret(APP_SECRET);
		OAuthV1Client.getQHttpClient().shutdownConnection();
		OAuthV1Client.setQHttpClient(new QHttpClient());
	}
	/**
	 * 分享到腾讯微博
	 * @param content
	 * @return -1表示分享成功，0表示分享时出现异常，1表示还没有绑定
	 */
	public void share(final String content,final String picPath) {
		if (isBinded()) {
			new AsyncTask<Void, Void, String>() {
				LoadDialog loadDialog;
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					loadDialog = new LoadDialog(context);
					loadDialog.show("分享中...");
				}
				@Override
				protected String doInBackground(Void... params) {
					TAPI tAPI= new TAPI(OAuthConstants.OAUTH_VERSION_1);
					String response = "";
					try {
						if (!TextUtils.isEmpty(picPath)) {
							response=tAPI.addPic(Tencent.oAuth, "json", content, "127.0.0.1", picPath);
						} else {
							response=tAPI.add(Tencent.oAuth, "json", content, "127.0.0.1");
						}
						tAPI.shutdownConnection();
						JSONObject jsonObject = new JSONObject(response);
						String result = jsonObject.getString("msg");
						if(!flag){
							return result;
						}
						flag = false;
					} catch (Exception e) {
						e.printStackTrace();
						return "no";
					}
					return null;
				}
				@Override
				protected void onPostExecute(String result) {
					super.onPostExecute(result);
					if (loadDialog != null) {
						loadDialog.dismiss();
					}
					if ("ok".equals(result)) {
						Toast.makeText(context, R.string.share_tencent_success, Toast.LENGTH_SHORT).show();
						((Activity) context).finish();
					}else {
						Toast.makeText(context, R.string.share_fail, Toast.LENGTH_SHORT).show();
					}
				}
			}.execute();
		}
	}

	/**
	 * 点评一条微博
	 * 
	 * @param content
	 *            微博内容 s * @param reid 点评父结点微博id
	 * @return
	 * @throws Exception
	 * @see <a
	 *      href="http://wiki.open.t.qq.com/index.php/%E5%BE%AE%E5%8D%9A%E7%9B%B8%E5%85%B3/%E7%82%B9%E8%AF%84%E4%B8%80%E6%9D%A1%E5%BE%AE%E5%8D%9A">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public String comment(String content, String reid) throws Exception {
		if (isBinded()) {
			TAPI tAPI = new TAPI(OAuthConstants.OAUTH_VERSION_1);
			return tAPI.comment(Tencent.oAuth, "json", content, "127.0.0.1", reid);
		}else {
			return "没有绑定";
		}
	}

	@Override
	public void bind() {
		new Thread() {
			public void run() {
				try {
					oAuth = OAuthV1Client.requestToken(oAuth);
					Intent intent = new Intent(context, OAuthV1AuthorizeWebView.class);
					intent.putExtra("oauth", oAuth);
					((Activity) context).startActivityForResult(intent, 1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

	@Override
	public void cancelBind() {
		SharedPreferences qweiboSP = context.getSharedPreferences("qweibo", 0);
		Editor editor = qweiboSP.edit();
		editor.clear().commit();
	}

	@Override
	public boolean isBinded() {
		SharedPreferences qweiboSP = context.getSharedPreferences("qweibo", 0);
		if (qweiboSP.getString("oauth_token", "") != null
				&& qweiboSP.getString("oauth_token", "").length() > 0
				&& qweiboSP.getString("oauth_token_secret", "") != null
				&& qweiboSP.getString("oauth_token_secret", "").length() > 0) {
			oAuth = new OAuthV1("null");
			oAuth.setOauthConsumerKey(APP_KEY);
			oAuth.setOauthConsumerSecret(APP_SECRET);
			oAuth.setOauthToken(qweiboSP.getString("oauth_token", ""));
			oAuth.setOauthTokenSecret(qweiboSP.getString("oauth_token_secret",
					""));
			return true;
		} else {
			return false;
		}
	}

	public void saveInfo(final int resultCode, final Intent data) {
		new Thread(){
			public void run() {
				if (resultCode == OAuthV1AuthorizeWebView.RESULT_CODE) {
					Tencent.oAuth = (OAuthV1) data.getExtras().getSerializable("oauth");

					try {
						Tencent.oAuth = OAuthV1Client.accessToken(Tencent.oAuth);
					} catch (Exception e) {
						e.printStackTrace();
					}
					SharedPreferences sharedPreferences = context.getSharedPreferences(
							"qweibo", 0);
					sharedPreferences.edit()
							.putString("oauth_token", Tencent.oAuth.getOauthToken())
							.commit();
					sharedPreferences
							.edit()
							.putString("oauth_token_secret",
									Tencent.oAuth.getOauthTokenSecret()).commit();
				}
			};
		}.start();
	}
	
	/**
	 * 收听某个用户<br>
	 * @param name 他人的帐户名列表，用","隔开
	 * @return
	 * @throws Exception
	 */
	public String  addListener(String names) throws Exception{
		FriendsAPI friendsAPI = new FriendsAPI(OAuthConstants.OAUTH_VERSION_1);
		return friendsAPI.add(oAuth, "json", names, "");
	}
	
	/**
	 * 判断是否收听了某用户
	 */
	
	public String  check(String names) throws Exception{
		FriendsAPI friendsAPI = new FriendsAPI(OAuthConstants.OAUTH_VERSION_1);
		return friendsAPI.check(oAuth, "json", names, "", "1");
	}
	/**
	 * 假如没有关注唯图，增加关注，并发表一条推广
	 */
	public void addShare(final Tencent tencent) {
		new Thread(){
			public void run() {
				try {
					String str = tencent.check(Constants.QQ_GUANFANG);
					JSONObject jsonObject = new JSONObject(str);
					JSONObject jsonObject2 = new JSONObject(jsonObject.getString("data").toString());
					if(!"true".equals(jsonObject2.get(Constants.QQ_GUANFANG))){
						flag = true;
						tencent.addListener(Constants.QQ_GUANFANG);
						tencent.share(Constants.SHARE_CONTENT+" "+ Constants.QQ_GUANFANG, "");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
		
	}

}
