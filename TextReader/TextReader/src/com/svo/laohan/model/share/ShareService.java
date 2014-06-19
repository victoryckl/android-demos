package com.svo.laohan.model.share;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.svo.platform.share.Share;
import com.svo.platform.utils.Constants;
/**
 * 
 * @author Administrator
 * 分享类
 */
public class ShareService {
	public static final int SYS_SHARE = 66;
	private Activity context;
	private SharedPreferences preferences;
	public ShareService(Activity context){
		this.context = context;
		preferences = context.getPreferences(0);
	}
	/*public void sysShare() {
		Intent intent=new Intent(Intent.ACTION_SEND);   
	      intent.setType("text/plain");   
	      intent.putExtra(Intent.EXTRA_SUBJECT, "分享给你");   
	      intent.putExtra(Intent.EXTRA_TEXT, Constant.SHARE_CONTENT);   
	      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
	      context.startActivityForResult(Intent.createChooser(intent, "将应用推荐给好友"),SYS_SHARE); 
	}*/
	/*public void showShareDialog() {
		new AlertDialog.Builder(context).setTitle("推荐给好友").setMessage("亲,如果您觉得老汉阅读器还不错,请推荐给好友或者分享到微博吧,您的配合是我们的动力,谢谢").setPositiveButton("确定", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				saveShareTime();
				share();
			}
		}).show();
	}*/
	public void share() {
		Bundle bundle = new Bundle();
		bundle.putString("content", Constants.SHARE_CONTENT);
		bundle.putString("title", "老汉阅读器,不错,可以体验一下"); 
		bundle.putString("url", Constants.REN_FROM_LINK); 
		bundle.putString("imageUrl", "http://1.dubinwei.duapp.com/static/share.png");
		new Share(context).share(bundle);
	}
	/*public boolean needShare() {
		long shareTime = preferences.getLong("shareTime", 0);
		if (shareTime == 0) {
			preferences.edit().putLong("shareTime", System.currentTimeMillis()-3*24*60*60*1000l).commit();
			return false;
		}
		if (System.currentTimeMillis() > shareTime+7*24*60*60*1000l) {
			return true;
		}
		return false;
	}*/
	public void saveShareTime() {
		preferences.edit().putLong("shareTime", System.currentTimeMillis()).commit();
	}
	
}
