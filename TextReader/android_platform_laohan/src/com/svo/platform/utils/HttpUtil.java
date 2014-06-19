package com.svo.platform.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 网络请求工具类
 * @author duweibin
 */
public class HttpUtil {

	private static AsyncHttpClient client = new AsyncHttpClient();
	/**
	 * get异步请求
	 * @param url 网络地址
	 * @param params 参数，可为null
	 * @param responseHandler 回调接口
	 */
	public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.get(url, params, responseHandler);
	}
	/**
	 * post异步请求
	 * @param url 网络地址
	 * @param params 参数，可为null
	 * @param responseHandler 回调接口
	 */
	public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.post(url, params, responseHandler);
	}
	/**
	 * get请求。根据url地址得到服务器返回的文本数据.非异步请求. HttpClient类内部已使用gzip压缩请求
	 * 
	 * @param strUrl url地址
	 * @return 服务器返回的数据 或者null
	 */
	public static String getRequest(String strUrl) {
		HttpClient client = new DefaultHttpClient();
		HttpParams httpParams = client.getParams();
		String result = null; // 返回结果
		try {
			HttpGet get = new HttpGet(strUrl);
			// 设置网络超时参数
			HttpConnectionParams.setConnectionTimeout(httpParams, Constants.CONNECT_OUT_TIME);
			HttpConnectionParams.setSoTimeout(httpParams, 30000);
			HttpResponse response = client.execute(get);

			int statusCode = response.getStatusLine().getStatusCode(); // 网络状态
			if (statusCode == 200) {
				result = EntityUtils.toString(response.getEntity(), "utf-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * post请求方法,非异步请求
	 * 
	 * @param url 发送请求的URL
	 * @param params 请求参数
	 * @return 服务器响应字符串
	 * @throws Exception
	 */
	public static String postRequest(String url, Map<String, String> rawParams) throws Exception {
		// 创建HttpClient对象
		HttpClient httpClient = new DefaultHttpClient();
		// 创建HttpPost对象。
		HttpPost post = new HttpPost(url);
		// 如果传递参数个数比较多的话可以对传递的参数进行封装
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (String key : rawParams.keySet()) {
			// 封装请求参数
			params.add(new BasicNameValuePair(key, rawParams.get(key)));
		}
		// 设置请求参数
		post.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
		// 发送POST请求
		HttpResponse httpResponse = httpClient.execute(post);
		// 如果服务器成功地返回响应
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			// 获取服务器响应字符串
			String result = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
			return result;
		}
		return null;
	}
}
