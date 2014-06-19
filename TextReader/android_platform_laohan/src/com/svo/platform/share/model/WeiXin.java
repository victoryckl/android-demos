package com.svo.platform.share.model;

import java.io.File;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.svo.platform.R;
import com.svo.platform.utils.Constants;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

public class WeiXin {
	private Activity activity;
	IWXAPI api;
	public WeiXin(Activity activity){
		this.activity = activity;
		api = WXAPIFactory.createWXAPI(activity, Constants.WEIXIN_ID, false);
		api.registerApp(Constants.WEIXIN_ID);
	}
	/**
	 * 分享到微信
	 * @param title 标题
	 * @param share_txt 分享的文字内容
	 * @param picPath 不可为null
	 * @param webpageUrl  原文链接地址
	 * @param from  weixin表示分享到微信,weixin_friend表示分享到朋友圈
	 */
	public void share(String title,String share_txt,String picPath,String webpageUrl,String from) {
		if (api.isWXAppInstalled()) {
			if (api.isWXAppSupportAPI()) {
				 // 将该app注册到微信
                File picFile = new File(picPath);
                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = webpageUrl;
                final WXMediaMessage msg = new WXMediaMessage();
                msg.title = title;
				msg.description = share_txt;
				msg.mediaObject = webpage;
                if (picFile != null && picFile.exists()) {
					msg.setThumbImage(Util.extractThumbNail(picPath, 150, 150, true));
				}else {
					msg.setThumbImage(BitmapFactory.decodeResource(activity.getResources(), R.drawable.logo));
				}
                // 构造一个Req
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("webpage"); // transaction字段用于唯一标识一个请求
                req.message = msg;
                req.scene = (from.equals("weixin")?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline);
                
                // 调用api接口发送数据到微信
                api.sendReq(req);
			} else {
				Toast.makeText(activity, R.string.not_support_weixin, Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(activity, R.string.not_install_weixin, Toast.LENGTH_LONG).show();
		}
		activity.finish();
	}
	public void shareImg(String title,String share_txt,String picPath,String webpageUrl,String from) {
		if (api.isWXAppInstalled()) {
			if (api.isWXAppSupportAPI()) {
				File picFile = new File(picPath);
                Log.i("weitu", "ii:"+picPath+";"+picFile.exists());
                Log.i("weitu", "title:"+title+";share_txt:"+share_txt+";webpageUrl:"+webpageUrl+";from:"+from);
                if (picFile == null || !picFile.exists()) {
                	Log.e("weitu", "分享图片不存在");
					return;
				}
				WXImageObject imgObj = new WXImageObject();
				imgObj.setImagePath(picPath);
				
				WXMediaMessage msg = new WXMediaMessage();
				msg.mediaObject = imgObj;
				
				Bitmap bmp = BitmapFactory.decodeFile(picPath);
				Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
				bmp.recycle();
				msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
				
				SendMessageToWX.Req req = new SendMessageToWX.Req();
				req.transaction = buildTransaction("img");
				req.message = msg;
				req.scene = from.equals("weixin")?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;
				api.sendReq(req);
			} else {
				Toast.makeText(activity, R.string.not_support_weixin, Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(activity, R.string.not_install_weixin, Toast.LENGTH_LONG).show();
		}
		activity.finish();
	}
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
}
