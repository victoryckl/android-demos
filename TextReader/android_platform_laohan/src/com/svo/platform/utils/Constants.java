package com.svo.platform.utils;

import android.os.Environment;

public abstract class Constants {
	public Constants() {
		overrideConstants();
	}

	// 图片浏览器 模块,当用户保存图片的目录
	public static String SAVE_PIC_PATH_NAME = "weitu";
	public static String SAVE_PIC_PATH = Environment.getExternalStorageDirectory() + "/" + SAVE_PIC_PATH_NAME + "/";
	/* 网络连接超时时间 */
	public static int CONNECT_OUT_TIME = 15 * 1000;
	// 有道云笔记
	public static String YOUDAO_API_KEY = "c6785ca2e9b2eac9714c1d20c0b62ab2";
	public static String YOUDAO_SECRET_KEY = "819a323b5ada32ceb31f4c7cc441b151";
	/* 微博分享 项目中需要覆盖 */
	// 新浪
	public static String SINA_APPKEY = "250763553";
	public static String SINA_APPSECRET = "ea5904b85318627ab06543743db490fd";
	public static String SINA_CALLBACK_URL = "http://www.baidu.com";
	// 是否支持分享URL图片
	public static boolean IS_SUPPORT_HTTP_PIC = false;
	// 腾讯
	// 腾讯
	public static String TENCENT_APPKEY = "801382573";
	public static String TENCENT_APPSECRET = "139493ee4f5f1438e035eb85f62828bb";
	// 微信ID
	public static String WEIXIN_ID = "wxf5818b5ab8d48990";
	// 人人网
	public static String REN_API_KEY = "8e0c2490a0354dada59d0b9a437fac08";
	public static String REN_SECRET_KEY = "eac14a378af9449f98958593671d7aae";
	public static String REN_APP_ID = "215836";
	// 分享到人人网之后显示客户端名称
	public static String REN_FROM = "老汉阅读器";
	// 与上面对应的链接
	public static String REN_FROM_LINK = "http://www.mumayi.com/android-249875.html";
	public static String REN_FROM_MSG = "老汉阅读器，阅读的不只是文字";
	// QQ互联
	public static String QZONE_ID = "100341960";
	/** 腾讯微博 */
	// 腾讯微博的名字
	public static String QQ_GUANFANG = "duweibn";
	// 推广 的文字
	public static String SHARE_CONTENT = "老汉阅读器是一款可以阅读多种格式的经典电子书阅读器,支持阅读word文档,支持格式包括doc,txt,epub,oeb,fb2,mobi,prc,rtf等.可搜索感兴趣书籍,还可以查看网友共享的书籍.下载地址: "+REN_FROM_LINK;

	/**
	 * 新浪微博微博关注
	 */
	public static long SINA_GUANFANG_UID = 1767686853L;// UID
	public static String SINA_GUANFANG = "svosvo";// 微博名

	/**
	 * 子类修改父类常量的方法
	 */
	protected abstract void overrideConstants();
}
