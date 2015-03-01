package com.example.utils;

public class TimeStat {
	private long startTime = 0;
	private long endTime = 0;
	
	public void startTime() {
		startTime = endTime = System.currentTimeMillis();
	}
	
	public String updateTime() {
		endTime = System.currentTimeMillis();
		long time = (endTime - startTime) / 1000;
		long s = time % 60;
		long m = (time / 60) % 60;
		long h = (time / 3600) % 60;
		String text = String.format("%02d:%02d:%02d", h, m, s);
		return text;
	}
	
	public String updateTime(boolean print) {
		String text = updateTime();
		if (print) {
			System.out.println(text);
		}
		return text;
	}
	
	public String updateTime(String msg) {
		String text = String.format("[%s]%s", updateTime(), msg);
		System.out.println(text);
		return text;
	}
	
	public String updateTimeMillis() {
		endTime = System.currentTimeMillis();
		long time = endTime - startTime;
		String text = String.format("%06d", time);
		return text;
	}
	
	public String updateTimeMillis(boolean print) {
		String text = updateTimeMillis();
		if (print) {
			System.out.println(text);
		}
		return text;
	}
	
	public String updateTimeMillis(String msg) {
		String text = String.format("[%s]%s", updateTimeMillis(), msg);
		System.out.println(text);
		return text;
	}
	
	public TimeStat() {}
}
