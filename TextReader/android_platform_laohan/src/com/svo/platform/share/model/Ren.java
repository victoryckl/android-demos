package com.svo.platform.share.model;

import android.app.Activity;

import com.renn.rennsdk.RennClient;
import com.renn.rennsdk.RennClient.LoginListener;
import com.renn.rennsdk.RennExecutor.CallBack;
import com.renn.rennsdk.exception.RennException;
import com.renn.rennsdk.param.PutFeedParam;
import com.renn.rennsdk.param.PutStatusParam;
import com.svo.platform.utils.Constants;

public class Ren implements IShare{
	

	private RennClient renren;
	private Activity activity;
	public Ren(Activity activity) {
		this.activity = activity;
		renren = RennClient.getInstance(activity);
		renren.init(Constants.REN_APP_ID, Constants.REN_API_KEY, Constants.REN_SECRET_KEY);
		renren.setTokenType("bearer");
		renren
		.setScope("publish_blog publish_share "
				+ "send_notification photo_upload status_update "
				+ "publish_feed");
	}
	/**
	 * 向人人网分享内容
	 * @param title 分享标题 
	 * @param content 分享内容
	 * @param linkUrl 
	 */
	public void share(String title, String content,String linkUrl,String imageUrl,CallBack callBack) {
		if (isBinded()) {
			PutFeedParam param = new PutFeedParam();
	        param.setTitle(title);
	        param.setMessage("唯图");
	        param.setDescription(content);
	        param.setActionName(Constants.REN_FROM);
	        param.setActionTargetUrl(Constants.REN_FROM_LINK);
	        param.setImageUrl(imageUrl);
	        param.setTargetUrl("http://www.anzhi.com/soft_1050517.html");
	        try {
                renren.getRennService().sendAsynRequest(param, callBack);
            } catch (RennException e1) {
                e1.printStackTrace();
            }
		} else {
			bind();
		}
	}
	/**
	 * 分享状态
	 * @param content
	 * @param listener
	 */
	public void shareStatus(String content,CallBack callBack) {
		PutStatusParam putStatusParam = new PutStatusParam();
        putStatusParam.setContent(content);
        try {
			renren.getRennService().sendAsynRequest(putStatusParam, callBack);
		} catch (RennException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void bind() {
		renren.setLoginListener(new LoginListener() {
			@Override
			public void onLoginSuccess() {
				/*SharedPreferences renrenSP = activity.getSharedPreferences("renren", 0);
				String access_token = values.getString("access_token");
				if (access_token != null && access_token.length()>0) {
					renrenSP.edit().putString("accessToken", access_token).commit();
				}*/
			}

			@Override
			public void onLoginCanceled() {
			}
		});
		renren.login(activity);
	}
	
	@Override
	public boolean isBinded() {
		return renren.isLogin();
	}
	@Override
	public void cancelBind() {
		renren.logout();
	}

}
