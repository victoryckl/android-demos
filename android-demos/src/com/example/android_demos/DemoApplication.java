package com.example.android_demos;

import android.app.Application;

import com.example.utils.CrashHandler;

public class DemoApplication extends Application {
	private static DemoApplication self;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		self = this;
		
        /* 全局异常崩溃处理 */
        new CrashHandler(this);
	}
	
    public static DemoApplication getInstance() {
        return self;
    }
}
