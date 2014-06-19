package com.svo.laohan.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.text.TextUtils;


public class StringUtil {
	/**
	 * 得到文件的后缀名
	 * @param fileName
	 * @return
	 */
	public static String getSuffix(String fileName) {
		if (!TextUtils.isEmpty(fileName) && fileName.contains(".")) {
			return fileName.substring(fileName.lastIndexOf(".")+1);
		}
		return "";
	}
	/**
	 * 对URL编码
	 * @param uri 下载地址或者路径
	 * @return
	 */
	public static String encode(String uri) {
		if (uri.startsWith("http://bcs.duapp.com/")) {
			String downUrl = uri.replace("http://bcs.duapp.com/", "");
			try {
				downUrl = "http://bcs.duapp.com/"+URLEncoder.encode(downUrl, "utf-8").replace("%2F", "/");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return downUrl;
		}else if (uri.startsWith("/")){
			try {
				return "http://bcs.duapp.com/laohan"+URLEncoder.encode(uri, "utf-8").replace("%2F", "/");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return "";
		}else {
			return uri;
		}
	}
	/**
	 * 转换下载次数
	 * @param downNum
	 * @return
	 */
	public static String conDownNum(int downNum) {
		if (downNum < 1000) {
			return "1千";
		}else if (downNum < 10000){
			return (downNum/1000)+"千";
		}else {
			return (downNum/10000)+"万";
		}
	}
	/**
	 * 将文件名的后缀名去掉
	 * @param str
	 * @return
	 */
	public static String delSuffix(String str) {
		int index = str.lastIndexOf(".");
		if (index >= 0) {
			return str.substring(0, index);
		} else {
			return str;
		}
	}
	
	/**
     * 根据一个文件的绝对路径得到文件所在的目录路径和文件名
     * @param filePath 某文件的绝对路径
     * @return 长度为2的数组,第一个元素是目录路径,第二个元素是文件名.可能为null
     */
    public static String[] sepPath(String filePath) {
    	if (filePath == null || !filePath.contains("/") ) {
			return null;
		}
    	if (filePath.endsWith("/")) {
			filePath = filePath.substring(0, filePath.length()-1);
		}
    	int index = filePath.lastIndexOf("/")+1;
    	String[] arr = new String[2];
    	arr[0] = filePath.substring(0,index);
    	arr[1] = filePath.substring(index);
    	return arr;
	}
	/**
	 * 将文件转换为KB或者M
	 * @param size
	 * @return
	 */
	public static String sizeConvert(long size) {
		if (size <= 1024) {
			return "1Kb";
		}else if (size > 1024 && size < 1024*1024) {
			return size/1024+"Kb";
		}else {
			return size/1024/1024+"M";
		}
	}
}
