package com.svo.laohan;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.json.JSONArray;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.svo.laohan.adapter.FileAdapter;
import com.svo.laohan.model.FileService;
import com.svo.laohan.model.dao.FileData;
import com.svo.laohan.model.dao.entity.FileEntity;
import com.svo.laohan.util.Constant;
import com.svo.laohan.util.FileUtil;
import com.svo.laohan.util.NetStateUtil;
import com.svo.laohan.util.StringUtil;
import com.svo.laohan.util.TypeUtil;
import com.svo.platform.widget.PushRefreshListView;
import com.umeng.analytics.MobclickAgent;

public class MyListActivity extends SherlockActivity {
	private List<FileEntity> names = new ArrayList<FileEntity>();
	private Stack<String> pathStack = new Stack<String>();
	private PushRefreshListView listView;
	private FileData fileData;
	private String resName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mylist);
		init();
		refresh(true);
		/** 下拉监听 */
		listView.setonRefreshListener(new PushRefreshListView.OnRefreshListener() {
			public void onRefresh() {
				load();
			}
		});
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
					FileEntity fileEntity = names.get(position-1);
					if (fileEntity.isDir()) {
						pathStack.push(fileEntity.getFileName());
						refresh(true);
						setTitle(fileEntity.getFileName());
					} else {
						String fileName = fileEntity.getFileName();
						String filePath = fileName;
						if (TypeUtil.getTypeBasePath(fileName) == TypeUtil.IMAGE || TypeUtil.getTypeBasePath(fileName) == TypeUtil.GIF) {
							new FileService(MyListActivity.this).browsePic(names,pathStack.peek(),filePath);
//							String accessToken = PcsApi.getInstance(MyListActivity.this).getOauthInfo().getAccessToken();
//							filePath = "https://pcs.baidu.com/rest/2.0/pcs/file?method=download&access_token="+accessToken+"&path="+URLEncoder.encode(filePath);
//							new FileService(MyListActivity.this).openFile(filePath);
							return;
						}
						FileService fileService = new FileService(MyListActivity.this);
						if (fileEntity.isDown()) {
							boolean isExist = FileUtil.isFileExist(fileEntity.getLocalPath());
							if (isExist) {
								fileService.openFile(Constant.Save_path+fileName);
							} else {
								fileData.delDown(filePath);
								fileService.downFile(fileName,StringUtil.delSuffix(StringUtil.sepPath(fileName)[1]));
							}
						} else {
							fileService.downFile(fileName,StringUtil.delSuffix(StringUtil.sepPath(fileName)[1]));
						}
					}
			}
		});
	}

	/**
	 * findview,接收参数,初始化
	 */
	private void init() {
		listView = (PushRefreshListView) findViewById(R.id.filelist);
		fileData = new FileData(this);
		String curPath = getIntent().getStringExtra("rePath");
		Log.i("weitu", "first curpath:"+curPath);
		pathStack.push(curPath);
		resName = StringUtil.sepPath(curPath)[1];
		setTitle(resName);
	}

	/**
	 * 本地刷新 
	 */
	private void refresh(boolean isReqNet) {
		names = fileData.queryFiles("select * from file  where parDir in (?,?)  order by mtime desc", new String[]{pathStack.peek(),pathStack.peek()+"/"});
		FileAdapter adapter = new FileAdapter(this, names);
		listView.setAdapter(adapter);
		if (isReqNet) {
			if (names.size() == 0) {
				load();
			}
		}
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
	public void onBackPressed() {
		if (pathStack.size() > 1) {
			pathStack.pop();
			refresh(false);
			setTitle(getUpTitle(getTitle().toString()));
		} else {
			super.onBackPressed();
		}
	}
	//得到上一级标题
	private String getUpTitle(String title){
		return title.substring(0,title.lastIndexOf("/"));
	}
	private void load() {
		if (!NetStateUtil.isNetworkAvailable(MyListActivity.this)) {
			Toast.makeText(MyListActivity.this, "网络不可用", Toast.LENGTH_LONG).show();
			return;
		}
		Log.i("weitu", "curPath:"+pathStack.peek());
		new FileService(getApplicationContext()).reqFiles(pathStack.peek(),new JsonHttpResponseHandler(){
			private ProgressDialog progressDialog;
			@Override
			public void onStart() {
				super.onStart();
				if (names.size() == 0) {
    				progressDialog = ProgressDialog.show(MyListActivity.this, "稍候", "加载中...", true, true);
    				progressDialog.setCanceledOnTouchOutside(false);
    			}
			}
			@Override
			public void onSuccess(int arg0, JSONArray arg1) {
				super.onSuccess(arg0, arg1);
				if (progressDialog != null) {
    				progressDialog.dismiss();
				}
				Log.i("weitu", "json:"+arg1.toString());
				new FileData(MyListActivity.this).delOldFile(pathStack.peek());
				new FileData(MyListActivity.this).insert(arg1);
    			listView.onRefreshComplete();
				refresh(false);
			}
			@Override
			public void onFailure(Throwable arg0, String arg1) {
				super.onFailure(arg0, arg1);
				if (progressDialog != null) {
    				progressDialog.dismiss();
				}
    			listView.onRefreshComplete();
			}
		});
	}

	
}
