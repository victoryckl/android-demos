package com.svo.platform.share;

import java.util.LinkedList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.svo.platform.R;
import com.svo.platform.share.model.IShare;
import com.svo.platform.share.model.Qzone;
import com.svo.platform.share.model.Ren;
import com.svo.platform.share.model.Sina;
import com.svo.platform.share.model.Tencent;

public class WeiboBindActivity extends Activity {
	/** 帐号绑定ListView */
	private ListView listView;
	/** 适配器 */
	private WeiboAdapter weiboAdapter;
	private LinkedList<String> ss = new LinkedList<String>();
	/** Activity创建初*/
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.bind_account);
		String[] weibos = getResources().getStringArray(R.array.weibos);
		for (String s : weibos) {
			if (s.contains("新浪微博") || s.contains("腾讯微博") || s.contains("人人网")) {
				ss.add(s);
			}
		}
		listView = (ListView)findViewById(R.id.bindaccount_list);
	}
	
	protected void onResume() {
		super.onResume();
		weiboAdapter = new WeiboAdapter();
		listView.setAdapter(weiboAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			IShare whichWeibo;
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				String weiboName = ss.get(position);
				if(weiboName.contains("新浪微博")){
					whichWeibo = new Sina(WeiboBindActivity.this);
				}else if(weiboName.contains("腾讯微博")){
					whichWeibo = new Tencent(WeiboBindActivity.this);
				}else if(weiboName.contains("人人网")){
					whichWeibo = new Ren(WeiboBindActivity.this);
				}else if(weiboName.contains("空间")){
					whichWeibo = new Qzone(WeiboBindActivity.this);
				}
				if (whichWeibo.isBinded()) {
					getDialog(whichWeibo);
				} else {
					startBind(whichWeibo);
				}
			}
		});
	}
	
	/**
	 * 绑定界面适配器
	 * @author duweibin
	 */
	private class WeiboAdapter extends BaseAdapter{
		public int getCount() {
			return ss.size();
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = (LayoutInflater.from(WeiboBindActivity.this).inflate(R.layout.bind_account_itemlist, null));
			}
			TextView titleTv = (TextView) convertView.findViewById(R.id.account_name);
			TextView bindTv = (TextView) convertView.findViewById(R.id.account_bind);
			ImageView imageView = (ImageView) convertView.findViewById(R.id.account_img);
			String weiboName = ss.get(position);
			if(weiboName.contains("新浪微博")){
				imageView.setImageDrawable(getResources().getDrawable(R.drawable.third_sina));
				titleTv.setText("新浪微博");
				if (new Sina(WeiboBindActivity.this).isBinded()) {
					bindTv.setText("解绑");
				} else {
					bindTv.setText("绑定");   
				}
			}else if(weiboName.contains("腾讯微博")){
				imageView.setImageDrawable(getResources().getDrawable(R.drawable.third_qq));
				titleTv.setText("腾讯微博");
				if (new Tencent(WeiboBindActivity.this).isBinded()) {
					bindTv.setText("解绑");
				} else {
					bindTv.setText("绑定");   
				}
			}else if(weiboName.contains("人人网")){
				imageView.setImageDrawable(getResources().getDrawable(R.drawable.third_renren));
				titleTv.setText("人人网");
				if (new Ren(WeiboBindActivity.this).isBinded()) {
					bindTv.setText("解绑");
				} else {
					bindTv.setText("绑定");   
				}
			}else if(weiboName.contains("空间")){
				imageView.setImageDrawable(getResources().getDrawable(R.drawable.qzone));
				titleTv.setText("QQ空间");
				if (new Qzone(WeiboBindActivity.this).isBinded()) {
					bindTv.setText("解绑");
				} else {
					bindTv.setText("绑定");   
				}
			}
			return convertView;
	         
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
	}
	
	
	/**
	 * 点击跳转到绑定页面
	 * @param entity
	 * @throws Exception 
	 */
	private void startBind(IShare listener){
		listener.bind();
	}
	/**
	 * 微博解绑对话框
	 * @param whichWeibo2 
	 */
	private void getDialog(final IShare whichWeibo2) {
		new AlertDialog.Builder(WeiboBindActivity.this).setTitle(R.string.cancle_bind_title).setMessage(R.string.cancle_bind_message)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				cancleBind(whichWeibo2);
			}
		}).setNegativeButton(R.string.cancel, null).show();
	}
	
	/**
	 * 取消绑定
	 * @param flag
	 */
	private void cancleBind(IShare shareWeibo){
		shareWeibo.cancelBind();
		weiboAdapter.notifyDataSetChanged();
	}
	
	 protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if (requestCode == 1) {
			Tencent tencent = new Tencent(this);
			tencent.saveInfo(resultCode, data);
		}
		if (Qzone.mTencent != null) {
			//QQ互联如果要成功接收到回调，需要在调用接口的Activity的onActivityResult方法中增加如下代码
			Qzone.mTencent.onActivityResult(requestCode, resultCode, data) ;
		}
	}
	 /**
	  * 当新浪微博和人人网绑定成功后会调用 这个方法,
	  * 在这里通知界面改变,模拟器上不会调用该方法
	  */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			weiboAdapter.notifyDataSetChanged();
		}
	}

	 
	 
	 
}
