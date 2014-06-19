package com.svo.laohan.util;

import android.os.Environment;

import com.svo.platform.utils.Constants;


public class Constant extends Constants{
	public Constant(){
		super();
	}
	public static String ROOT = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/com.svo.laohan/cache";
	/*新版本的保存路径 */
	public static  String UPDATE_PATH = ROOT+"/update";
	public static String WEL_PIC_PATH = ROOT+"/welcome";
	public static String Save_path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Books/";
	public final static String mbApiKey = "WxB8hPjfISNcaIYO2g3K3TrB";
	public final static String reRoot = "/网友共享/";
	public final static String reShare = "/网友共享/";
	public final static String gen = "http://1.dubinwei.duapp.com/";
	public final static String gen2 = "http://2.dubinwei.duapp.com/";
	public final static String share_pic = "http://1.dubinwei.duapp.com/static/share.jpg";
	public final static String LAOHAN_SITE = "http://1.playandroid.duapp.com/laohan/laohan.jsp";
	public  static boolean isShowSe = true;
	@Override
	protected void overrideConstants() {
		// 有道云笔记
		Constants.YOUDAO_API_KEY = "c6785ca2e9b2eac9714c1d20c0b62ab2";
		Constants.YOUDAO_SECRET_KEY = "819a323b5ada32ceb31f4c7cc441b151";
		/* 微博分享   项目中需要覆盖 */
		//新浪
		Constants.SINA_APPKEY = "250763553";
		Constants.SINA_APPSECRET = "ea5904b85318627ab06543743db490fd";
		Constants.SINA_CALLBACK_URL = "http://www.baidu.com";
		//是否支持分享URL图片
		Constants.IS_SUPPORT_HTTP_PIC = false;
		//腾讯
		Constants.TENCENT_APPKEY = "801382573";
		Constants.TENCENT_APPSECRET = "139493ee4f5f1438e035eb85f62828bb";
	    //微信ID
		Constants.WEIXIN_ID = "wxf5818b5ab8d48990";
	    //人人网
		Constants.REN_API_KEY = "8e0c2490a0354dada59d0b9a437fac08";
		Constants.REN_SECRET_KEY = "eac14a378af9449f98958593671d7aae";
		Constants.REN_APP_ID = "215836";
		//分享到人人网之后显示客户端名称
		Constants.REN_FROM = "老汉阅读器";
		//与上面对应的链接
		Constants.REN_FROM_LINK = "http://1.playandroid.duapp.com/laohan/laohan.jsp";
		Constants.REN_FROM_MSG = "经典电子阅读器推荐";
		//QQ互联
		Constants.QZONE_ID = "100341960";
		/**腾讯微博*/
		//腾讯微博的名字
		Constants.QQ_GUANFANG = "duweibn";
		//推广 的文字
		Constants.SHARE_CONTENT = 
				"老汉阅读器是一款可以阅读多种格式的经典电子书阅读器,支持阅读word文档,支持格式包括doc,txt,epub,oeb,fb2,mobi,prc,rtf等.可搜索感兴趣书籍,还可以查看网友共享的书籍.下载地址: http://www.mumayi.com/android-249875.html";
	}
}
