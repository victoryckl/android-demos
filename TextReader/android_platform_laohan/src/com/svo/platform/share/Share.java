package com.svo.platform.share;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.svo.platform.R;
import com.svo.platform.share.activity.ShareActivity;
import com.svo.platform.widget.QuickAction;
import com.svo.platform.widget.QuickActionGrid;
import com.svo.platform.widget.QuickActionWidget;
import com.svo.platform.widget.QuickActionWidget.OnQuickActionClickListener;

/**
 * @author duweibin 
 * @version 创建时间：2012-11-2 上午9:52:23
 * 分享帮助类
 */
public class Share {
	private Activity activity;
	/*用来定位用户点击的是哪个微博*/
	private int[] whichWeibo;
	/*各个微博的名字 */
	private String[] weiboStrings;
	public Share(Activity activity) {
		this.activity = activity;
		weiboStrings = activity.getResources().getStringArray(R.array.weibos);
		whichWeibo = new int[weiboStrings.length];
	}

	/**
	 * 显示分享列表选择框
	 * 
	 * @param bundle  
	 * 			bundle.putString("content", "one for all,all for one"); 文本内容
	 *            bundle.putString("pic", "/mnt/sdcard/icon100.png"); 本地图片路径 
	 *            
	 *            bundle.putString("title", "one for all,all for one"); 
	 *            bundle.putString("url", "原文链接地址"); 
	 *            //仅适用于新浪微博与腾讯微博,可不传
	 *            bundle.putString("extra_begin", "分享时加在分享内容前面的内容"); 
	 *            bundle.putString("extra_end", "分享时加在分享内容后面的内容"); 
	 *            //QQ空间需要
	 *            bundle.putString("imageUrl", "图片链接地址，http开头");
	 */
	public void share(final Bundle bundle) {
		new AlertDialog.Builder(activity).setAdapter(getAdapter(activity), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				weiboClick(bundle, which);
			}
		}).setTitle(R.string.share2weibo).show();
	}
	public void share(final Bundle bundle,View view) {
		QuickActionGrid mGrid = new QuickActionGrid(activity);
		ArrayList<HashMap<String, Object>> data = getAdapterValues();
		for (int i = 0; i < data.size(); i++) {
			HashMap<String, Object> map = data.get(i);
			mGrid.addQuickAction(new QuickAction(activity, (Integer)map.get("img"), (String)map.get("ItemTitle")));
		}
        mGrid.show(view);
        mGrid.setOnQuickActionClickListener(new OnQuickActionClickListener() {
			
			@Override
			public void onQuickActionClicked(QuickActionWidget widget, int position) {
				widget.dismiss();
				weiboClick(bundle, position);
			}
		});
	}
	/**
	 * @param bundle
	 * @param which
	 */
	private void weiboClick(final Bundle bundle, int which) {
		Intent intent = new Intent(activity, ShareActivity.class);
		intent.putExtra("bundle", bundle);
		switch (whichWeibo[which]) {
		case 0: // 分享到新浪微博
			bundle.putString("from", "sina");
			activity.startActivityForResult(intent, 100);
			break;
		case 1:// 腾讯微博
			bundle.putString("from", "tencent");
			activity.startActivityForResult(intent, 100);
			break;
		case 2:// 微信
			bundle.putString("from", "weixin");
			activity.startActivityForResult(intent, 100);
			break;
		case 3:// 人人网
			bundle.putString("from", "renren");
			activity.startActivityForResult(intent, 100);
			break;
		case 4: // 分享到QQ空间
			bundle.putString("from", "Qzone");
			activity.startActivityForResult(intent, 100);
			break;
		case 5: // 邮件
			Intent it = new Intent(Intent.ACTION_SEND);    
	        it.setPackage("com.android.email");
	        it.putExtra(Intent.EXTRA_TEXT, bundle.getString("content"));    
	        it.setType("text/plain");    
	        activity.startActivity(it); 
			break;
		case 6: // 短信发送
			Uri smsToUri = Uri.parse("smsto:");
			Intent mIntent = new Intent(Intent.ACTION_SENDTO, smsToUri);
			mIntent.putExtra("sms_body", bundle.getString("content"));
			activity.startActivity(mIntent);
			break;
		case 7: // 分享到朋友圈
			bundle.putString("from", "weixin_friend");
			activity.startActivityForResult(intent, 100);
			break;
		case 8: // 分享到有道
			bundle.putString("from", "youdao");
			activity.startActivity(intent);
			break;
		default:
			break;
		}
	}
	/**
	 * 通过已安装应用分享
	 * @param subject 主题
	 * @param title 标题
	 * @param text 分享的内容
	 * @param picPath 图片的本地路径 
	 */
	public void share2app(String subject,String title,String text,String picPath) {
		Intent intent = new Intent(Intent.ACTION_SEND); // 启动分享发送的属性

		intent.setType("text/plain"); // 分享发送的数据类型
		intent.putExtra(Intent.EXTRA_SUBJECT, subject); // 分享的主题
		intent.putExtra(Intent.EXTRA_TITLE, subject); // 分享的标题
		intent.putExtra(Intent.EXTRA_TEXT, text); // 分享的内容
		intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(picPath));
		activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.select_shrae)));
	}
	/**
	 * 分享列表对话框适配器
	 * @param activity 
	 * @return 
	 */
	private ListAdapter getAdapter(Activity activity) {
		ArrayList<HashMap<String, Object>> data = getAdapterValues();
		SimpleAdapter adapter = new SimpleAdapter(activity, data, R.layout.share_listview,new String[] { "img", "ItemTitle" }, new int[] {
                R.id.share_listviewImg, R.id.share_TV });
		return adapter;
	}

	/**
	 * @return
	 */
	private ArrayList<HashMap<String, Object>> getAdapterValues() {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < weiboStrings.length; i++) {
			int picResId = getResId(weiboStrings[i],i);
			HashMap<String, Object> map = new HashMap<String, Object>();
	        map.put("ItemTitle", weiboStrings[i]);
	        map.put("img", picResId);
	        data.add(map);
		}
		return data;
	}
	
	/**
	 * 根据分享微博条目内容得到图标资源ID
	 * @param i 
	 * @param string 微博条目内容（比如分享到新浪微博）
	 * @return 图标资源ID
	 */
	private int getResId(String str, int i) {
		if (str.contains("新浪")) {
			whichWeibo[i] = 0;
			return R.drawable.third_sina;
		} else if (str.contains("腾讯微博")) {
			whichWeibo[i] = 1;
			return R.drawable.third_qq;
		} else if (str.contains("人人")) {
			whichWeibo[i] = 3;
			return R.drawable.third_renren;
		} else if (str.contains("QQ空间") || str.contains("qq空间")) {
			whichWeibo[i] = 4;
			return R.drawable.qzone;
		} else if (str.contains("微信")&&str.contains("好友")) {
			whichWeibo[i] = 2;
			return R.drawable.weixin_icon;
		} else if (str.contains("邮件")) {
			whichWeibo[i] = 5;
			return R.drawable.email;
		} else if (str.contains("短信")) {
			whichWeibo[i] = 6;
			return R.drawable.sms;
		}else if (str.contains("朋友圈")){
			whichWeibo[i] = 7;
			return R.drawable.weixin_timeline;
		}else if (str.contains("有道")){
			whichWeibo[i] = 8;
			return R.drawable.youdao_share;
		}
		return R.drawable.youdao_share;
	}

	/**
	 * 显示正在分享进度框
	 * @param context
	 * @return
	 */
	public ProgressDialog showProgressDialog() {
		return ProgressDialog.show(activity, null, activity.getString(R.string.sharing), true, true);
	}
}
