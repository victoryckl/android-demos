package com.svo.platform.picbrowser;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PicBrowseAdapter extends FragmentPagerAdapter {
	
	private ArrayList<String> picsPath;
	
	public PicBrowseAdapter(FragmentManager fm,ArrayList<String> picsPath) {
		super(fm);
		this.picsPath = picsPath;
	}

	@Override
	public int getCount() {
		return picsPath.size();
	}

	@Override
	public Fragment getItem(int position) {
		return PicFragment.newInstance(picsPath.get(position)) ;
	}
}
