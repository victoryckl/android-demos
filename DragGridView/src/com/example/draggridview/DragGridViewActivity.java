package com.example.draggridview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.example.draggridview.DragAdapter.OnDragClickListener;
import com.example.draggridview.DragGridView.OnChanageListener;
import com.example.utils.MLog;

public class DragGridViewActivity extends Activity {
	private List<HashMap<String, Object>> mDataList = new ArrayList<HashMap<String, Object>>();
	private DragAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		init();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mAdapter.isEdit()) {
				mAdapter.setEdit(false);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void init() {
		DragGridView mDragGridView = (DragGridView) findViewById(R.id.dragGridView);
		for (int i = 0; i < 30; i++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			int id = ((i&1) == 0) ? R.drawable.com_tencent_big : R.drawable.music;
			item.put("item_image", id);
			item.put("item_text", "item " + Integer.toString(i));
			mDataList.add(item);
		}
		
		mAdapter = new DragAdapter(this, mDataList);
		mAdapter.setOnDragClickListener(mDragClickListener);
		
		mDragGridView.setAdapter(mAdapter);
		mDragGridView.setOnChangeListener(mChanageListener);
	}

	private OnChanageListener mChanageListener = new OnChanageListener() {
		
		@Override
		public void onStartDrag() {
			MLog.i("on start drag");
			mAdapter.setEdit(true);
		}
		
		@Override
		public void onStopDrag() {
			MLog.i("on stop drag");
			mAdapter.notifyDataSetChanged();
		}
		
		@Override
		public void onChange(int from, int to) {
			MLog.i("on change, "+from+" -> " +to);
			HashMap<String, Object> temp = mDataList.get(from);
			
			if(from < to){
				for(int i=from; i<to; i++){
					Collections.swap(mDataList, i, i+1);
				}
			}else if(from > to){
				for(int i=from; i>to; i--){
					Collections.swap(mDataList, i, i-1);
				}
			}
			
			mDataList.set(to, temp);
			mAdapter.notifyDataSetChanged();
		}
	};
	
	private OnDragClickListener mDragClickListener = new OnDragClickListener() {
		@Override
		public void onIcon(int position) {
			MLog.i("onIcon(), position: " + position);
			Toast.makeText(getApplicationContext(), 
					"onIcon(), pos: "+position, Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public void onDelete(int position) {
			MLog.i("onDelete(), position: " + position);
			mDataList.remove(position);
			if (mDataList.size() <= 0) {
				mAdapter.setEdit(false);
			}
			mAdapter.notifyDataSetChanged();
		}
	};
}
