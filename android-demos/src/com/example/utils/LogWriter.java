package com.example.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LogWriter {

	// 可以写作配置：true写文件; false输出控制台
	private static boolean fileLog = true;
	public static String logFileName = "/sdcard/check2.log";
	private static OutputStream outStream = null;
	
	public static void setLogFile(String file) {
		logFileName = file;
		if (outStream != null) {
			try {
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			outStream = null;
		}
	}

	public static void log(String info) throws IOException {
		OutputStream out = getOutputStream();
		out.write(info.getBytes("utf-8"));
	}

	public static OutputStream getOutputStream() throws IOException {
		if (fileLog) {
			if (outStream == null) {
				File file = new File(logFileName);
				if (!file.exists())
					file.createNewFile();
				outStream = new FileOutputStream(file);
			} 
			return outStream;
		} else {
			return System.out;
		}
	}
}
