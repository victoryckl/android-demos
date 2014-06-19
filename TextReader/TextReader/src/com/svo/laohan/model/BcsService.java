package com.svo.laohan.model;

import java.io.File;
import java.io.IOException;

import android.util.Log;

import com.baidu.inf.iis.bcs.BaiduBCS;
import com.baidu.inf.iis.bcs.auth.BCSCredentials;
import com.baidu.inf.iis.bcs.model.Empty;
import com.baidu.inf.iis.bcs.model.ObjectMetadata;
import com.baidu.inf.iis.bcs.model.Resource;
import com.baidu.inf.iis.bcs.request.PutObjectRequest;
import com.baidu.inf.iis.bcs.response.BaiduBCSResponse;

public class BcsService {
	String host = "bcs.duapp.com";
	String accessKey = "WxB8hPjfISNcaIYO2g3K3TrB";
	String secretKey = "L3kQ6uOsGm2cLTACRgRlpRkUsOObmu5z";
	String bucket = "laohan";
//	String path = "/网友共享/";// 记得最后带斜杠
	BaiduBCS baiduBCS;
	public BcsService() {
		BCSCredentials credentials = new BCSCredentials(accessKey, secretKey);
		baiduBCS = new BaiduBCS(credentials, host);
		baiduBCS.setDefaultEncoding("UTF-8"); // Default UTF-8
	}
	/**
	 * 上传文件
	 * @param path 上传路径
	 * @param file 待上传文件
	 * @return
	 */
	public ObjectMetadata upload(String path,File file) {
		PutObjectRequest request = new PutObjectRequest(bucket, path, file);
		ObjectMetadata metadata = new ObjectMetadata();
		// metadata.setContentType("text/html");
		request.setMetadata(metadata);
		BaiduBCSResponse<ObjectMetadata> response = baiduBCS.putObject(request);
		ObjectMetadata objectMetadata = response.getResult();
		Log.i("weitu","x-bs-request-id: " +response.getRequestId());
		Log.i("weitu",objectMetadata.toString());
		return objectMetadata;
	}
	/**
	 * 创建目录，绝对路径
	 * @param path
	 */
	public  boolean mkDir(String path) {
		PutObjectRequest request = null;
		if (!path.endsWith("/")) {
			path = path + "/";
		}
		try {
			request = new PutObjectRequest(bucket, path+"a.txt", File.createTempFile("avv", "bvv"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		ObjectMetadata metadata = new ObjectMetadata();
		// metadata.setContentType("text/html");
		request.setMetadata(metadata);
		BaiduBCSResponse<ObjectMetadata> response = baiduBCS.putObject(request);
		response.getResult();
		return deleteObject(path+"a.txt");
	}
	public boolean deleteObject(String path) {
		return baiduBCS.deleteObject(bucket, path).getResult()==null?false:true;
	}
	public boolean rename(String srcPath,String destObject) {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType("image/jpeg");
		BaiduBCSResponse<Empty> response = baiduBCS.copyObject(new Resource(bucket, srcPath), new Resource(bucket, destObject));
		deleteObject(srcPath);
		return response.getResult() == null;
	}
}
