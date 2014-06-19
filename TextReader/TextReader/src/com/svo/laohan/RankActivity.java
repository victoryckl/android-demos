package com.svo.laohan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;

import com.svo.laohan.fragment.RankBooks;
import com.viewpagerindicator.TabPageIndicator;

public class RankActivity extends FragmentActivity {
	private JSONArray arr;
	private int position;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.simple_tabs);
		String json = getIntent().getStringExtra("json");
		try {
			arr = new JSONArray(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		position = getIntent().getIntExtra("position", 0);
        FragmentPagerAdapter adapter = new GoogleMusicAdapter(getSupportFragmentManager());

        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);
        
        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        indicator.setCurrentItem(position);
	}
	 class GoogleMusicAdapter extends FragmentPagerAdapter {
	        public GoogleMusicAdapter(FragmentManager fm) {
	            super(fm);
	        }

	        @Override
	        public Fragment getItem(int position) {
	            try {
					JSONObject jsonObject = arr.getJSONObject(position);
					return RankBooks.newInstance(jsonObject,jsonObject.optString("resName","未知"));
				} catch (JSONException e) {
					e.printStackTrace();
					return null;
				}
	        }

	        @Override
	        public CharSequence getPageTitle(int position) {
	            try {
					return arr.getJSONObject(position).optString("resName","未知");
				} catch (JSONException e) {
					e.printStackTrace();
					return "未知";
				}
	        }

	        @Override
	        public int getCount() {
	          return arr.length();
	        }
	    }
}
