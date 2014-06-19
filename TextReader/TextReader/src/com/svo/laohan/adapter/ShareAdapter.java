package com.svo.laohan.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.svo.laohan.R;

public class ShareAdapter extends BaseAdapter {
	private Activity context;
	private List<String> ss;
	public ShareAdapter(Activity context) {
		this.context = context;
		ss = new ArrayList<String>();
		ss.add("我的书籍");
		ss.add("我的图片");
		ss.add("我的音频");
		ss.add("我的视频");
	}

	@Override
	public int getCount() {
		return ss.size();
	}

	@Override
	public Object getItem(int position) {
		return ss.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = (View) LayoutInflater.from(context).inflate(
					R.layout.res_file_item_share, null);
			holder.textView1 = (TextView) view.findViewById(R.id.textView1);
			holder.typeImg = (ImageView) view.findViewById(R.id.type_img);
			view.setTag(holder);
		}else {
			holder = (ViewHolder) view.getTag();
		}
		String fileName = ss.get(position);
		holder.textView1.setText(fileName);
		holder.typeImg.setImageResource(R.drawable.filechooser_folder);
		return view;
	}
	private class ViewHolder{
		TextView textView1;
		ImageView typeImg;
	}
}