package com.svo.laohan.util;

public class TypeUtil {
	private final static String imgType = "jpg,jpeg,png,gif,tif,bmp,ico,pcx,tga";
	private final static String audioType = "mp3,wav,amr,wma,ogg,mp2,m4a,m4r,mid";
	private final static String videoType = "mp4,avi,3gp,rmvb,rm,wmv,mkv,mpg,mpeg,vob,flv,swf,mov";
	private final static String docType = "txt,doc,pdf,wps,xls,docx,htm,html,fb2,epub,xml,ppt,mobi";
	private final static String zipType = "zip,rar,7z,tar,iso,gz,gzip,jar,bz";
	private final static String apk = "apk";
	public final static int DOC = 1;
	public final static int IMAGE = 2;
	public final static int AUDIO = 3;
	public final static int VIDEO = 4;
	public final static int APK = 5;
	public final static int GIF = 6;
	/**
	 * 根据热后缀名得到文件类型
	 * @param suffix
	 * @return
	 */
	public static int getType(String suffix) {
		String s = suffix.toLowerCase();
		if (imgType.contains(s)) {
			if ("gif".equals(s)) {
				return GIF;
			}
			return IMAGE;
		} else if (audioType.contains(s)) {
			return AUDIO;
		} else if (videoType.contains(s)) {
			return VIDEO;
		} else if (docType.contains(s)) {
			return DOC;
		} else if (zipType.contains(s)){
			return DOC;
		}else if (apk.contains(s)){
			return APK;
		}
		return 0;
	}
	/**
	 * 根据文件路径得到文件类型
	 * @param path 文件路径
	 * @return
	 */
	public static int getTypeBasePath(String path) {
		String s = path.substring(path.lastIndexOf(".")+1).toLowerCase();
		return getType(s);
	}
}
