package com.example.rx.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

// RxJava系列教程 https://www.jianshu.com/nb/14302692
// https://www.jianshu.com/p/a406b94f3188 Android Rxjava：这是一篇 清晰 & 易懂的Rxjava 入门教程
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button_rx1).setOnClickListener(this);
        findViewById(R.id.button_rx2).setOnClickListener(this);
        findViewById(R.id.button_just).setOnClickListener(this);
        findViewById(R.id.button_array).setOnClickListener(this);
        findViewById(R.id.button_iterable).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_rx1: testRx1(); break;
            case R.id.button_rx2: testRx2(); break;
            case R.id.button_just: testJust(); break;
            case R.id.button_array: testFromArray(); break;
            case R.id.button_iterable: testFromIterable(); break;
        }
    }

    private void testRx1() {
        //  1. 创建被观察者 Observable 对象
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            // 2. 在复写的subscribe（）里定义需要发送的事件
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                // 通过 ObservableEmitter类对象产生事件并通知观察者
                // ObservableEmitter类介绍
                // a. 定义：事件发射器
                // b. 作用：定义需要发送的事件 & 向观察者发送事件
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        });

        //步骤2：创建观察者 Observer 并 定义响应事件行为
        Observer<Integer> observer = new LogObserver<>("testRx1");

        // 步骤3：通过订阅（subscribe）连接观察者和被观察者
        observable.subscribe(observer);
    }

    private void testRx2() {
        // RxJava的流式操作
        Observable.create(new ObservableOnSubscribe<Integer>() {
            // 1. 创建被观察者 & 生产事件
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        })
        // 2. 通过通过订阅（subscribe）连接观察者和被观察者
        // 3. 创建观察者 & 定义响应事件的行为
        .subscribe(new LogObserver<Integer>("testRx2"));
    }

    private void testJust() {
        // 1. 创建时传入整型1、2、3、4
        // 在创建后就会发送这些对象，相当于执行了onNext(1)、onNext(2)、onNext(3)、onNext(4)
        // 注：最多只能发送10个参数
        Observable.just(1,2,3,4)
        // 至此，一个Observable对象创建完毕，以下步骤仅为展示一个完整demo，可以忽略
        // 2. 通过通过订阅（subscribe）连接观察者和被观察者
        // 3. 创建观察者 & 定义响应事件的行为
        .subscribe(new LogObserver<Integer>("testJust"));
    }

    private void testFromArray() {
        // 1. 设置需要传入的数组
        Integer[] items = new Integer[] {5,6,7,9,6,108};
        // 2. 创建被观察者对象（Observable）时传入数组
        // 在创建后就会将该数组转换成Observable & 发送该对象中的所有数据
        Observable.fromArray(items)
                .subscribe(new LogObserver<Integer>("testFromArray"));
        // 可发送10个以上参数
        // 若直接传递一个list集合进去，否则会直接把list当做一个数据元素发送
    }

    //快速发送集合
    private void testFromIterable() {
        // 1. 设置一个集合
        List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(2);
        list.add(1);

        // 2. 通过fromIterable()将集合中的对象 / 数据发送出去
        Observable.fromIterable(list)
                .subscribe(new LogObserver<Integer>("testFromIterable"));
    }
}
