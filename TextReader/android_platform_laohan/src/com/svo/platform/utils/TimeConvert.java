package com.svo.platform.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeConvert {

	/**
	 * 换算时间,换算为日期格式,如:2012-10-29,昨天,前天,1分钟前等等
	 * @param t 长整型时间
	 */
	public static String formatDate(long t) {
		String strDate = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String systemTime = sdf.format(new Date()).toString();
		String time = sdf.format(t);

		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, -1);
		String yesterday = sdf.format(c.getTime()).toString();
		c.add(Calendar.DAY_OF_MONTH, -1);
		String yesterday_before = sdf.format(c.getTime()).toString();

		try {
			java.util.Date begin = sdf.parse(time);
			java.util.Date end = sdf.parse(systemTime);
			long between = (end.getTime() - begin.getTime()) / (1000 * 60);
			if (time.substring(0, 10).equals(yesterday.substring(0, 10))) {
				strDate = "昨天";
			} else if (time.substring(0, 10).equals(
					yesterday_before.substring(0, 10))) {
				strDate = "前天";
			} else if (between <= 0) {
				strDate = "1分钟前";
			} else if (between < 60 && between > 0) {
				strDate = Math.round(between) + "分钟前";
			} else if (between >= 60 && between < 60 * 24) {
				strDate = Math.round(between / 60) + "小时前";
			} else {
				strDate = time.substring(0, 10);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return strDate;
	}
	
	/**
	 * 将长整型毫秒数转换为日期格式yyyy-MM-dd
	 * @param t 毫秒数
	 * @return
	 */
	public static String format2Date(long t) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time = sdf.format(t);
		return time;
	}
}
