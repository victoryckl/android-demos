package com.example.draggridview;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
			holder.delete = (ImageButton) convertView.findViewById(R.id.ib_delete);
			
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		HashMap<String, Object> item = mList.get(position);
		holder.icon.setImageResource((Integer) item.get("item_image"));
		holder.text.setText("# " + item.get("item_text"));
		
		holder.icon.setOnClickListener(mClickListener);
		holder.delete.setOnClickListener(mClickListener);
		holder.delete.setVisibility(isEdit ? View.VISIBLE : View.INVISIBLE);
		
		holder.icon.setTag(position);
		holder.text.setTag(position);
		holder.delete.setTag(position);
		
		return convertView;
	}
	
	private class Holder {
		private ImageView icon;
		private TextView text;
		private ImageButton delete;
	}
	
	//-----------------------------------
	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.item_image:
				if (mDragClickListener != null) {
					mDragClickListener.onIcon((Integer) v.getTag());
				} else {
					Toast.makeText(mContext, "onclick, item_image", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.ib_delete:
				if (mDragClickListener != null) {
					mDragClickListener.onDelete((Integer) v.getTag());
				} else {
					Toast.makeText(mContext, "onclick, ib_delete", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
		}
	};
	
	private OnDragClickListener mDragClickListener;
	public interface OnDragClickListener {
		void onIcon(int position);
		void onDelete(int position);
	}
	public void setOnDragClickListener(OnDragClickListener l) {
		mDragClickListener = l;
	}
	
	//-----------------------------------
	private boolean isEdit = false;
	public boolean isEdit() {
		return isEdit;
	}
	public void setEdit(boolean edit) {
		if (isEdit != edit) {
			isEdit = edit;
			notifyDataSetChanged();
		}
	}
}
