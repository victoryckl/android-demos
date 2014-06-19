package com.svo.laohan.model;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.svo.laohan.adapter.SearchAdapter;
import com.svo.laohan.util.Constant;
import com.svo.laohan.util.NetStateUtil;
import com.svo.platform.utils.HttpUtil;
import com.svo.platform.utils.SvoToast;

public class SearchService {
	private Activity activity;
	private JSONObject searchRs;//当前搜索结果
	public JSONObject getList() {
		return searchRs;
	}
	public SearchService(Activity activity) {
		this.activity = activity;
	}
	/**
	 * 异步搜索
	 * @param mbRootPath
	 * @param key
	 * @param i 
	 */
	public void search(final String key,final ListView listView){
		if (!NetStateUtil.isNetworkAvailable(activity)) {
			Toast.makeText(activity, "网络不可用", Toast.LENGTH_LONG).show();
			return;
		}
		int vip = new Mjifen(activity).isVip()?1:0;
		HttpUtil.get(Constant.gen2+"Search2?key="+key+"&vip="+vip, null, new JsonHttpResponseHandler(){
			ProgressDialog dialog;
			@Override
			public void onStart() {
				super.onStart();
				dialog=ProgressDialog.show(activity, null, "让搜索飞一会……", true,true);
				dialog.setCanceledOnTouchOutside(false);
			}
			@Override
			public void onSuccess(JSONObject json) {
				super.onSuccess(json);
				searchRs = json;
				if (json == null || json.length() < 1) {
					SvoToast.showHint(activity, "搜索结果为空", Toast.LENGTH_LONG);
					return;
				}
				SearchAdapter adapter = new SearchAdapter(activity, json);
				listView.setAdapter(adapter);
				dialog.cancel();
			}
			@Override
			public void onFailure(Throwable arg0, JSONObject arg1) {
				super.onFailure(arg0, arg1);
				SvoToast.showHint(activity, "请求超时", Toast.LENGTH_LONG);
				dialog.cancel();
			}
		});
	}
}
