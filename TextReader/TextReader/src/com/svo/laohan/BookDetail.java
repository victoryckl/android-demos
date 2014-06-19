package com.svo.laohan;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.svo.laohan.model.FileService;
import com.svo.laohan.model.Mjifen;
import com.svo.laohan.util.Constant;
import com.svo.laohan.util.FileUtil;
import com.svo.laohan.util.StringUtil;
import com.svo.platform.share.Share;
import com.svo.platform.utils.Constants;
import com.svo.platform.utils.PicUtil;
import com.svo.platform.utils.SvoToast;

public class BookDetail extends Activity{
	private String json;
	private TextView bookName;
	private TextView author;
	private TextView cat;
	private TextView score;
	private TextView downNum;
	private TextView intro;
	private ImageView cover;
	JSONObject jo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_detail);
		json = getIntent().getStringExtra("json");
		String resName = getIntent().getStringExtra("resName");
		bookName = (TextView) findViewById(R.id.bookName);
		author = (TextView) findViewById(R.id.author);
		cat = (TextView) findViewById(R.id.cat);
		score = (TextView) findViewById(R.id.score);
		score.setTextColor(Color.RED);
		downNum = (TextView) findViewById(R.id.downNum);
		downNum.setTextColor(Color.GRAY);
		intro = (TextView) findViewById(R.id.intro);
		cover = (ImageView) findViewById(R.id.cover);
		
		try {
			jo = new JSONObject(json);
			bookName.setText(jo.optString("name"));
			author.setText("作者:"+jo.optString("author"));
			cat.setText("分类:"+resName);
			downNum.setText(StringUtil.conDownNum(jo.optInt("downNum"))+"人下载");
			score.setText(jo.optDouble("score")+"分");
			String descrip = jo.optString("descrip");
			if (!TextUtils.isEmpty(descrip)) {
				descrip = descrip.replaceFirst("简介：内容预览", "内容预览");
			}
			intro.setText(Html.fromHtml(descrip));
			PicUtil.displayImage(this, jo.optString("imgUrl"), cover);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public void click(View view) {
		if (R.id.shareBtn == view.getId()) {
			Share share = new Share(BookDetail.this);
			Bundle bundle = new Bundle();
			bundle.putString("content", "我正在看《"+jo.optString("name")+"》，老汉阅读器里好书不少，一般人我不告诉他。"+Constants.REN_FROM_LINK);
			bundle.putString("imageUrl", jo.optString("imgUrl"));
			bundle.putString("title", "老汉阅读器，阅读的不只是文字");
			bundle.putString("url", Constants.REN_FROM_LINK);
			bundle.putString("pic", PicUtil.getPicFile(jo.optString("imgUrl")).getAbsolutePath());
			share.share(bundle , view);
		}else {
			String downUrl = jo.optString("htmlUrl");
			String suffix = "."+StringUtil.getSuffix(downUrl);
			String fileName = jo.optString("name");
			if (!fileName.endsWith(suffix)) {
				fileName = fileName+suffix;
			}
			boolean isExist = FileUtil.isFileExist(Constant.Save_path + fileName);
			if (isExist) {
				/*
				 * if (TypeUtil.getTypeBasePath(fileName) == TypeUtil.IMAGE ||
				 * TypeUtil.getTypeBasePath(fileName) == TypeUtil.GIF) { new
				 * FileService(getActivity()).browsePic(searchService.getList(),
				 * arg2); return; }
				 */
				new FileService(BookDetail.this).openFile(Constant.Save_path + fileName);
			} else {
				new FileService(BookDetail.this).downFile(jo.optString("htmlUrl"),jo.optString("name"));
			}
		}
		
	}
}
