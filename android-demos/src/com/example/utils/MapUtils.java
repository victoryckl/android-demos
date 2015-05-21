package com.example.utils;

import java.util.Map;

public class MapUtils {

	public static boolean isEmpty(Map<?, ?> map) {
		if (map == null || map.isEmpty()) {
			return true;
		}
		return false;
	}
	
	private MapUtils() {}
}
