package com.svo.platform.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

public class FileUtil {
	
	private static final String TAG = "FileUtil";

	/**
	 * 复制文件
	 * @param sourceFile 原文件对象
	 * @param targetFile 目标文件对象
	 */
    public static boolean copyFile(File sourceFile, File targetFile){
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
            Log.i("FileUtil", "复制文件成功!"); 
            return true;
        }catch (Exception e) {  
        	Log.w("FileUtil", "复制单个文件操作出错!");  
            e.printStackTrace();  
        }   finally {
            try {
				// 关闭流
				if (inBuff != null)
				    inBuff.close();
				if (outBuff != null)
				    outBuff.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        return false;
    }
	
	/**
	 * 复制某个文件
	 * @param oldPath 原文件的绝对路径
	 * @param newPath 目标文件的绝对路径。
	 * 注意：请确保目标文件所在的目录已存在
	 */
	public static boolean copyFile(String oldPath, String newPath) {  
		File sourceFile = new File(oldPath); 
		File targetFile = new File(newPath);
		return copyFile(sourceFile, targetFile);
    }
	/**
     * 删除目录或文件
     * @param path 目录或文件的路径
     */
	public static void delFile(String path) {
		// 如果file不存在 什么都不干
		File file = new File(path);
		if (file == null || !file.exists()) {
			return;
		}
		// 如果file是文件直接删除
		if (file.isFile()) {
			file.delete();
			return;
		}
		// 如果是目录
		if (file.isDirectory()) {
			File[] subfiles = file.listFiles();
			if (subfiles == null || subfiles.length == 0) {
				// 若无东西直接删除
				file.delete();
				return;
			}
			// 如果目录下有东西，先删目录中的东西，最后删目录
			for (File subfile : subfiles) {
				delFile(subfile.getAbsolutePath());
			}
			file.delete();

		}
	}
	
	/**
	 * 得到图片的位图，可为网络图片
	 * @param filePath 待读取图片的路径可为图片的URL
	 * @return Bitmap 读取失败则返回null
	 */
	public static Bitmap getBitmap(String picUri) {
		if (picUri == null || picUri.length() < 1) {
			Log.e(TAG, "读取图片时路径不能为空");
			return null;
		}
		if (picUri.startsWith("http:")) {
			URL url;
			try {
				url = new URL(picUri);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(8*1000);
				return BitmapFactory.decodeStream(conn.getInputStream());
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			File file = new File(picUri);
			if (file == null || !file.exists() || file.isDirectory()) {
				Log.e(TAG, "读取的图片不存在");
				return null;
			}
		}
		return BitmapFactory.decodeFile(picUri);
	}
	
	/**
	 * 读取文本文件
	 * @param filePath 待读取的文件绝对路径 
	 * @return 读取的文本内容;读取失败则返回null
	 */
	public static String readTxtFile(String filePath){
		File file = new File(filePath);
		if (file == null || !file.exists()) {
			Log.e(TAG, "文件不存在:"+file.getAbsolutePath());
			return null;
		}
		try {
			Scanner scanner = new Scanner(file);
			StringBuilder sb = new StringBuilder();
			while (scanner.hasNextLine()) {
				sb.append(scanner.nextLine());
			}
			scanner.close();
			return sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 保存图片
	 * @param imageView
	 * @param path 保存目录
	 * @param picName 图片名字
	 * @return 保存的文件对象;保存失败返回null
	 */
	public static File savePic(ImageView imageView,String path,String picName){
		BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
		if (bitmapDrawable == null) {
			Log.e(TAG, "没有给imageView设置图片,保存失败");
			return null;
		}
		return saveBitmap(bitmapDrawable.getBitmap(), path, picName);
	}
	public static File saveBitmap(Bitmap bitmap,String path,String picName){
		File dir = new File(path);
		if (dir != null && !dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(dir, picName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bitmap.compress(CompressFormat.PNG, 100, fos);
			fos.close();
		}catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return file;
	}
	/**
	 * 保存二进制文件
	 * @param url
	 * @param path
	 * @param fileName
	 * @return
	 */
	public static File saveFile(String urlStr,String path,String fileName){
		try {
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			//连接超时时间
			conn.setConnectTimeout(Constants.CONNECT_OUT_TIME);
			//读取超时时间
			conn.setReadTimeout(Constants.CONNECT_OUT_TIME);
			InputStream inputStream=conn.getInputStream();
			if (inputStream == null) {
				return null;
			}
			File dir = new File(path);
			if (dir != null && !dir.exists()) {
				dir.mkdirs();
			}
			File file = new File(dir, fileName);
			FileOutputStream outputStream = null;
			try {
				outputStream = new FileOutputStream(file);
				byte[] bs = new byte[1024 * 8];
				int len = -1;
				while ((len = inputStream.read(bs)) != -1) {
					outputStream.write(bs, 0, len);
				}
				outputStream.flush();
				outputStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
					try {
						inputStream.close();
						inputStream = null;
						if (outputStream != null) {
							outputStream.close();
							outputStream = null;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
			return file;
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}
	/**
	 * 保存图片
	 * @param inputStream 输入流
	 * @param path 保存目录
	 * @param picName 图片名字
	 * @return 保存的文件对象;保存失败返回null
	 */
	public static File savePic(InputStream inputStream,String path,String picName){
		if (inputStream == null) {
			return null;
		}
		File dir = new File(path);
		if (dir != null && !dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(dir, picName);
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(file);
			byte[] bs = new byte[1024 * 4];
			int len = -1;
			while ((len = inputStream.read(bs)) != -1) {
				outputStream.write(bs, 0, len);
			}
			outputStream.flush();
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
				try {
					inputStream.close();
					inputStream = null;
					if (outputStream != null) {
						outputStream.close();
						outputStream = null;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return file;
	}
	
	/**
	 * 保存图片
	 * @param urlStr 图片的网络地址
	 * @param path 保存目录
	 * @param picName 图片名字
	 * @return 保存的文件对象;保存失败返回null
	 */
	public static File savePic(String picUrl,String path,String picName){
		try {
			URL url = new URL(picUrl);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			//连接超时时间
			conn.setConnectTimeout(Constants.CONNECT_OUT_TIME);
			//读取超时时间
			conn.setReadTimeout(Constants.CONNECT_OUT_TIME);
			InputStream inputStream=conn.getInputStream();
			return savePic(inputStream, path, picName);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}
    
    /**
	 * 将字符串保存到文件中
	 * @param content 待保存的内容.内容为空则不保存
	 * @param filePath 待保存文件的全路径
	 * @return 保存成功则返回保存的文件对象。保存失败则返回null
	 */
	public static File saveTxtFile(String content,String filePath){
		String[] arr = StringUtil.sepPath(filePath);
		return saveTxtFile(content, arr[0], arr[1]);
	}
	
	/**
	 * 将字符串保存到文件中
	 * @param content 待保存的内容.内容为空则不保存
	 * @param path 文件所在的目录
	 * @param fileName 文件名
	 * @return 保存成功则返回保存的文件对象。保存失败则返回null
	 */
	public static File saveTxtFile(String content,String path,String fileName){
		//内容为空则不保存
		if (TextUtils.isEmpty(content)) {
			Log.e(TAG, "保存内容不能为空");
			return null;
		}
		//判断目录是否存在，不存在则创建
		File dir = new File(path);
		if (dir != null && !dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(dir, fileName);
		try {
			PrintStream ps = new PrintStream(file);
			ps.println(content);
			ps.flush();
			ps.close();
			return file;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}  
	
	/**
	 * 解压缩ZIP文件，将ZIP文件里的内容解压到targetDIR目录下.解压完之后会把压缩文件删除
	 * @param zipName	待解压缩的ZIP文件名
	 * @param targetBaseDirName	目标目录
	 */
    public static void upzipFile(String zipFileName, String targetBaseDirName){
		if (!targetBaseDirName.endsWith(File.separator)){
			targetBaseDirName += File.separator;
		}
        try {
        	//根据ZIP文件创建ZipFile对象
        	ZipFile zipFile = new ZipFile(zipFileName);
            ZipEntry entry = null;
            String entryName = null;
            String targetFileName = null;
            byte[] buffer = new byte[4096];
            int bytes_read; 
            //获取ZIP文件里所有的entry
            Enumeration<? extends ZipEntry> entrys = zipFile.entries();
            //遍历所有entry
            while (entrys.hasMoreElements()) {
            	entry = (ZipEntry)entrys.nextElement();
            	//获得entry的名字
            	entryName =  entry.getName();
            	targetFileName = targetBaseDirName + entryName;
            	if (entry.isDirectory()){
            		//  如果entry是一个目录，则创建目录
            		new File(targetFileName).mkdirs();
            		continue;
            	} else {
            		//	如果entry是一个文件，则创建父目录
            		new File(targetFileName).getParentFile().mkdirs();
            	}

            	//否则创建文件
            	File targetFile = new File(targetFileName);
            	//打开文件输出流
            	FileOutputStream os = new FileOutputStream(targetFile);
            	//从ZipFile对象中打开entry的输入流
            	InputStream  is = zipFile.getInputStream(entry);
            	while ((bytes_read = is.read(buffer)) != -1){
            		os.write(buffer, 0, bytes_read);
            	}
            	os.close( );
            	is.close( );
            }
        } catch (IOException err) {
        	err.printStackTrace();
        	File file = new File(zipFileName);
        	if(file.exists()){
        		file.delete();
        	}
        }finally{
        	File file = new File(zipFileName);
        	if(file.exists()){
        		file.delete();
        	}
        }
	}
}
