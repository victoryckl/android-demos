package com.example.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;
import android.util.Log;

import com.example.okhttp.demo.BuildConfig;

public class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();

	/**
     * @brief 创建目录
     * @param dirPath 目录路径
     * @return true: success; false: failure or already existed and not a
     *         directory.
     */
    public static boolean makeDirs(String dirPath) {
        File file = new File(dirPath);
        return makeDirs(file);
    }
    
    public static boolean makeDirs(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                return true;
            }
            return false;
        }
        return file.mkdirs();
    }
    
    public static boolean makeParentDirs(String filePath) {
        File file = new File(filePath);
        return makeParentDirs(file);
    }
    
    public static boolean makeParentDirs(File file) {
        file = file.getParentFile();
        return makeDirs(file);
    }

    public static boolean exists(String filepath) {
    	File file = new File(filepath);
    	return file.exists();
    }
    
    public static boolean writeFile(String path, byte[] content) {
    	return writeFile(new File(path), content);
    }
    
   	public static boolean writeFile(File file, byte[] content) {
    	boolean isOk = false;
    	
    	if (content == null || content.length <= 0) {
    		Log.e(TAG, "content is empty!");
    		return false;
    	}
    	
    	if (!makeParentDirs(file)) {
    		Log.e(TAG, "make dirs failed! "+ file.getParent());
    		return false;
    	}
    	
		if (file.exists()) file.delete();
    	
    	FileOutputStream fos = null;
    	try {
    		fos = new FileOutputStream(file);
    		fos.write(content);
    		isOk = true;
    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally {
    		try {
    			if (fos != null) fos.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	
    	return isOk;
    }
    
    public static boolean writeFile(String dir, String name, byte[] content) {
    	File file = new File(dir, name);
    	return writeFile(file, content);
    }
    
    public static byte[] readFile(String dir, String name) {
    	File file = new File(dir, name);
    	return readFile(file);
    }
    
    public static byte[] readFile(String path) {
    	File file = new File(path);
    	return readFile(file);
    }

	public static String readLine(File file) {
		if (!file.exists() || !file.isFile()) {
			Log.e(TAG, file.getAbsolutePath() + " is not exist or not a file!");
			return null;
		}
		if (file.length() <= 0) return null;

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			return br.readLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try { if (br != null) br.close(); } catch (IOException e) {}
		}

		return null;
	}

    public static byte[] readFile(File file) {
		if (!file.exists() || !file.isFile()) {
			Log.e(TAG, file.getAbsolutePath() + " is not exist or not a file!");
			return null;
		}
		
		if (file.length() <= 0) {
			return null;
		}
		
		byte[] content = new byte[(int) file.length()];
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			fis.read(content);
		} catch (FileNotFoundException e) {
			content = null;
			e.printStackTrace();
		} catch (IOException e) {
			content = null;
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
    	return content;
    }
    
    //--------------------
	public static String copyPrepare(String src, String dst) {
		return copyPrepare(new File(src), new File(dst));
	}
	
	public static String copyPrepare(File src, File dst) {
		String outPath = dst.getAbsolutePath();
		File dstFile = dst;
		if (dstFile.exists()) {
			if (dstFile.isDirectory()) {
				File srcFile = src;
				outPath = dst.getAbsolutePath() + File.separator + srcFile.getName();
			} else {
				dstFile.delete();
			}
		} else {
			File parent = dstFile.getParentFile();
			if (!parent.exists()) {
				parent.mkdirs();
			}
		}
		return outPath;
	}
	
	public static void copyFileByStream(InputStream in, OutputStream out) 
			throws IOException {
		int count = 0;
		byte[] buffer = new byte[1024*10];
		do {
			count = in.read(buffer);
			if (count <= 0) {
				break;
			} else {
				out.write(buffer, 0, count);
			}
		} while(true);
		out.flush();
	}
	
	public static void copyFile(InputStream in, RandomAccessFile out) 
			throws IOException {
		int count = 0;
		byte[] buffer = new byte[1024*10];
		do {
			count = in.read(buffer);
			if (count <= 0) {
				break;
			} else {
				out.write(buffer, 0, count);
			}
		} while(true);
	}
	
	public static boolean rmDir(String path) {
		return rmDir(new File(path));
	}
	
	public static boolean rmDir(File file) {
		boolean isOk = false;
		
		if (!file.exists()) {
			return true;
		}
		
		if (file.isFile()) {
			return file.delete();
		}
		
		//file.isDirectory()
		File [] subFiles = file.listFiles();
		if (subFiles == null || subFiles.length<=0) {
			return file.delete();
		}
		
		for (File f : subFiles) {
			if (f.isFile()) {
				isOk = f.delete();
				if (!isOk) return isOk;
			} else if (f.isDirectory()) {
				isOk = rmDir(f);
				if (!isOk) return isOk;
			}
		}
		
		return file.delete();
	}
	
	public static void copyFileByPath(String srcpath,String dstpath) throws Exception {
		copyFile(new File(srcpath), new File(dstpath));
	}
	
	public static void copyFile(File src, File dst) throws Exception {
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			Log.i(TAG, src.getAbsolutePath()+ " => " + dst.getAbsolutePath());
			String dstpath = copyPrepare(src, dst);
			is = new FileInputStream(src);
			fos = new FileOutputStream(new File(dstpath));
			copyFileByStream(is,fos);
		} finally {
			if (is != null) is.close();
			if (fos != null) fos.close();
		}
	}

	//--------------
	//如果suffix为空，则不过滤后缀名
	public static List<File> scanDir(String dir, String suffix) {
		List<File> list = new ArrayList<File>();
		File dirFile = new File(dir);
		if (!dirFile.exists()) {
			Log.e(TAG, "file is not exists: "+dir);
			return list;
		}
		if (!dirFile.isDirectory()) {
			Log.e(TAG, dir + " is not dir");
			return list;
		}
		scanFiles(new File(dir), list, suffix);
		return list;
	}
	
	//-----------------------------------
	public static final int MAX_LEVEL = 10;
	public static final String MAX_HINT = "向下搜索，最多10层，避免文件系统出错时，无限递归";
	
	/**  扫描指定后缀的文件，并保持到到List中，返回所有文件(包括后缀不是suffix的文件)大小总和，
	 * 如果suffix为空，则不过滤后缀名 */
	public static long scanFiles(File dir, List<File> list, String suffix) {
		return scanFiles(dir, list, suffix, 0);
	}
	
	private static long scanFiles(File dir, List<File> list, String suffix, int level) {
		if (level > MAX_LEVEL) {
			Log.e(TAG, "[scanFiles] "+MAX_HINT);
			return 0;
		}
		
		long size = 0;
		File[] files = dir.listFiles();
		if (files == null || files.length <= 0) return 0;
		if (!TextUtils.isEmpty(suffix)) {
			suffix = suffix.toLowerCase();
		}
		for (File f:files) {
			if (f.isDirectory()) {
				size += scanFiles(f, list, suffix, level+1);
			} else if (f.isFile()) {
				size += f.length();
				if (TextUtils.isEmpty(suffix)) {//如果suffix为空，则不过滤后缀名
					list.add(f);
				} else {//转为小写来比较
					String path = f.getAbsolutePath().toLowerCase();
					if (path.endsWith(suffix)) {
						list.add(f);
					}
				}
			} else {
				Log.e(TAG, "[scanFiles] it is not file or directory: "+f.getAbsolutePath());
			}
		}
		return size;
	}

	public static String getFileEncoding(File file) {
		String encoding = "GBK";
		byte[] codes = new byte[3];

		try {
			FileInputStream in = new FileInputStream(file);
			in.read(codes);
			encoding = getBytesEncoding(codes);
			in.close();
		} catch (IOException e) {
			Log.i(TAG, "[getFileEncoding] " + e);
		}
		
		return encoding;
	}
	
	public static String getBytesEncoding(byte[] data) {
		String code = "GBK";
		int d0 = byte2Integer(data[0]);
		int d1 = byte2Integer(data[1]);
		int d2 = 0;

		if (d0 == 0xff && d1 == 0xfe) code = "UTF-16LE";
		else if (d0 == 0xfe && d1 == 0xff) code = "UTF-16BE";
		else if (d0 == 0xef && d1 == 0xbb) {
			d2 = byte2Integer(data[2]);
			if (d2 == 0xbf) code = "UTF-8";
		}
		if (BuildConfig.DEBUG) Log.i(TAG, "[getBytesEncoding]"+
				   Integer.toHexString(d0) +","+
						Integer.toHexString(d1)+"," +
						Integer.toHexString(d2)+"," +
						code);
		return code;
	}
	
	public static int byte2Integer(byte b) {
		return b < 0 ? b + 256 : b;
	}
	
	public static String byte2HexString(byte b) {
		String hexString = Integer.toHexString(byte2Integer(b));
		return (hexString.length() == 1) ? "0"+hexString : hexString;
	}
}
