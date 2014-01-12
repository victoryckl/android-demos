package com.example.draggridview;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DragAdapter extends BaseAdapter {
	private Context mContext;
	private List<HashMap<String, Object>> mList;
	
	public DragAdapter(Context context, List<HashMap<String, Object>> list) {
		mContext = context;
		mList = list;
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.grid_item, null);
			
			holder = new Holder();
			holder.icon = (ImageView) convertView.findViewById(R.id.item_image);
			holder.text = (TextView) convertView.findViewById(R.id.item_text);
			
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		HashMap<String, Object> item = mList.get(position);
		holder.icon.setImageResource((Integer) item.get("item_image"));
		holder.text.setText("# " + item.get("item_text"));
		
		return convertView;
	}
	
	private class Holder {
		private ImageView icon;
		private TextView text;
	}
}
