package com.example.nestviewpager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.mylistview.view.MyLayout;

public class MainActivity extends Activity {

	private ViewPager viewpager;

	int[] colors = new int[]{0xffff0000, 0xff00ff00, 0xff0000ff};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewpager_layout);
		viewpager = (ViewPager) findViewById(R.id.viewpager);
		LayoutInflater inflater = LayoutInflater.from(this);
		List<View> list = new ArrayList<View>();
		View view = null, childView = null;
		ViewPager child_viewpager;
		TextView textview, testtextview;
		List<View> childlist = null;
		MyLayout mylayout;
		for (int i = 0; i < 3; i++) {
			view = inflater.inflate(R.layout.child_viewpager_layout, null);
			mylayout = (MyLayout) view.findViewById(R.id.mylayout);
			testtextview = (TextView) view.findViewById(R.id.testtextview);
			testtextview.setText("viewpager：" + i);
			mylayout.setBackgroundColor(colors[i]);
			list.add(view);
			child_viewpager = (ViewPager) view
					.findViewById(R.id.child_viewpager);
			// 注入里层viewpager
			mylayout.setChild_viewpager(child_viewpager);
			childlist = new ArrayList<View>();
			for (int j = 0; j < 3; j++) {
				childView = inflater.inflate(R.layout.child_viewpager_item, null);
				textview = (TextView) childView.findViewById(R.id.textview);
				textview.setText("view" + i + "：" + j);
				childView.setBackgroundColor(colors[j]);
				childlist.add(childView);
				child_viewpager.setAdapter(new ViewPagerAdapter(childlist));
			}
		}
		viewpager.setAdapter(new ViewPagerAdapter(list));
	}

	public class ViewPagerAdapter extends PagerAdapter {
		List<View> list;

		public ViewPagerAdapter(List<View> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup view, int position, Object object) {
			view.removeView(list.get(position));
		}

		// 当要显示的图片可以进行缓存的时候，会调用这个方法进行显示图片的初始化，我们将要显示的ImageView加入到ViewGroup中，然后作为返回值返回即可
		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			view.addView(list.get(position));
			return list.get(position);
		}
	}
}
