package com.svo.laohan.fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.svo.laohan.R;
import com.svo.platform.utils.PicUtil;

public class MyAdapter extends BaseAdapter {
	JSONArray jsonArray;
	Context context;
	public MyAdapter(FragmentActivity fragmentActivity, JSONArray jsonArray){
		this.jsonArray = jsonArray;
		context = fragmentActivity;
	}
	@Override
	public int getCount() {
		return jsonArray.length();
	}

	@Override
	public Object getItem(int position) {
		try {
			return jsonArray.getJSONObject(position);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(context).inflate(R.layout.book_item, null);
		TextView nameTv = (TextView) convertView.findViewById(R.id.name);
		TextView authorTv = (TextView) convertView.findViewById(R.id.author);
		TextView introTv = (TextView) convertView.findViewById(R.id.intro);
		ImageView img = (ImageView) convertView.findViewById(R.id.cover);
		try {
			JSONObject jo = jsonArray.getJSONObject(position);
			nameTv.setText(jo.optString("name"));
			authorTv.setText(jo.optString("author"));
			introTv.setText(Html.fromHtml(jo.optString("descrip","暂无简介")));
			PicUtil.displayImage(context, jo.optString("imgUrl"), img);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return convertView;
	}

}
