package com.example.rx.demo;

import android.util.Log;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class LogObserver<T> implements Observer<T> {
    private static final String TAG = LogObserver.class.getSimpleName();

    private String tag;

    public LogObserver(String tag) {
        this.tag = tag;
    }

    @Override
    public void onSubscribe(Disposable d) {
        Log.d(TAG, tag+" 开始采用subscribe连接");
    }

    @Override
    public void onNext(T value) {
        Log.d(TAG, tag+" 接收到了事件" + value);
    }

    @Override
    public void onComplete() {
        Log.d(TAG, tag+" 对Complete事件作出响应");
    }

    @Override
    public void onError(Throwable e) {
        Log.d(TAG, tag+" 对Error事件作出响应");
    }
}
