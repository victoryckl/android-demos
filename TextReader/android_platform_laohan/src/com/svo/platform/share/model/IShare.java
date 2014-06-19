package com.svo.platform.share.model;
/**
 * @author duweibin 
 * @version 创建时间：2012-11-6 上午10:35:12
 * 绑定与解决绑定接口
 */
public interface IShare {
	void bind(); //绑定方法 
	boolean isBinded(); //判断是否绑定
	void cancelBind(); //解除绑定
}
