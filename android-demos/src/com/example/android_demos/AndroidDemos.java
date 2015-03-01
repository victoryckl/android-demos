package com.example.android_demos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class AndroidDemos extends Activity {
	private final static String TAG = AndroidDemos.class.getSimpleName();
	private ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_android_demos);
		
//		SToast.show(this, "OnCreate()");
//		StackTrace.printStackTrace();
//		
//		new Thread(){
//			public void run() {
//				StackTrace.printStackTrace();
//			};
//		}.start();
		
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.android_demos, menu);
//		SToast.show(this, "onCreateOptionsMenu()");
		return true;
	}

	private void init() {
		mListView = (ListView)findViewById(R.id.lv_function);
		
		SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), 
				getData(), 
				R.layout.listview_item, 
				new String[] { "title" },
                new int[] { R.id.tv_title });
		
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(mItemClickListener);
	}
	
	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		addItem(data, "带清除按钮的编辑框", activityIntent(EditActivity.class.getName()));
		addItem(data, "带文字的图片按钮", activityIntent(TextImageButtonActivity.class.getName()));
		addItem(data, "标题栏弹出框", activityIntent(EditActivity.class.getName()));
		addItem(data, "获取资源数组中的资源id", activityIntent(ArrayResIdActivity.class.getName()));
		addItem(data, "String.replace测试", activityIntent(StringReplaceTestActivity.class.getName()));
		return data;
	}
	
    protected void addItem(List<Map<String, Object>> data, String name, Intent intent) {
        Map<String, Object> temp = new HashMap<String, Object>();
        temp.put("title", name);
        temp.put("intent", intent);
        data.add(temp);
    }
    
    protected Intent activityIntent(String componentName) {
        Intent result = new Intent();
        result.setClassName(getPackageName(), componentName);
        return result;
    }
    
    private OnItemClickListener mItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, 
				View view, int position, long id) {
			Map<String, Object> map = (Map<String, Object>)parent.getItemAtPosition(position);
	        Intent intent = (Intent) map.get("intent");
	        if (intent != null) {
	        	startActivity(intent);
	        }
		}
    	
	};
//    @Override
//    protected void onListItemClick(ListView l, View v, int position, long id) {
//        Map<String, Object> map = (Map<String, Object>)l.getItemAtPosition(position);
//
//        Intent intent = (Intent) map.get("intent");
//        startActivity(intent);
//    }
	
//	private OnClickListener mBtnClickListener = new OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			Intent intent = new Intent();
//			switch (v.getId()) {
//			case R.id.btn_go_to_edit:
//				intent.setClass(AndroidDemos.this, EditActivity.class);
//				startActivity(intent);
//				break;
//			case R.id.btn_go_to_imagebutton:
//				break;
//			case R.id.btn_go_to_title:
//				break;
//			case R.id.btn_go_to_get_res:
//				intent.setClass(AndroidDemos.this, ArrayResIdActivity.class);
//				startActivity(intent);
//				break;
//			default:
//				break;
//			}
//		}
//	};
}
