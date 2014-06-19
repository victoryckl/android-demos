package com.svo.laohan.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;
import cn.waps.AppConnect;

import com.svo.laohan.JifenActivity;
import com.svo.platform.utils.SvoToast;
import com.tencent.mm.sdk.platformtools.Util;

public class Mjifen {
	private SharedPreferences sharedPreferences;
	Context context;
	public Mjifen(Context context) {
		this.context = context;
		sharedPreferences = context.getSharedPreferences("weitu", 0);
	}
	public void add(int num) {
		int jifen = getJifen();
		saveJifen(jifen+num);
		AppConnect.getInstance(context).awardPoints(num);
	}
	public void sub(int num) {
		int jifen = getJifen();
		saveJifen(jifen-num);
		AppConnect.getInstance(context).spendPoints(num);
	}
	public boolean isVip() {
		return sharedPreferences.getBoolean("isvip", false);
	}
	public void setVip() {
		sharedPreferences.edit().putBoolean("isvip", true).commit();
	}
	/**
	 * 保存当前积分数
	 * @param jifen 积分的个数
	 */
	public void saveJifen(int jifen) {
		sharedPreferences.edit().putInt("jifen", jifen).commit();
		if (!isVip()) {
			if (jifen >= 150) {
				setVip();
			}
		}
	}
	/**
	 * 获得积分
	 * @return
	 */
	public int getJifen() {
		return sharedPreferences.getInt("jifen", 0);
	}
	public void showDialog() {
		new AlertDialog.Builder(context).setTitle("免费赚取比特币").setMessage("您的比特币个数为0，下载一本书籍仅需一个比特币，免费赚取比特币，方便快捷").setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				context.startActivity(new Intent(context, JifenActivity.class));
			}
		}).setNegativeButton("取消", null).show();
	}
	/**
	 * 处理是否要增加积分
	 * @param from 分享来源，微博还是微信
	 */
	public void handShare(String from) {
		SharedPreferences jifenConfig = context.getSharedPreferences("jifen", 0);
		long lastShareTime = jifenConfig.getLong(from, 0);
		long curDayMs = Util.currentDayInMills();
		if (curDayMs > lastShareTime) { //应该加分
			add(5);
			jifenConfig.edit().putLong(from, System.currentTimeMillis()).commit();
			SvoToast.showHint(context, "恭喜，获得5个比特币", Toast.LENGTH_LONG);
		} else {
			if ("weixin".equals(from)) {
				from = "微信";
			}else if ("tencent".equals(from)) {
				from = "腾讯微博";
			}else if ("sina".equals(from)) {
				from = "新浪微博";
			}else if ("Qzone".equals(from)) {
				from = "QQ空间";
			}else if ("renren".equals(from)) {
				from = "人人网";
			}else if ("weixin_friend".equals(from)) {
				from = "微信朋友圈";
			}
			SvoToast.showHint(context, "明日分享到"+from+"即可获得5个比特币，记得再来哦", Toast.LENGTH_LONG);
		}
	}
}
