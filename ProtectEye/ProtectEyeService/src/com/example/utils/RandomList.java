package com.example.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.util.Log;

public class RandomList {
	private static final String TAG = RandomList.class.getSimpleName();
	private int min = 0;
	private int max = 0;
	private int[] list;
	
	public RandomList(int size) {
		max = size;
		reset();
	}
	
	public void reset() {
		Random random = new Random();
		int count = max-min;
		list = new int[count];
		
		for (int i=0; i<count; i++) {
			list[i] = min+i;
		}
		
		int times = count/2;
		for (int i=0; i<times; i++) {
			int r = random.nextInt(count);
			int t = list[i];
			list[i] = list[r];
			list[r] = t;
		}
		
		for (int i=0; i<count; i++) {
			Log.i(TAG, "["+i+"] "+list[i]);
		}
	}
	
	public int get(int index) {
		if (list != null && list.length > 0) {
			return list[ index%list.length ];
		}
		return 0;
	}
	
	public int size() {
		if (list != null) {
			return list.length;
		}
		return 0;
	}
}
