package com.svo.platform.picbrowser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.svo.platform.R;
import com.svo.platform.share.Share;
import com.svo.platform.utils.Constants;
import com.svo.platform.utils.FileUtil;
import com.svo.platform.utils.PicUtil;
import com.svo.platform.utils.SvoToast;
import com.tencent.mm.sdk.platformtools.NetStatusUtil;

public class PicBrowseActivity extends FragmentActivity{
	public static final String PICS = "pics";
	public static final String CUR_POSITION = "curPos";
	private TextView topTv; //顶部显示第几张View
	private PicViewPager viewPager; 
	private ImageView downImg; //底部下载图片
	protected ImageView shareImg; //底部分享图片
	private ImageView backImg;	//底部返回图片
	private RelativeLayout picbuttom;
	
	public ArrayList<String> picsPath; //图片的url或者本地路径
	protected int curPosition; //当前显示图片的下标
	public String subContent,title,url;
	private boolean isHideBtm;
	private boolean isLeft;//是否向左滑动
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.pic_browser);
		topTv = (TextView) findViewById(R.id.pic_toptext);
		viewPager = (PicViewPager) findViewById(R.id.pic_viewPager);
		downImg = (ImageView) findViewById(R.id.down_iv);
		shareImg = (ImageView) findViewById(R.id.share_iv);
		backImg = (ImageView) findViewById(R.id.back_iv);
		picbuttom = (RelativeLayout) findViewById(R.id.picbuttom);
		downImg.setOnClickListener(downListener);
		shareImg.setOnClickListener(shareListener);
		backImg.setOnClickListener(backListener);
		//接收传送过来的图片地址
		picsPath = getIntent().getStringArrayListExtra(PICS);
		curPosition = getIntent().getIntExtra("curPos", 0);
		subContent = getIntent().getStringExtra("subContent");
		title = getIntent().getStringExtra("title");
		url = getIntent().getStringExtra("url");
		/*if (TextUtils.isEmpty(title) || TextUtils.isEmpty(subContent) || TextUtils.isEmpty(url)) {
			isHideBtm = true;
			picbuttom.setVisibility(View.GONE);
		}*/
		topTv.setText((curPosition+1)+" / "+picsPath.size());
		PicBrowseAdapter adapter = new PicBrowseAdapter(getSupportFragmentManager(), picsPath);
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(curPosition);
		viewPager.setOnPageChangeListener(pageChangeListener);
		viewPager.setOnTouchListener(new OnTouchListener() {
			float oldX = 0;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (oldX < 1) {
					oldX  = event.getX();
				}else {
					isLeft = event.getX() - oldX > 0;
				}
				return false;
			}
		});
	}
	/**
	 * 底部与顶部的隐藏与显示
	 */
	public void hideBtm() {
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.gallery_menu_slide_out_to_bottom);
		if (topTv.getVisibility() == View.GONE) {
			topTv.setVisibility(View.VISIBLE);
		} else {
			topTv.setVisibility(View.GONE);
		}
		if (!isHideBtm) {
			if (picbuttom.getVisibility() == View.GONE) {
				picbuttom.setVisibility(View.VISIBLE);
			} else if (picbuttom.getVisibility() == View.VISIBLE) {
				picbuttom.startAnimation(animation);
				picbuttom.setVisibility(View.GONE);
			}
		}
	}
	private SimpleOnPageChangeListener pageChangeListener = new SimpleOnPageChangeListener() {
		/*用来判断是不是第一篇和最后一篇*/
        int max = 0;
		@Override
		public void onPageSelected(int position) {
			curPosition = position;
			topTv.setText((curPosition+1)+" / "+picsPath.size());
		}
		/*
         * 第一篇向左滑动和最后一篇向右滑动时，参数arg2永远为0.其他情况不会一直为0
         */
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
           if (max == 0) {
                max = Math.max(max, arg2);
            }
        }
		 @Override
         public void onPageScrollStateChanged(int arg0) {
             if (arg0 == 0) { //表示滑动已经停止
            	 if (max == 0) { 
	                 if (viewPager.getCurrentItem() == 0) {
						if (viewPager.getChildCount() == 1) {
							if (isLeft) {
								pre();
							}else {
								next();
							}
						}else {
							pre();
						}
	                 }else{
	                     next();
	                 }
	                 return;
            	 }
            	 max = 0;
             }
         }
	};
	private ImageView.OnClickListener backListener = new ImageView.OnClickListener(){

		@Override
		public void onClick(View v) {
			PicBrowseActivity.this.finish();
		}
	};
	//从图片浏览器分享图片和文字到微博
	private ImageView.OnClickListener shareListener = new ImageView.OnClickListener(){

		@Override
		public void onClick(View v) {
			if(!NetStatusUtil.isConnected(PicBrowseActivity.this)){
				Toast.makeText(PicBrowseActivity.this, "无法连接到网络  ,请检查网络配置", Toast.LENGTH_LONG).show();
			}else{
				Share share = new Share(PicBrowseActivity.this);
				Bundle bundle = new Bundle();
				bundle.putString("content", subContent);
				bundle.putString("imageUrl", picsPath.get(viewPager.getCurrentItem()));
				bundle.putString("title", title);;
				bundle.putString("pic", PicUtil.getPicFile(picsPath.get(viewPager.getCurrentItem())).getAbsolutePath());
				bundle.putString("url", url);
				share.share(bundle,v);
			}
		}
	};
	
	
	// 保存图片到pictures目录下
	private ImageView.OnClickListener downListener = new ImageView.OnClickListener() {

		@Override
		public void onClick(View v) {
			String picUri = picsPath.get(viewPager.getCurrentItem());
			File sourceFile = PicUtil.getPicFile(picUri);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
			File dir = new File(Constants.SAVE_PIC_PATH);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File targetFile = new File(Constants.SAVE_PIC_PATH, format.format(new Date()) + ".jpg");
			boolean flag = FileUtil.copyFile(sourceFile,	targetFile);
			if (flag) {
				Toast.makeText(PicBrowseActivity.this, "已保存到"+Constants.SAVE_PIC_PATH_NAME+"目录下", Toast.LENGTH_SHORT).show();
			}else {
				Toast.makeText(PicBrowseActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
			}
		}
	};

	protected void pre() {
		SvoToast.showHint(PicBrowseActivity.this, "加载上一组", Toast.LENGTH_SHORT);
	}
	protected void next() {
		SvoToast.showHint(PicBrowseActivity.this, "加载下一组", Toast.LENGTH_SHORT);
	}
}
