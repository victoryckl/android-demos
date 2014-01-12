package com.example.draggridview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;

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
	
	private void init() {
		DragGridView mDragGridView = (DragGridView) findViewById(R.id.dragGridView);
		for (int i = 0; i < 30; i++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("item_image",R.drawable.com_tencent_open_notice_msg_icon_big);
			item.put("item_text", "item " + Integer.toString(i));
			mDataList.add(item);
		}
		
		mAdapter = new DragAdapter(this, mDataList);
		mDragGridView.setAdapter(mAdapter);
		mDragGridView.setOnChangeListener(mChanageListener);
	}

	private OnChanageListener mChanageListener = new OnChanageListener() {
		
		@Override
		public void onStartDrag() {
			MLog.i("on start drag");
		}
		
		@Override
		public void onStopDrag() {
			MLog.i("on stop drag");
		}
		
		@Override
		public void onChange(int from, int to) {
			MLog.i("on change");
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
}
