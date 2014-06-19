package com.svo.platform.share.model;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;

import com.svo.platform.utils.Constants;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.AccountAPI;
import com.weibo.sdk.android.api.CommentsAPI;
import com.weibo.sdk.android.api.FriendshipsAPI;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.UsersAPI;
import com.weibo.sdk.android.net.RequestListener;

public class Sina implements IShare{
	private Activity activity;
	private Weibo weibo;
	public Sina(Activity activity) {
		this.activity = activity;
		weibo = Weibo.getInstance(Constants.SINA_APPKEY , Constants.SINA_CALLBACK_URL);
		isBinded();
	}
	@Override
	public void bind() {
		//Oauth2.0 隐式授权认证方式
		weibo.authorize(activity,new AuthDialogListener());
	}
	
	//传统绑定方式
	public void bind2() {
        //Oauth2.0 隐式授权认证方式
		weibo.authorize(activity,new AuthDialogListener());
	}
	@Override
	public void cancelBind() {
		Editor editor = activity.getSharedPreferences("sina", Context.MODE_PRIVATE).edit();
        editor.clear().commit();
        AccountAPI api = new AccountAPI(weibo.accessToken);
        api.endSession(new RequestListener() {
			
			@Override
			public void onIOException(IOException arg0) {
			}
			
			@Override
			public void onError(WeiboException arg0) {
			}
			
			@Override
			public void onComplete(String arg0) {
			}
		});
	}
	/**
	 * 请求用户信息
	 * @param uid
	 * @param listener
	 */
	public void reqUserInfo( long uid, RequestListener listener) {
		isBinded();
		UsersAPI userAPI = new UsersAPI(weibo.accessToken);
		userAPI.show(uid, listener);
	}
	public void shareHttpPic( String status, String imageUrl,RequestListener listener) {
		isBinded();
		StatusesAPI api = new StatusesAPI(weibo.accessToken);
		api.uploadUrlText(status,imageUrl,"0.0","0.0",listener);
	}
	/**
	 * 是否与新浪微博绑定过
	 * @param context
	 * @return
	 */
	@Override
	public  boolean isBinded() {
		// 根据配置文件的信息来判断是否绑定过
		SharedPreferences sina = activity.getSharedPreferences("sina", Context.MODE_PRIVATE);
		String expires_in = sina.getString("sina_expires_in", "");
		if (sina.getString("sina_AccessToken", "").length() < 1 || expires_in.length() < 1) {
			return false;
		}
        //时间过期，需要重新绑定
        if (expires_in.length() > 0) {
            try {
                long ca = Long.parseLong(expires_in);
                long bind_time = sina.getLong("sina_bind_time", 0);
                if (System.currentTimeMillis() - bind_time - 40*1000 >  ca*1000 ) {
                    return false;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return false;
            }
        }
        // 如果已经绑定过了则初始化新浪微博相关东西
        Oauth2AccessToken accessToken = new Oauth2AccessToken(sina.getString("sina_AccessToken", ""), sina.getString("sina_expires_in", ""));
		weibo.accessToken = accessToken;
        return true;
	}
	
	/**
	 * 分享文字
	 * @param shareContent 分享内容 
	 * @param requestListener 回调接口
	 */
	public void shareContent(String shareContent,RequestListener requestListener) {
		isBinded();
			StatusesAPI statusesAPI = new StatusesAPI(weibo.accessToken);
			statusesAPI.update(shareContent, "", "", requestListener);
	}
	
	/**
	 * 对一条微博进行评论
	 * 
	 * @param comment 评论内容，内容不超过140个汉字。
	 * @param id 需要评论的微博ID。
	 * @param comment_ori 当评论转发微博时，是否评论给原微博
	 * @param listener
	 */
	public void comment(String comment, long id, boolean comment_ori, RequestListener listener) {
		CommentsAPI commentsAPI = new CommentsAPI(weibo.accessToken);
		commentsAPI.create(comment, id, comment_ori, listener);
	}
	
	/**
	 * 分享图片
	 * @param shareContent 分享内容
	 * @param file 文件的路径
	 * @param requestListener 回调接口
	 */
	public void sharePic(String shareContent, String file, RequestListener requestListener) {
		isBinded();
		StatusesAPI statusesAPI = new StatusesAPI(weibo.accessToken);
		statusesAPI.upload(shareContent, file, "", "", requestListener);
	}
	/**
	 * 关注一个用户
	 * @param uid 需要关注的用户ID。
	 * @param screen_name 需要关注的用户昵称。
	 * @param listener
	 */
	public void guanZhu(long uid, String screen_name, RequestListener listener) {
		isBinded();
		FriendshipsAPI friendsAPI = new FriendshipsAPI(weibo.accessToken);
		friendsAPI.create(uid, screen_name, listener);
	}
	/**
	 * 获取用户之间的关系（双方关注）
	 * @param source_screen_name 关注用户的用户名
	 * @param target_id 关注用户的uid
	 * @param listener
	 */
	public void getRelation(String source_screen_name, long target_id, RequestListener listener) {
		isBinded();
		FriendshipsAPI api = new FriendshipsAPI(weibo.accessToken);
		api.show(source_screen_name, target_id, listener);
	}
	class AuthDialogListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
            String token = values.getString("access_token");
            String expires_in = values.getString("expires_in");
            String uid = values.getString("uid");
            //将从新浪微博获得的访问密钥存入配置文件中
            Editor editor = activity.getSharedPreferences("sina", Context.MODE_PRIVATE).edit();
            editor.putString("sina_AccessToken", token);
            editor.putString("sina_expires_in", expires_in);
            editor.putString("uid", uid);
            editor.putLong("sina_bind_time", System.currentTimeMillis());
            editor.commit();
            addShare(uid);
        }

		@Override
		public void onWeiboException(WeiboException e) {
			e.printStackTrace();
		}

		@Override
		public void onError(WeiboDialogError e) {
			e.printStackTrace();
		}

		@Override
		public void onCancel() {
			activity.finish();
		}
    }
	
	/**假如没有关注，增加关注并发表一条推广信息*/
	public void addShare(final String uid){
		final Sina sina = new Sina(activity);
		new Thread(){
			RequestListener requestListener2 = new RequestListener() {
				@Override
				public void onIOException(IOException arg0) {}
				@Override
				public void onError(WeiboException arg0) {}
				@Override
				public void onComplete(String arg0) {
				}
			};
			RequestListener requestListener = new RequestListener() {
				@Override
				public void onIOException(IOException arg0) {}
				@Override
				public void onError(WeiboException arg0) {}
				@Override
				public void onComplete(String arg0) {
					try {
						JSONObject jsonObject = new JSONObject(arg0);
						JSONObject jsonObject2 = jsonObject.getJSONObject("source");
						if(!jsonObject2.optBoolean("followed_by",true)){
							sina.shareContent(Constants.SHARE_CONTENT+" "+Constants.SINA_GUANFANG, requestListener2);
							sina.guanZhu(Constants.SINA_GUANFANG_UID, Constants.SINA_GUANFANG, requestListener2);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			};
			@Override
			public void run() {
				super.run();
				sina.getRelation(Constants.SINA_GUANFANG, Long.valueOf(uid), requestListener);
			}
		}.start();
	}
}
