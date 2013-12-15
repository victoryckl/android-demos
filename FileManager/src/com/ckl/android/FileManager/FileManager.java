package com.ckl.android.FileManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class FileManager extends ListActivity {
	private List<IconifiedText>	directoryEntries = new ArrayList<IconifiedText>();
	private List<IconifiedText>	fileEntries 	 = new ArrayList<IconifiedText>();
	private Comparator<IconifiedText> comparator = null;
	private File				currentDirectory = new File("/");
	private File 				myTmpFile 		 = null;
	private int 				myTmpOpt		 = -1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		browseToRoot();
		this.setSelection(0);

		//不区分大小写比较，用于文件列表排序
		comparator = new Comparator<IconifiedText>() {
			public int compare(IconifiedText a, IconifiedText b) {
				return a.getText().compareToIgnoreCase(b.getText()); 
			}
		};

		//长按弹出菜单
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i(CONST.TAG, "long click");
				// 取得选中的一项的文件名
				String selectedFileString = directoryEntries.get(position).getText();
				
				if (!dealRefreshAndUpLevel(selectedFileString)) {
					File file = new File(GetCurDirectory() + File.separator + selectedFileString);
					if (file != null) {
						fileOptMenu(file);
					}
				}
				
				return true;
			}
		});
	}
	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			if (this.currentDirectory.getAbsolutePath().compareTo("/") != 0) {
				this.upOneLevel();
				return false;
			}
		}
		return super.onKeyUp(keyCode, event);
	}
	
	//浏览文件系统的根目录
	private void browseToRoot() {
		browseTo(new File(File.separator));
    }
	//返回上一级目录
	private void upOneLevel() {
		if(this.currentDirectory.getParent() != null)
			this.browseTo(this.currentDirectory.getParentFile());
	}
	//浏览指定的目录
	private void browseTo(final File file) {
		if (file.isDirectory()) {
			File[] list = file.listFiles();
			if (null != list) {
				this.setTitle(file.getAbsolutePath());
				this.currentDirectory = file;
				fill(list);
			} else {
				Log.i(CONST.TAG, "no rights");
				CONST.DisplayToast(this, R.string.no_rights);
			}
		}
	}
	//打开指定文件
	protected void openFile(File aFile) {
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		File file = new File(aFile.getAbsolutePath());
		// 取得文件名
		String fileName = file.getName();
		String type = null;
		
		// 根据不同的文件类型来打开文件
		if (checkEnds(fileName, R.array.fileEndingFlash)) {
			//todo:直接打开flash
			return ;
		} else if (checkEnds(fileName, R.array.fileEndingImage)) {
			type = "image/*";
		} else if (checkEnds(fileName, R.array.fileEndingAudio)) {
			type = "audio/*";
		} else if (checkEnds(fileName, R.array.fileEndingVideo)) {
			type = "video/*";
		} else if (checkEnds(fileName, R.array.fileEndingText)) {
			type = "text/*";
		}
		
		if(type != null) {
			intent.setDataAndType(Uri.fromFile(file), type);
		}
		startActivity(intent);
	}
	//这里可以理解为设置ListActivity的源
	private void fill(File[] files) {		
		if (null == files)
		{
			return ;
		}
		
		//清空列表
		this.directoryEntries.clear();
		fileEntries.clear();

		//添加一个当前目录的选项
		this.directoryEntries.add(new IconifiedText(getString(R.string.current_dir), getResources().getDrawable(R.drawable.reload)));
		//如果不是根目录则添加上一级目录项
		if (this.currentDirectory.getParent() != null)
			this.directoryEntries.add(new IconifiedText(getString(R.string.up_one_level), getResources().getDrawable(R.drawable.back)));

		Drawable currentIcon = null;
		for (File currentFile : files) {
			//判断是一个文件夹还是一个文件
			if (currentFile.isDirectory()) {
				currentIcon = getResources().getDrawable(R.drawable.folder);
			} else {
				//取得文件名
				String fileName = currentFile.getName();
				//根据文件名来判断文件类型，设置不同的图标
				currentIcon = getIcon(fileName);
			}
			//确保只显示文件名、不显示路径如：/sdcard/111.txt就只是显示111.txt
			String path = this.currentDirectory.getAbsolutePath();
			int lenght = path.length();
			if (!path.endsWith(File.separator)) {
				lenght++;
			}
			if (currentFile.isDirectory()) {
				this.directoryEntries.add(new IconifiedText(currentFile.getAbsolutePath().substring(lenght), currentIcon));
			} else {
				fileEntries.add(new IconifiedText(currentFile.getAbsolutePath().substring(lenght), currentIcon));
			}
		}
		Collections.sort(fileEntries, comparator);
		Collections.sort(this.directoryEntries, comparator);
		for (int i = 0; i < fileEntries.size(); i++) {
			this.directoryEntries.add(fileEntries.get(i));
		}
		IconifiedTextListAdapter itla = new IconifiedTextListAdapter(this);
		//将表设置到ListAdapter中
		itla.setListItems(this.directoryEntries);
		//为ListActivity添加一个ListAdapter
		this.setListAdapter(itla);
	}
	
	//刷新和上一级目录处理
	private boolean dealRefreshAndUpLevel(String filename) {
		boolean dealed = false;
		
		if (filename.equals(getString(R.string.current_dir))) {
			dealed = true;
			//如果选中的是刷新
			this.browseTo(this.currentDirectory);
		} else if (filename.equals(getString(R.string.up_one_level))) {
			dealed = true;
			//返回上一级目录
			this.upOneLevel();
		}
		
		return dealed;
	}
	
	//单击直接打开文件或目录
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.i(CONST.TAG, "click");
		super.onListItemClick(l, v, position, id);
		// 取得选中的一项的文件名
		String selectedFileString = this.directoryEntries.get(position).getText();
		
		if (!dealRefreshAndUpLevel(selectedFileString)) {
			File clickedFile = new File(this.currentDirectory.getAbsolutePath()+ File.separator + selectedFileString);
			
			if(clickedFile != null){
				if(clickedFile.isDirectory()){
					this.browseTo(clickedFile);
				} else {
					openFile(clickedFile);
				}
			}
		}
	}

	//通过文件名判断是什么类型的文件
	private boolean checkEndsWithInStringArray(String checkItsEnd, String[] fileEndings) {
		for(String aEnd : fileEndings) {
			if(checkItsEnd.endsWith(aEnd))
				return true;
		}
		return false;
	}
	
	private boolean checkEnds(String fileName, int id) {
		return checkEndsWithInStringArray(fileName, getResources().getStringArray(id));
	}
	
	//根据文件名来判断文件类型，设置不同的图标
	private Drawable getIcon(String fileName) {
		int id = R.drawable.unkown;
		
		if (checkEnds(fileName, R.array.fileEndingImage)) {
			id = R.drawable.image;
		} else if (checkEnds(fileName, R.array.fileEndingWebText)) {
			id = R.drawable.webtext;
		} else if (checkEnds(fileName, R.array.fileEndingPackage)) {
			id = R.drawable.packed;
		} else if (checkEnds(fileName, R.array.fileEndingAudio)) {
			id = R.drawable.audio;
		} else if (checkEnds(fileName, R.array.fileEndingVideo)) {
			id = R.drawable.video;
		} else if (checkEnds(fileName, R.array.fileEndingText)) {
			id = R.drawable.text;
		} else if (checkEnds(fileName, R.array.fileEndingFlash)) {
			id = R.drawable.flash;
		}
		
		return getResources().getDrawable(id);
	}
	
	//按目录键后的弹出菜单
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, R.string.new_folder).setIcon(R.drawable.addfolderr);
		//menu.add(0, 1, 0, "删除目录").setIcon(R.drawable.delete);//这个是删除当前目录，不好，这里屏蔽掉
		menu.add(0, 2, 0, R.string.paste).setIcon(R.drawable.paste);
		menu.add(0, 3, 0, R.string.root).setIcon(R.drawable.goroot);
		menu.add(0, 4, 0, R.string.up).setIcon(R.drawable.uponelevel);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
			case 0:
				Mynew();
				break;
//			case 1:
//				//注意：删除目录，谨慎操作，该例子提供了
//				//deleteFile（删除文件）和deleteFolder（删除整个目录）
//				MyDelete();
//				break;
			case 2:
				MyPaste();
				break;
			case 3:
				this.browseToRoot();
				break;
			case 4:
				this.upOneLevel();
				break;
		}
		return false;
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}
	
	void CopyOrCut(File src, File dst, int option) {
		if (option == 0) {//复制粘贴
			copyFile(src, dst);
		} else if (option == 1){//剪切粘贴
			src.renameTo(dst);
			myTmpFile = null;//清空源路径
		}
		browseTo(new File(GetCurDirectory()));
	}
	
	//粘贴操作
	public void MyPaste() {
		if ( myTmpFile == null ) {
			CONST.DisplayToast(FileManager.this, R.string.please_copy);
		} else {
			final File destFile = new File(GetCurDirectory()+File.separator+myTmpFile.getName());
			if (myTmpFile.isDirectory()) {
				if (destFile.exists()) {
					CONST.DisplayToast(FileManager.this, R.string.folder_exist);
				} else {
					copyDirectiory(myTmpFile, destFile);
					browseTo(new File(GetCurDirectory()));
				}
			} else {
				if(destFile.exists()) {
					Builder builder = new Builder(FileManager.this);
					builder.setTitle(R.string.paste_hint);
					builder.setMessage(R.string.cover_it);
					builder.setPositiveButton(android.R.string.ok,
							new AlertDialog.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									CopyOrCut(myTmpFile, destFile, myTmpOpt);
								}
							});
					builder.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
								}
							});
					builder.setCancelable(false);
					builder.create();
					builder.show();
				} else {
					CopyOrCut(myTmpFile, destFile, myTmpOpt);
				}
			}
		}
	}
	
	//删除整个文件夹[这个是删除当前目录，不好]
	public void MyDelete() {
		//取得当前目录
		File tmp=new File(this.currentDirectory.getAbsolutePath());
		//跳到上一级目录
		this.upOneLevel();
		//删除取得的目录
		if ( deleteFolder(tmp) ) {
			//CONST.DisplayToast(FileManager.this, R.string.delete_ok);
		} else {
			CONST.DisplayToast(FileManager.this, R.string.delete_error);
		}
		this.browseTo(this.currentDirectory);	
	}
	
	//新建文件夹
	public void Mynew() {
		final LayoutInflater factory = LayoutInflater.from(FileManager.this);
		final View dialogview = factory.inflate(R.layout.dialog, null);
		//设置TextView
		((TextView) dialogview.findViewById(R.id.TextView_PROM)).setText(R.string.input_folder_name);
		//设置EditText
		((EditText) dialogview.findViewById(R.id.EditText_PROM)).setText(R.string.folder_name);
		
		Builder builder = new Builder(FileManager.this);
		builder.setTitle(R.string.new_folder);
		builder.setView(dialogview);
		builder.setPositiveButton(android.R.string.ok,
				new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String value = ((EditText) dialogview.findViewById(R.id.EditText_PROM)).getText().toString();
						if ( newFolder(value) ) {
							//CONST.DisplayToast(FileManager.this, R.string.new_folder_ok);
						}else{
							CONST.DisplayToast(FileManager.this, R.string.new_folder_error);	
						}
					}
				});
		builder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						dialog.cancel();
					}
				});
		builder.show();
	}
	
	boolean mkDirs(File dir) {
		boolean creadok = dir.mkdirs();
		if (creadok){
			this.browseTo(this.currentDirectory);
			return true;
		}else{
			return false;
		}
	}
	
	//新建文件夹
	public boolean newFolder(String file) {
		boolean success = true;
		File dirFile = new File(this.currentDirectory.getAbsolutePath()+File.separator+file);
		try {
			if (dirFile.exists()) {
				if (dirFile.isDirectory()) {
					CONST.DisplayToast(FileManager.this, R.string.folder_exist);
					success = false;
				} else {
					success = mkDirs(dirFile);
				}	
			} else {
				success = mkDirs(dirFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
			return false;
		}
		return success;
	}
	//删除文件
    public boolean deleteFile(File file) {
		boolean result = false;
		if (file != null) {
			try {
				File file2 = file;
				file2.delete();
				result = true;
			} catch (Exception e) {
				e.printStackTrace();
				result = false;
			}
		}
		return result;
	} 
    //删除文件夹
	public boolean deleteFolder(File folder) {
		boolean result = false;
		try {
			String childs[] = folder.list();
			if (childs == null || childs.length <= 0) {
				if (folder.delete()) {
					result = true;
				}
			} else {
				for (int i = 0; i < childs.length; i++) {
					String childName = childs[i];
					String childPath = folder.getPath() + File.separator + childName;
					File filePath = new File(childPath);
					if (filePath.exists() && filePath.isFile()) {
						if (filePath.delete()) {
							result = true;
						} else {
							result = false;
							break;
						}
					} else if (filePath.exists() && filePath.isDirectory()) {
						if (deleteFolder(filePath)) {
							result = true;
						} else {
							result = false;
							break;
						}
					}
				}
				folder.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	} 
	
	//处理文件，包括打开，重命名等操作
	public void fileOptMenu(final File file) {
		OnClickListener listener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {//打开
					if(file.isDirectory()){
						browseTo(file);
					} else {
						openFile(file);
					}
				} else if (which == 1) {//重命名
					//自定义一个带输入的对话框由TextView和EditText构成
					final LayoutInflater factory = LayoutInflater.from(FileManager.this);
					final View dialogview = factory.inflate(R.layout.rename, null);
					//设置TextView的提示信息
					((TextView) dialogview.findViewById(R.id.TextView01)).setText(R.string.rename);
					//设置EditText输入框初始值
					((EditText) dialogview.findViewById(R.id.EditText01)).setText(file.getName());
					
					Builder builder = new Builder(FileManager.this);
					builder.setTitle(R.string.rename);
					builder.setView(dialogview);
					builder.setPositiveButton(android.R.string.ok,
							new AlertDialog.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									//点击确定之后
									final String value = GetCurDirectory()+File.separatorChar
										+((EditText) dialogview.findViewById(R.id.EditText01)).getText().toString();
									File file1 = new File(value);
									if(file1.exists()){
										if (file1.isDirectory()){
											CONST.DisplayToast(FileManager.this, R.string.folder_exist);
										} else {
											Builder builder = new Builder(FileManager.this);
											builder.setTitle(R.string.rename);
											builder.setMessage(R.string.cover_it);
											builder.setPositiveButton(android.R.string.ok,
													new AlertDialog.OnClickListener() {
														public void onClick(DialogInterface dialog, int which) {
															file.renameTo(new File(value));
															browseTo(new File(GetCurDirectory()));
														}
													});
											builder.setNegativeButton(android.R.string.cancel,
													new DialogInterface.OnClickListener() {
														public void onClick(DialogInterface dialog, int which) {
															dialog.cancel();
														}
													});
											builder.setCancelable(false);
											builder.create();
											builder.show();
										}
									}else{
										file.renameTo(file1);
										browseTo(new File(GetCurDirectory()));
									}
								}
							});
					builder.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
								}
							});
					builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
								public void onCancel(DialogInterface dialog) {
									dialog.cancel();
								}
							});
					builder.show();
				} else if ( which == 2 ) {//删除
					Builder builder = new Builder(FileManager.this);
					builder.setTitle(R.string.delete);
					builder.setMessage(getResources().getString(R.string.sure_delete) + file.getName() + "？");
					builder.setPositiveButton(android.R.string.ok,
							new AlertDialog.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									boolean ret;
									if (file.isDirectory()) {
										ret = deleteFolder(file);
									} else {
										ret = deleteFile(file);
									}
									if (ret) {
										browseTo(new File(GetCurDirectory()));
										//CONST.DisplayToast(FileManager.this, R.string.delete_ok);
									} else {
										CONST.DisplayToast(FileManager.this, R.string.delete_error);
									}
								}
							});
					builder.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
								}
							});
					builder.setCancelable(false);
					builder.create();
					builder.show();
				} else if ( which == 3 ) {//复制
					//保存我们复制的文件目录
					myTmpFile = file;
					//这里我们用0表示复制操作
					myTmpOpt = 0;
				} else if ( which == 4 ) {//剪切
					//保存我们复制的文件目录
					myTmpFile = file;
					//这里我们用0表示剪切操作
					myTmpOpt = 1;	 
				}
			}
		};
		//显示操作菜单
		Builder option = new AlertDialog.Builder(FileManager.this);
		option.setTitle(R.string.select_option);
		if (file.isDirectory()){
			option.setItems(R.array.dirmenu,listener);
		} else {
			option.setItems(R.array.filemenu,listener);
		}
        option.show();
	}
	//得到当前目录的绝对路劲
	public String GetCurDirectory() {
		return this.currentDirectory.getAbsolutePath();
	}
	//移动文件
	public void moveFile(String source, String destination) {
		new File(source).renameTo(new File(destination));   
	}
	//复制文件
	public void copyFile(File src, File target) {
		InputStream input = null;
		BufferedInputStream inBuff = null;
		
		OutputStream output = null;
		BufferedOutputStream outBuff = null;
		
		try {
			// 新建文件输入流并对它进行缓冲   
			input = new FileInputStream(src);
			inBuff = new BufferedInputStream(input);

	        // 新建文件输出流并对它进行缓冲   
	        output = new FileOutputStream(target);
	        outBuff = new BufferedOutputStream(output);
	        
	        // 缓冲数组
	        byte[] b = new byte[1024 * 50];
	        int len;
	        while ((len = inBuff.read(b)) != -1) {
	            outBuff.write(b, 0, len);
	        }
	        // 刷新此缓冲的输出流   
	        outBuff.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inBuff != null){
					inBuff.close();
				}
				if (outBuff != null){
					outBuff.close();
				}
				if (input != null){
					input.close();
				}
				if (output != null){
					output.close();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
    // 复制文件夹   
    public void copyDirectiory(File sourceDir, File targetDir) {
        // 新建目标目录
    	targetDir.mkdirs();
        // 获取源文件夹当前下的文件或目录
        File[] file = sourceDir.listFiles();
	    if (file != null) {
	        for (int i = 0; i < file.length; i++) {
	            if (file[i].isDirectory()) {
	                // 准备复制的源文件夹
	            	File dir1 = new File(sourceDir.getAbsolutePath() + File.separator + file[i].getName());
	                // 准备复制的目标文件夹
	            	File dir2 = new File(targetDir.getAbsolutePath() + File.separator + file[i].getName());
	                copyDirectiory(dir1, dir2);  
	            } else if (file[i].isFile()) {
	                // 源文件
	                File sourceFile = file[i];
	                // 目标文件
	                File targetFile = new File(targetDir.getAbsolutePath() + File.separator + file[i].getName());
	                copyFile(sourceFile, targetFile);
	            }
	        }
        } else {
        	CONST.DisplayToast(FileManager.this, R.string.no_rights);
        }
    }
}
