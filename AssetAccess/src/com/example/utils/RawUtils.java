package com.example.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.res.Resources;

public class RawUtils {
	public static boolean copyFile(Resources resources, int id, String dstPath) {
		boolean isOk = false;
		InputStream in = null;
		FileOutputStream out = null;
		
		try {
			File file = new File(dstPath);
			File parent = file.getParentFile();
			if (!parent.exists()) {
				parent.mkdirs();
			}
			if (file.exists()) {
				file.delete();
			}
			
			file.createNewFile();
			in = resources.openRawResource(id);
			out = new FileOutputStream(file);
			FileUtils.copyFileByStream(in, out);
			isOk = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null)	in.close();
				if (out != null) out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return isOk;
	}
}
