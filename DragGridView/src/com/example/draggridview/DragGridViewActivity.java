package com.example.draggridview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.SimpleAdapter;

import com.example.draggridview.DragGridView.OnChanageListener;
import com.example.utils.MLog;

public class DragGridViewActivity extends Activity {
	private List<HashMap<String, Object>> mDataList = new ArrayList<HashMap<String, Object>>();
	SimpleAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		DragGridView mDragGridView = (DragGridView) findViewById(R.id.dragGridView);
		for (int i = 0; i < 30; i++) {
			HashMap<String, Object> itemHashMap = new HashMap<String, Object>();
			itemHashMap.put("item_image",R.drawable.com_tencent_open_notice_msg_icon_big);
			itemHashMap.put("item_text", "拖拽 " + Integer.toString(i));
			mDataList.add(itemHashMap);
		}

		mAdapter = new SimpleAdapter(this, mDataList,
				R.layout.grid_item, new String[] { "item_image", "item_text" },
				new int[] { R.id.item_image, R.id.item_text });
		
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
			//这里的处理需要注意下
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
