package com.example.receiver;

public interface OnNetworkListener {

	/**
	 * @brief 网络连接�?
	 * @param isWifi 是否Wifi连接
	 */
	void onConnected(boolean isWifi);

	/**
	 * @brief 网络断开�?
	 */
	void onDiscon