package com.svo.laohan.fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.svo.laohan.R;
import com.svo.laohan.RankActivity;
import com.svo.platform.utils.HttpUtil;
import com.svo.platform.utils.PicUtil;
import com.tencent.mm.sdk.platformtools.Util;

public class Rank extends SherlockFragment implements OnItemClickListener{
	private View root;
	private GridView gridView;
	private SharedPreferences preferences;
	private JSONArray rankJson;
	private ProgressBar progressBar;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.rank, null);
		gridView = (GridView) root.findViewById(R.id.grid);
		progressBar = (ProgressBar) root.findViewById(R.id.progresBar);
		gridView.setOnItemClickListener(this);
		preferences = getActivity().getSharedPreferences("weitu", 0);
		try {
			String json = preferences.getString("rank_json", "");
			long weekMs = preferences.getLong("weekMs", 0);
			if (TextUtils.isEmpty(json) || Util.currentWeekInMills() > weekMs) {
				preferences.edit().putLong("weekMs", Util.currentWeekInMills()).commit();
				HttpUtil.get("http://2.dubinwei.duapp.com/Rank?flag=res", null, new JsonHttpResponseHandler(){
					@Override
					public void onStart() {
						super.onStart();
						progressBar.setVisibility(View.VISIBLE);
					}
					@Override
					public void onSuccess(JSONArray arg0) {
						super.onSuccess(arg0);
						rankJson = arg0;
						preferences.edit().putString("rank_json", arg0.toString()).commit();
						gridView.setAdapter(new MyAdapter(arg0));
						if (progressBar != null) {
							progressBar.setVisibility(View.GONE);
						}
					}
					@Override
					public void onFailure(Throwable arg0) {
						if (progressBar != null) {
							progressBar.setVisibility(View.GONE);
						}
					}
				});
			}else {
				rankJson = new JSONArray(json);
				gridView.setAdapter(new MyAdapter(rankJson));
				progressBar.setVisibility(View.GONE);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return root;
	}
	private class MyAdapter extends BaseAdapter{
		JSONArray arr;
		MyAdapter(JSONArray arg0){
			arr = arg0;
		}
		@Override
		public int getCount() {
			return arr.length();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(getActivity()).inflate(R.layout.rank_grid_item, null);
			ImageView img = (ImageView) convertView.findViewById(R.id.img);
			TextView resName = (TextView) convertView.findViewById(R.id.resTv);
			try {
				JSONObject jo = arr.getJSONObject(position);
				PicUtil.displayImage(getActivity(), jo.optString("img"), img);
				resName.setText(jo.optString("resName"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}
		
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(getActivity(), RankActivity.class);
		intent.putExtra("json", rankJson.toString());
		intent.putExtra("position", position);
		startActivity(intent);
	}
}
