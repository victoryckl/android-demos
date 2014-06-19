package com.svo.laohan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import cn.waps.AppConnect;

import com.svo.laohan.model.Mjifen;
import com.svo.laohan.util.Constant;
import com.svo.platform.share.Share;
import com.svo.platform.utils.Constants;

public class JifenActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jifen);
	}
	public void click(View view) {
		if (view.getId() == R.id.downApp) {
			AppConnect.getInstance(this).showOffers(this);
		} else {
			Bundle bundle = new Bundle();
			bundle.putString("content", Constants.SHARE_CONTENT);
			bundle.putString("imageUrl", Constant.share_pic);
			bundle.putString("title", "老汉阅读器，阅读的不只是文字");
			bundle.putString("url", Constants.REN_FROM_LINK);
			new Share(this).share(bundle, view);
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			String from = data.getStringExtra("from");
			//根据分享结果，看是否要增加积分
			new Mjifen(JifenActivity.this).handShare(from);
		}
	}
}
