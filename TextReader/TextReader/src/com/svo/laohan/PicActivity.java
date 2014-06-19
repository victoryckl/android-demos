/*package com.svo.laohan;

import com.actionbarsherlock.app.SherlockActivity;

public class PicActivity extends SherlockActivity {
	private GalleryViewPager mViewPager;
	private ArrayList<String> items;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pic_browse);
		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
		items = (ArrayList<String>) getIntent().getSerializableExtra("urls");
		int index = getIntent().getIntExtra("index", 0);
        UrlPagerAdapter pagerAdapter = new UrlPagerAdapter(this, items);
        mViewPager = (GalleryViewPager)findViewById(R.id.viewer);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(index);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Sina sina = new Sina(this);
		if (sina != null && sina.getSsoHandler() != null) {
			sina.getSsoHandler().authorizeCallBack(requestCode, resultCode, data);
		}
		if (requestCode == 1) {
			Tencent tencent = new Tencent(this);
			tencent.saveInfo(resultCode, data);
		}
		if (Qzone.mTencent != null) {
			//QQ互联如果要成功接收到回调，需要在调用接口的Activity的onActivityResult方法中增加如下代码
			Qzone.mTencent.onActivityResult(requestCode, resultCode, data) ;
		}
	}
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("分享").setIcon(R.drawable.ic_menu_share).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String title = item.getTitle().toString();
		if ("分享".equals(title)) {
			WeiboShare share = new WeiboShare(this);
			Bundle bundle = new Bundle();
			bundle.putString("content", Constants.SHARE_CONTENT);
			bundle.putString("title", "老汉阅读器,阅读的不只是文字"); 
			bundle.putString("url", Constants.REN_FROM_LINK); 
			File file = ImageLoader.getInstance().getDiscCache().get(items.get(mViewPager.getCurrentItem()));
			bundle.putString("picPath", file.getAbsolutePath()); 
			bundle.putString("imageUrl", "http://1.dubinwei.duapp.com/static/share.png");
			share.share(bundle);
		}
		return super.onOptionsItemSelected(item);
	}
}
*/