package com.example.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
	private static final String TAG = FileUtils.class.getSimpleName();
	
	public static boolean exist(String path) {
		File file = new File(path);
		if (file.exists()) {
			return true;
		}
		return false;
	}
	
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
