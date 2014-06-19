package com.svo.laohan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import cn.waps.AppConnect;
import cn.waps.UpdatePointsNotifier;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.svo.laohan.fragment.Rank;
import com.svo.laohan.fragment.Scan;
import com.svo.laohan.fragment.Search;
import com.svo.laohan.fragment.Share;
import com.svo.laohan.model.Mjifen;
import com.svo.platform.share.model.Qzone;
import com.svo.platform.share.model.Tencent;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

public class HomeTabActivity extends SherlockFragmentActivity implements ActionBar.TabListener,UpdatePointsNotifier {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fram);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		for (int i = 1; i <= 4; i++) {
			ActionBar.Tab tab = getSupportActionBar().newTab();
			tab.setText("排行");
			if (i == 2) {
				tab.setText("搜索");
			} else if (i == 3) {
				tab.setText("浏览");
			} else if (i == 4) {
				tab.setText("我");
			}
			tab.setTabListener(this);
			getSupportActionBar().addTab(tab);
		}
		AppConnect.getInstance(this).getPoints(this);
		UmengUpdateAgent.update(this);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
	}
	@Override
	public void getUpdatePoints(final String arg0, final int arg1) {
			new Mjifen(this).saveJifen(arg1);
	}
	@Override
	public void getUpdatePointsFailed(String arg0) {
//		SvoToast.showHint(this, "获取积分失败", Toast.LENGTH_SHORT);
	}
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	private Share shareFragment;
	private Search search;
	private Scan scan;
	private Rank rank;
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (tab.getText().equals("浏览")) {
			if (null == scan) {
				scan = new Scan();
			}
			ft.replace(R.id.root, scan);
		} else if (tab.getText().equals("我")) {
			if (null == shareFragment) {
				shareFragment = new Share();
			}
			ft.replace(R.id.root, shareFragment);
		} else if (tab.getText().equals("搜索")) {
			if (null == search) {
				search = new Search();
			}
			ft.replace(R.id.root, search);
		}  else if (tab.getText().equals("排行")) {
			if (null == rank) {
				rank = new Rank();
			}
			ft.replace(R.id.root, rank);
		} 
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {

	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/*Sina sina = new Sina(this);
		if (sina != null && sina.getSsoHandler() != null) {
			sina.getSsoHandler().authorizeCallBack(requestCode, resultCode, data);
		}*/
		if (requestCode == 1) {
			Tencent tencent = new Tencent(this);
			tencent.saveInfo(resultCode, data);
		}
		if (Qzone.mTencent != null) {
			//QQ互联如果要成功接收到回调，需要在调用接口的Activity的onActivityResult方法中增加如下代码
			Qzone.mTencent.onActivityResult(requestCode, resultCode, data) ;
		}
	}
	/*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("分享").setIcon(R.drawable.ic_menu_share).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String title = item.getTitle().toString();
		if ("分享".equals(title)) {
			share();
		}
		return super.onOptionsItemSelected(item);
	}
	public void share() {
		Bundle bundle = new Bundle();
		bundle.putString("content", Constants.SHARE_CONTENT);
		bundle.putString("title", "老汉阅读器,不错,可以体验一下"); 
		bundle.putString("url", Constants.REN_FROM_LINK); 
		bundle.putString("imageUrl", "http://1.dubinwei.duapp.com/static/share.png");
		new com.svo.platform.share.Share(this).share(bundle);
	}*/
}
