package com.example.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

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

    /**
     * 获取文件后缀名，不带`.`
     * @param file 文件
     * @return 文件后缀
     */
    public static String getExtension(File file) {
        String name = file.getName();
        int i = name.lastIndexOf('.');
        int p = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        return i > p ? name.substring(i + 1) : "";
    }
    
    /**
     * @brief 判断外部存储是否挂载
     */
    public static boolean isExternalStorageMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * @brief 获取文件系统路径内的可用空间，单位bytes
     * @param path 文件系统路径
     */
    public static int getAvailableBytes(String path) {
        StatFs sf = new StatFs(path);
        int blockSize = sf.getBlockSize();
        int availCount = sf.getAvailableBlocks();
        return blockSize * availCount;
    }

    /**
     * @brief 存储大小格式化为可阅读的字串
     */
    public static String readableFileSize(long size) {
        if (size <= 0)
            return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " "
                + units[digitGroups];
    }
    
    public static boolean exists(String filepath) {
    	File file = new File(filepath);
    	return file.exists();
    }
    
    public static boolean writeFile(File file, byte[] content) {
    	boolean isOk = false;
    	
    	if (!makeParentDirs(file)) {
    		Log.e(TAG, "make dirs failed! "+ file.getParent());
    		return isOk;
    	}
    	System.out.println(file.getAbsolutePath());
    	
    	FileOutputStream fos = null;
    	try {
    		fos = new FileOutputStream(file);
    		fos.write(content);
    		isOk = true;
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
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
    
    public static byte[] readFile(File file) {
		if (!file.exists() || !file.isFile()) {
			Log.e(TAG, file.getAbsolutePath() + " is not exist or not a file!");
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
		String outPath = dst;
		File dstFile = new File(dst);
		if (dstFile.exists()) {
			if (dstFile.isDirectory()) {
				File srcFile = new File(src);
				outPath = dst + File.separator + srcFile.getName();
			} else {
//				Log.e(TAG, "error: " + dst + " is a file!");
//				outPath = null;
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
}
