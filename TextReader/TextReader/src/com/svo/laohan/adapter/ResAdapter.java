package com.svo.laohan.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.svo.laohan.R;
import com.svo.laohan.util.StringUtil;

public class ResAdapter extends BaseAdapter{
		private Context context;
		private ArrayList<String> names;
		public ResAdapter(Context context, ArrayList<String> names) {
			this.context = context;
			this.names = names;
		}
		@Override
		public int getCount() {
			return names.size();
		}

		@Override
		public Object getItem(int position) {
			return names.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.scan_item, null);
			}
			TextView textView = (TextView) convertView.findViewById(R.id.res_name);
			String fileName = names.get(position);
			if (fileName.endsWith("/")) {
				fileName = fileName.substring(0, fileName.length()-1);
				fileName = StringUtil.sepPath(fileName)[1];
			}
			textView.setText(fileName);
			return convertView;
		}
		
	}