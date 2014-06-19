package com.svo.laohan;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.svo.laohan.fragment.Search;

public class SearchActivity extends SherlockFragmentActivity {
	
	private Search search;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.fram);
		search = new Search();
		search.setArguments(getIntent().getExtras());
		getSupportFragmentManager().beginTransaction().replace(R.id.root, search).commit();
	}
	@Override
	protected void onResume() {
		super.onResume();
		search.search(getIntent().getExtras().getString("key"));
	}
}
