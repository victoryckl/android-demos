package com.svo.laohan.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class FileUtil {
	
	/**
	 * 对字符串进行编码
	 * @param str
	 * @return
	 */
	public static String encode(String str) {
		try {
			return new String(str.getBytes("gbk"), "iso-8859-1").trim();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 对字符串进行解码
	 * @param str
	 * @return
	 */
	public static String decode(String str) {
		try {
			return new String(str.getBytes("iso-8859-1"),"gbk");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	/**
	 * 判断某文件是否存在
	 * @param resId 资源ID
	 * @return
	 */
	public static boolean isFileExist(String filePath) {
		File file = new File(filePath);
		if (file != null && file.exists() && file.length() > 0) {
			return true;
		}else {
			return false;
		}
	}
	// 复制文件
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 8];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }
    public static void copyFile(String source,String target){
    	try {
			copyFile(new File(source), new File(target));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
