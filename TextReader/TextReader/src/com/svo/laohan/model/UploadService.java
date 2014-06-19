package com.svo.laohan.model;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import yuku.filechooser.FileChooserActivity;
import yuku.filechooser.FileChooserConfig;
import yuku.filechooser.FileChooserConfig.Mode;
import yuku.filechooser.FolderChooserActivity;
import yuku.filechooser.FolderChooserConfig;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.svo.laohan.model.dao.FileData;
import com.svo.laohan.model.dao.entity.FileEntity;
import com.svo.laohan.util.Constant;
import com.svo.laohan.util.FileUtil;
import com.svo.laohan.util.TypeUtil;

public class UploadService {
	public static final int UPLOAD_FILE = 21;
	public static final int UPLOAD_FILES = 22;
	public static final int REFRESH = 0;
	public static final int DEL_SUCCESS = 31;
	public static final int RENAME = 32;
	public static final int MKDIR = 36;
	private Activity activity;
	private String sharePath; //我的共享路径 
	private Handler handler;
	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	public UploadService(Activity activity) {
		this.activity = activity;
		sharePath = Constant.reShare+getUserId();
	}
	
	 public void upload(final String filePath,final File file) {
		 Log.i("weitu", "filePath:"+filePath+";"+file.getAbsolutePath()+";"+file.exists()+";"+file.length());
		 new Thread(){
				public void run() {
					new BcsService().upload(filePath,file);
					Message msg = handler.obtainMessage();
					msg.what = UPLOAD_FILE;
					handler.sendMessage(msg);
					FileUtil.copyFile(file.getAbsolutePath(), Constant.Save_path+file.getName());
				}
			}.start();
	}
	public String getSharePath() {
		return sharePath;
	}
	/**
	 * 选择上传文件
	 * @param activity
	 * @param path
	 */
	public void selectFile(String path) {
		FileChooserConfig config = new FileChooserConfig();
		config.mode = Mode.Open;
		config.initialDir = path;
		config.title = "选择文件";
		config.subtitle = "";
		config.pattern = ".*\\.(?i:doc|txt|epub|fb2|mobi|jpg|jpeg|png|gif|mp3|wav|amr|wma|mp4|avi|3gp|rmvb|rm|wmv|mkv|mpg|mpeg|vob|flv|swf|mov|apk)";
		activity.startActivityForResult(FileChooserActivity.createIntent(activity.getApplicationContext(), config), 1);
	}
	private String getDevId(){
		TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}
	public String getUserId(){
		String devId = getDevId();
		if (TextUtils.isEmpty(devId)) {
			return "emulator";
		}
		String userId = devId.hashCode()+"";
		if (userId.startsWith("-")) {
			userId = userId.replace("-", "lr");
		}
		return userId;
	}
	/**
	 * 得到共享的文件
	 * @param curPath 绝对路径
	 * @return
	 */
	public List<FileEntity> getShareFiles(String curPath) {
		FileData fileData = new FileData(activity);
		return fileData.queryFiles("select * from file  where parDir in (?,?)  order by mtime desc", new String[]{curPath,curPath+"/"});
	}

	public void selectFolder(String folderPath) {
		List<String> roots = new ArrayList<String>();
		roots.add(folderPath);
		FolderChooserConfig config = new FolderChooserConfig();
		config.roots = roots;
		config.title = "选择目录";
		activity.startActivityForResult(FolderChooserActivity.createIntent(activity, config), 2);
	}
	/**
	 * 请求某个文件夹下的文件
	 * @param path 相对路径
	 */
	public void reqShare(final String path) {
		new Thread(){
			public void run() {
				boolean flag = new FileService(activity).reqFiles(path);
				Message msg = handler.obtainMessage();
				if (flag) {
					msg.what = REFRESH;
					msg.arg1 = 1;
				}else {
					msg.arg1 = 0;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	/**
	 * 处理上传目录
	 * @param selectedFolder
	 * @param curPath 
	 */
	public void uploadDir(final String selectedFolder, final String curPath) {
		File dir = new File(selectedFolder);
		if (dir.list().length < 1) {
			Toast.makeText(activity, "目录为空", Toast.LENGTH_LONG).show();
			return;
		}
		final File[] files = dir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String filename) {
				String tmp = filename.toLowerCase();
				if (TypeUtil.getTypeBasePath(tmp) > 0) {
					return true;
				}
				return false;
			}
		});
		if (files != null && files.length < 1) {
			Toast.makeText(activity, "目录内没有找到可上传文件", Toast.LENGTH_LONG).show();
			return;
		}
		new Thread() {
			public void run() {
				for (int i = 0; i < files.length; i++) {
					upload(curPath+"/"+files[i].getName(), files[i]);
					FileUtil.copyFile(files[i].getAbsolutePath(), Constant.Save_path+files[i].getName());
				}
			}
		}.start();
	}
	/**
	 * 从表中删除一条数据
	 * @param get_id
	 * @return 
	 */
	public int delItem(final FileEntity fileEntity) {
		FileData fileData = new FileData(activity);
		new Thread(){
			public void run() {
				boolean flag = new BcsService().deleteObject(fileEntity.getFileName());
				if (flag) {
					handler.sendEmptyMessage(DEL_SUCCESS);
				}
			}
		}.start();
		return fileData.delOneFile(fileEntity.get_id());
	}
	/**
	 * 修改文件名字
	 * @param _id
	 * @param newName
	 * @param oldName 
	 */
	public void reName(String _id, final String newName, final String oldName) {
		FileData fileData = new FileData(activity);
		fileData.updateName(_id,newName);
		handler.sendEmptyMessage(REFRESH);
		new Thread(){
			public void run() {
				String source = sharePath+"/"+oldName;
				String target = sharePath+"/"+newName;
				boolean flag = new BcsService().rename(source, target);
				Message msg = handler.obtainMessage();
				msg.what = RENAME;
				if (flag) {
					msg.obj = "命名成功";
				}else {
					msg.obj = "命名失败";
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	public void mkdir(final String dirName) {
		new Thread(){
			public void run() {
				boolean flag = new BcsService().mkDir(sharePath+"/"+dirName);
				Message msg = handler.obtainMessage();
				if (flag) {
					msg.what = MKDIR;
					msg.arg1 = 1;//创建成功
				} else {
					msg.arg1 = 0;//创建失败
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
}
