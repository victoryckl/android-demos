package com.svo.laohan;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import yuku.filechooser.FileChooserActivity;
import yuku.filechooser.FileChooserResult;
import yuku.filechooser.FolderChooserActivity;
import yuku.filechooser.FolderChooserResult;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.actionbarsherlock.view.Window;
import com.svo.laohan.adapter.ShareListAdapter;
import com.svo.laohan.model.FileService;
import com.svo.laohan.model.UploadService;
import com.svo.laohan.model.dao.entity.FileEntity;
import com.svo.laohan.util.Constant;
import com.svo.laohan.util.FileUtil;
import com.svo.laohan.util.StringUtil;
import com.svo.laohan.util.TypeUtil;
import com.svo.platform.share.Share;
import com.svo.platform.share.model.Qzone;
import com.svo.platform.share.model.Tencent;
import com.svo.platform.utils.Constants;
import com.svo.platform.widget.PushRefreshListView;

public class ShareList extends SherlockActivity implements OnItemClickListener{
	public String fileChoosePath = Environment.getExternalStorageDirectory().getAbsolutePath();
	private UploadService uploadService;
	private PushRefreshListView listView;
	private List<FileEntity> entities;
	private ProgressBar progressBar;
	private String curPath;
	private LinearLayout upload_par;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.sharelist);
		setSupportProgressBarVisibility(false);
		int index = getIntent().getIntExtra("index", 1);
		curPath = "/网友共享/"+new UploadService(this).getUserId()+"/";
		if (index == 1) {
			curPath = curPath+"我的书籍/";
		} else if (index == 2) {
			curPath = curPath+"我的图片/";
		}else if (index == 3) {
			curPath = curPath+"我的音乐/";
		}else if (index == 4) {
			curPath = curPath+"我的电影/";
		}
		setTitle(curPath);
		listView = (PushRefreshListView) findViewById(R.id.list);
		/** 下拉监听 */
		listView.setonRefreshListener(new PushRefreshListView.OnRefreshListener() {
			public void onRefresh() {
				netRefresh();
			}
		});
		upload_par = (LinearLayout)findViewById(R.id.upload);
		listView.setOnItemClickListener(this);
		progressBar = (ProgressBar) findViewById(R.id.progress);
		uploadService = new UploadService(this);
		uploadService.setHandler(handler);
		refresh();
		if (entities.size() == 0) {
			progressBar.setVisibility(View.VISIBLE);
			netRefresh();
		}
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			progressBar.setVisibility(View.GONE);
			if (msg.what == UploadService.REFRESH) {
				listView.onRefreshComplete();
				if (msg.arg1 == 1) {
					refresh();
				}
				if (entities.size() == 0) {
					upload_par.setVisibility(View.VISIBLE);
				}else {
					upload_par.setVisibility(View.GONE);
				}
			}else if (msg.what == UploadService.DEL_SUCCESS) {
				Toast.makeText(ShareList.this, "文件删除成功", Toast.LENGTH_LONG).show();
			}else if (msg.what == UploadService.RENAME) {
				Toast.makeText(ShareList.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
			}else if (msg.what == UploadService.MKDIR) {
				if (msg.arg1 == 0) {
					Toast.makeText(ShareList.this, "创建目录失败", Toast.LENGTH_LONG).show();
				} else {
					netRefresh();
				}
			}
		}
	};
	private void refresh() {
		entities = uploadService.getShareFiles(curPath);
		ShareListAdapter adapter = new ShareListAdapter(this, entities,this);
		listView.setAdapter(adapter);
		if (entities.size() > 0) {
			upload_par.setVisibility(View.GONE);
		}
	}
	private void refresh(boolean isNet) {
		refresh();
		if (isNet && entities.size() == 0) {
			netRefresh();
		} 
	}
	/**
	 * 网络请求刷新
	 */
	public void netRefresh() {
		uploadService.reqShare(curPath);
	}
	private Stack<String> pathStack = new Stack<String>();
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//点击位置要减1
		FileEntity fileEntity = entities.get(position-1);
		if (fileEntity.isDir()) {
			pathStack.push(curPath);
			curPath = fileEntity.getFileName();
			refresh(true);
			setTitle(curPath);
		}else {
			String fileName = fileEntity.getFileName();
			FileService fileService = new FileService(this);
			boolean isExist = FileUtil.isFileExist(Constant.Save_path+fileName);
			if (isExist) {
				if (TypeUtil.getTypeBasePath(fileName) == TypeUtil.IMAGE || TypeUtil.getTypeBasePath(fileName) == TypeUtil.GIF) {
					UploadService uploadService = new UploadService(this);
					fileService.browsePic(entities,uploadService.getSharePath()+"/"+curPath,uploadService.getSharePath()+"/"+curPath+"/"+fileEntity.getFileName());
					return;
				}
				fileService.openFile(Constant.Save_path+fileName);
			} else {
				Toast.makeText(this, "本地文件已被删除，请从云端下载", Toast.LENGTH_LONG).show();
			}
		}
	}
	public void onBackPressed() {
		if (pathStack.size() > 0) {
			curPath = pathStack.pop();
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
	public void mkdir() {
		final EditText editText = new EditText(this);
		new AlertDialog.Builder(this).setTitle("请输入文件夹名称").setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String dirName = editText.getText().toString().trim();
				if (TextUtils.isEmpty(dirName)) {
					Toast.makeText(ShareList.this, "文件夹名称不可为空", Toast.LENGTH_LONG).show();
					return;
				}
				uploadService.mkdir(curPath+"/"+dirName);
			}
		}).setNegativeButton("取消", null).show();
	}
	//响应隐藏操作,下拉三角
	public void operate(int viewId, final FileEntity fileEntity) {
		if (viewId == R.id.del) {
			uploadService.delItem(fileEntity);
			refresh();
		}else if (viewId == R.id.rename) {
			final EditText editText = new EditText(this);
			final String fileName = StringUtil.sepPath(fileEntity.getFileName())[1];//带后缀名
			editText.setText(fileName);
			new AlertDialog.Builder(this).setTitle("重命名").setView(editText).setPositiveButton("确定", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String name = editText.getText().toString().trim();
					if (TextUtils.isEmpty(name)) {
						Toast.makeText(ShareList.this, "文件名不可为空", Toast.LENGTH_LONG).show();
						return;
					}
					uploadService.reName(fileEntity.get_id(),name,fileName);
				}
			}).setNegativeButton("取消", null).show();
		}else if (viewId == R.id.share) {
			Share share = new Share(this);
			Bundle bundle = new Bundle();
			bundle.putString("content", "我正在用老汉阅读器阅读《"+fileEntity.getFileName()+"》,值得一读,老汉阅读器阅读方便,可以试用一下:"+Constants.REN_FROM_LINK);
			bundle.putString("title", "老汉阅读器,不错,可以体验一下"); 
			bundle.putString("url", Constants.REN_FROM_LINK); 
			bundle.putString("imageUrl", "http://1.dubinwei.duapp.com/static/share.png");
			share.share(bundle);
		}else if (viewId == R.id.down) {
			String filePath = com.svo.laohan.util.Constant.Save_path+fileEntity.getFileName();
			boolean isExist = FileUtil.isFileExist(filePath);
			if (isExist) {
				Toast.makeText(this, "文件已存在", Toast.LENGTH_LONG).show();
				return;
			} else {
				String fileName = fileEntity.getFileName();
				new FileService(this).downFile(fileName ,StringUtil.delSuffix(StringUtil.sepPath(fileName)[1]));
			}
		}
	}
	private Handler uploadHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if (msg.what == UploadService.UPLOAD_FILE) {
				int value = (int) (msg.arg1*1.0/msg.arg2*10000);
				setSupportProgress(value);
				if (msg.arg1 == msg.arg2) {
					uploadOver();
				}
			}else {
				setSupportProgress(msg.arg1);
				if (msg.arg1 == 10000) {
					uploadOver();
				}
			}
		}

		/**
		 * 
		 */
		private void uploadOver() {
			Toast.makeText(ShareList.this, "上传完成", Toast.LENGTH_LONG).show();
			netRefresh();
		}
	};
	
	public void upClick(View view) {
		if (view.getId() == R.id.up_btn) {
			new UploadService(this).selectFile(fileChoosePath);
		}else {
			Intent intent = new Intent(this, SearchActivity.class);
			Bundle bundle = new Bundle();
			int random = new Random().nextInt();
			if (curPath.equals("我的书籍")) {
				bundle.putString("key", "穷爸爸");
				if (random % 2 == 1) {
					bundle.putString("key", "恋爱");
				}
			} else if (curPath.equals("我的图片")) {
				bundle.putString("key", "可爱美女");
				if (random % 2 == 1) {
					bundle.putString("key", "色小孩");
				}
			}else if (curPath.equals("我的音乐")) {
				bundle.putString("key", "恋曲");
				if (random % 2 == 1) {
					bundle.putString("key", "猪八戒");
				}
			}else if (curPath.equals("我的电影")) {
				bundle.putString("key", "搞笑短篇");
				if (random % 2 == 1) {
					bundle.putString("key", "美女相册");
				}
			}
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			Tencent tencent = new Tencent(this);
			tencent.saveInfo(resultCode, data);
		}
		if (Qzone.mTencent != null) {
			//QQ互联如果要成功接收到回调，需要在调用接口的Activity的onActivityResult方法中增加如下代码
			Qzone.mTencent.onActivityResult(requestCode, resultCode, data) ;
		}
		if (requestCode == 1) {
			if (resultCode == Activity.RESULT_OK) {
				FileChooserResult result = FileChooserActivity.obtainResult(data);
				fileChoosePath = result.currentDir;
				File file = new File(result.firstFilename);
				if (file != null && file.length() > 250*1024*1024) {
					Toast.makeText(this, "上传的文件不能大于250M,请重新选择", Toast.LENGTH_LONG).show();
					return;
				}
				String fileName = StringUtil.sepPath(result.firstFilename)[1];
				UploadService uploadService = new UploadService(this);
				uploadService.setHandler(uploadHandler);
				uploadService .upload(curPath+"/"+fileName, new File(result.firstFilename));
				Toast.makeText(this, "正在上传文件："+fileName, Toast.LENGTH_LONG).show();
				setSupportProgressBarVisibility(true);
			}
		}else if (requestCode == 2) {
			if (resultCode == Activity.RESULT_OK) {
				FolderChooserResult result = FolderChooserActivity.obtainResult(data);
				UploadService uploadService = new UploadService(this);
				uploadService.setHandler(uploadHandler);
				uploadService.uploadDir(result.selectedFolder,curPath);
				invalidateOptionsMenu();
			}
		}
	}
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		SubMenu subMenu1 = menu.addSubMenu("上传书籍");
		subMenu1.add("上传文件");
		subMenu1.add("上传目录");
		subMenu1.add("建立文件夹");
		MenuItem subMenu1Item = subMenu1.getItem();
		subMenu1Item.setIcon(R.drawable.ic_title_share_default);
		subMenu1Item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return super.onCreateOptionsMenu(menu);
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String title = item.getTitle().toString();
		if ("上传文件".equals(title)) {
			new UploadService(this).selectFile(fileChoosePath);
		}else if ("上传目录".equals(title)) {
			new UploadService(this).selectFolder(fileChoosePath);
		}else if ("建立文件夹".equals(title)) {
			mkdir();
		}
		return super.onOptionsItemSelected(item);
	}
}
