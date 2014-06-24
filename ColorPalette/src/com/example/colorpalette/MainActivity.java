package com.example.colorpalette;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.colorpalette.ResMgr.ColorItem;

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	
	private ResMgr mResMgr;
	private GridView mGridView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mResMgr = new ResMgr(getApplicationContext());
		List<ColorItem> colors = mResMgr.getColors();
		GridViewAdapter adapter = new GridViewAdapter(getApplicationContext(), colors);
		
		mGridView = (GridView)findViewById(R.id.gridview); 
		mGridView.setAdapter(adapter);
	}
	
	class GridViewAdapter extends BaseAdapter {
		// 上下文对象
		private Context context;
		private List<ColorItem> colors;

		// 图片数组
		private Integer[] imgs = { 0 };

		GridViewAdapter(Context context, List<ColorItem> colors) {
			this.context = context;
			this.colors = colors;
		}

		public int getCount() {
			return colors.size();
		}

		public Object getItem(int item) {
			return item;
		}

		public long getItemId(int id) {
			return id;
		}

		// 创建View方法
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder h = null;
			if (convertView == null) {
				h = new Holder();
				
				View v = View.inflate(context, R.layout.gridview_item, null);
				h.name  = (TextView) v.findViewById(R.id.item_name);
				h.color = (TextView) v.findViewById(R.id.item_color);
				
				convertView = v;
				convertView.setTag(h);
			} else {
				h = (Holder)convertView.getTag();
			}
			
			ColorItem c = colors.get(position);
			h.name.setText(c.name+"\n0x"+Integer.toHexString(c.color));
			h.color.setBackgroundColor(c.color);
			
			return convertView;
		}
		
		private int inversePass(int pass) {
			int passR;
	        //转换  
            passR = (255 - pass);
            //均小于等于255大于等于0
            if(passR > 255){
                passR = 255;
            } else if(passR < 0){
                passR = 0;
            }
            return passR;
		}
		
		private int inversePixel(int color) {
            return Color.argb(
            		Color.alpha(color), 
            		inversePass(Color.red(color)), 
            		inversePass(Color.green(color)), 
            		inversePass(Color.blue(color)));
		}
		
		class Holder {
			TextView name,color;
		}
	}
}
