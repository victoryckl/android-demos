package com.svo.laohan.model;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.geometerplus.android.fbreader.FBReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.svo.laohan.MyListActivity;
import com.svo.laohan.model.dao.DBHelper;
import com.svo.laohan.model.dao.FileData;
import com.svo.laohan.model.dao.entity.FileEntity;
import com.svo.laohan.util.Constant;
import com.svo.laohan.util.NetStateUtil;
import com.svo.laohan.util.StringUtil;
import com.svo.laohan.util.TypeUtil;
import com.svo.platform.picbrowser.PicBrowseActivity;
import com.svo.platform.utils.HttpUtil;

/**
 * @author duweibin 
 * @version 创建时间：2012-11-13 下午12:48:55
 * 类说明
 */
public class FileService {
	private Context context;
	private ProgressDialog dialog;
	public FileService(Context context) {
		this.context = context;
	}
	/**
	 * 同步更新本地文件
	 * @param resJsonUrl
	 * @return 返回True表示更新数据库了,否则没有更新
	 */
	public boolean updateRes(String path) {
		String json = HttpUtil.getRequest(Constant.gen2+"Bcs?path="+path+"&count=0");
		if (!TextUtils.isEmpty(json)) {
			new FileData(context).delOldFile(path);
			try {
				new FileData(context).insert(new JSONArray(json));
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	public void updateDown(String resName, String fileName) {
		DBHelper dbHelper = DBHelper.getInstance(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String sql = "update file set down=? where name=? and resName=?";
		db.execSQL(sql, new String[]{"yes",fileName,resName});
	}
	/**
	 * 下载文件
	 * @param uri 远程路径或者网络地址
	 * @param name 文件名，不带后缀名
	 */
	public void downFile(String uri, String name) {
		if (!NetStateUtil.isNetworkAvailable(context)) {
			Toast.makeText(context, "网络不可用", Toast.LENGTH_SHORT).show();
			return;
		}
		final FileTask task = new FileTask();
		dialog=ProgressDialog.show(context, "稍候", "正在下载……", true, true, new DialogInterface.OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				task.cancel(true);
			}
		});
		dialog.setCanceledOnTouchOutside(false);
		task.execute(uri,name);
	}
	
	/**
	 * 打开某文件
	 * @param filePath
	 */
	public void openFile(String filePath){
		int fileType = TypeUtil.getTypeBasePath(filePath);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		switch (fileType) {
		case TypeUtil.DOC:
			context.startActivity(new Intent(context, FBReader.class)
			.setAction(Intent.ACTION_VIEW)
			.putExtra(FBReader.BOOK_PATH_KEY, filePath)
			.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			break;
		case TypeUtil.IMAGE:
		case TypeUtil.GIF:
//			intent.setDataAndType(Uri.parse("file:"+filePath), "image/*");
//			context.startActivity(Intent.createChooser(intent, "选择"));
			Intent picIntent = new Intent(context, PicBrowseActivity.class);
			ArrayList<String> pics = new ArrayList<String>();
			pics.add(filePath);
			picIntent.putExtra("pics", pics);
			context.startActivity(picIntent);
			break;
		case TypeUtil.AUDIO:
			intent.setDataAndType(Uri.parse("file:"+filePath), "audio/*");
			context.startActivity(Intent.createChooser(intent, "选择"));
			break;
		case TypeUtil.VIDEO:
			intent.setDataAndType(Uri.parse("file:"+filePath), "video/*");
			context.startActivity(Intent.createChooser(intent, "选择"));
			break;
		case TypeUtil.APK:
			intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive"); 
			context.startActivity(intent);
			break;
		/*case TypeUtil.GIF:
			intent.setClass(context, GifActivity.class);
			intent.putExtra("path", filePath);
			context.startActivity(intent);
			break;*/
		default:
			break;
		}

	}
	
	/**
	 * 请求网络某目录下文件
	 * @return 
	 */
	public void reqFiles(String path,AsyncHttpResponseHandler responseHandler) {
		 reqFiles(path, 0,responseHandler);
	}
	public void reqFiles(String path,int count,AsyncHttpResponseHandler responseHandler) {
		HttpUtil.get(Constant.gen2+"Bcs?path="+path+"&count="+count, null, responseHandler);
	}
	private class FileTask extends AsyncTask<String, Void, String>{
		@Override
		protected String doInBackground(String... params) {
			String downUrl = params[0];
			String name = params[1];//不带后缀名
			if (TextUtils.isEmpty(downUrl)) {
				return null;
			}
			downUrl=StringUtil.encode(downUrl);
			String fileName = name +"."+ StringUtil.getSuffix(downUrl);
			String filePath = Constant.Save_path+fileName;
			File dirFile = new File(Constant.Save_path);
			if (!dirFile.exists()) {
				dirFile.mkdirs();
			}
			File resultFile = com.svo.platform.utils.FileUtil.saveFile(downUrl,
					Constant.Save_path, fileName);
			if (resultFile != null && resultFile.exists() && resultFile.length()>1) {
				return filePath;
			}else {
				return null;
			}
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			try {
				if (result != null) {
					openFile(result);
					Toast.makeText(context, "下载成功,消耗1个比特币", Toast.LENGTH_SHORT).show();
					new Mjifen(context).sub(1);
				} else {
					Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 搜索页
	 * @param list
	 * @param arg2
	 */
	public void browsePic(JSONObject json, int arg2) {
		ArrayList<String> urlList = new ArrayList<String>();
		JSONArray booksArr = new JSONArray();
		try {
			booksArr = new JSONArray(json.optString("books"));
			for (int i = 0; i < booksArr.length(); i++) {
				JSONObject jo = booksArr.getJSONObject(i);
				String url = jo.optString("htmlUrl");
				if (TypeUtil.getTypeBasePath(url) == TypeUtil.IMAGE || TypeUtil.getTypeBasePath(url) == TypeUtil.GIF) {
					urlList.add(url);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Intent intent = new Intent(context, PicBrowseActivity.class);
		intent.putExtra("pics", urlList);
		intent.putExtra("curPos", arg2);
		intent.putExtra("subContent","老汉阅读，阅读的不只是文字");
		intent.putExtra("title","老汉阅读，阅读的不只是文字");
		intent.putExtra("url",Constant.LAOHAN_SITE);
		context.startActivity(intent);
	}
	/**
	 * MylistActivity页浏览图片
	 * @param names 
	 * @param curPath 当前路径，可能包含中文.绝对路径
	 * @param filePath 当前点击的文件 路径 .绝对路径
	 */
	public void browsePic(List<FileEntity> names, String curPath, String filePath) {
		ArrayList<String> list = new ArrayList<String>();
		int index = 0;
		for (FileEntity entity : names) {
			String url = "http://bcs.duapp.com" +entity.getParDir()+entity.getFileName();
			if (TypeUtil.getTypeBasePath(url) == TypeUtil.IMAGE || TypeUtil.getTypeBasePath(url) == TypeUtil.GIF) {
				list.add(StringUtil.encode(url));
			}
		}
		Intent intent = new Intent(context, PicBrowseActivity.class);
		intent.putExtra("pics", list);
		intent.putExtra("curPos", index);
		intent.putExtra("subContent","老汉阅读，阅读的不只是文字");
		intent.putExtra("title","老汉阅读，阅读的不只是文字");
		intent.putExtra("url",Constant.LAOHAN_SITE);
		context.startActivity(intent);
	}
	/**
	 * 请求文件，同步
	 * @param path
	 * @return
	 */
	public boolean reqFiles(String path) {
		Log.i("weitu", "path:"+path);
		String json = HttpUtil.getRequest(Constant.gen2+"Bcs?path="+path);
		Log.i("weitu", "json:"+json);
		if (!TextUtils.isEmpty(json)) {
			new FileData(context).delOldFile(path);
			try {
				new FileData(context).insert(new JSONArray(json));
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
}
