package com.example.rx.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.util.StackTrace;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

// RxJava系列教程 https://www.jianshu.com/nb/14302692
// https://www.jianshu.com/p/a406b94f3188 Android Rxjava：这是一篇 清晰 & 易懂的Rxjava 入门教程
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        findViewById(R.id.button_rx1).setOnClickListener(this);
//        findViewById(R.id.button_rx2).setOnClickListener(this);
//        findViewById(R.id.button_just).setOnClickListener(this);
//        findViewById(R.id.button_array).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_rx1: testRx1(); break;
            case R.id.button_rx2: testRx2(); break;
            case R.id.button_just: testJust(); break;
            case R.id.button_array: testFromArray(); break;
            case R.id.button_iterable: testFromIterable(); break;
            case R.id.button_defer: testDefer(); break;
            case R.id.button_timer: testTimer(); break;
            case R.id.button_interval: testInterval(); break;
            case R.id.button_interval_range: testIntervalRange(); break;
            case R.id.button_range: testRange(); break;
            case R.id.button_range_long: testRangeLong(); break;
            case R.id.btn_map: testMap(); break;
            case R.id.btn_flatmap: testFlatMap(); break;
            case R.id.btn_concatmap: testConcatMap(); break;
            case R.id.btn_buffer: testBuffer(); break;
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

    private Integer mIndex = 0;
    private Observable<Integer> mDeferObservable;

    private void testDefer() {
        //通过defer 定义被观察者对象
        if (mDeferObservable == null) {
            mDeferObservable = Observable.defer(
                    new Callable<ObservableSource<? extends Integer>>() {
                        @Override
                        public ObservableSource<? extends Integer> call() throws Exception {
                            mIndex++;
                            Log.i(TAG, "[call] defer index=" + mIndex);
                            return Observable.just(mIndex);
                        }
                    });
        }
        //观察者开始订阅
        mDeferObservable.subscribe(new LogObserver<Integer>("defer " + mIndex));
    }

    private void testTimer() {
        // 本质 = 延迟指定时间后，调用一次 onNext(0)，一般用于检测
        // 该例子 = 延迟2s后，发送一个long类型数值0
        Observable.timer(2, TimeUnit.SECONDS)
                .subscribe(new LogObserver<Long>("timer"));
        // 注：timer操作符默认运行在一个新线程上
        // 也可自定义线程调度器（第3个参数）：timer(long,TimeUnit,Scheduler)
    }

    private void testInterval() {
        // 发送事件的特点：每隔指定时间 就发送 事件
        // 发送的事件序列 = 从0开始、无限递增1的的整数序列
        // 参数说明：
        // 参数1 = 第1次延迟时间；
        // 参数2 = 间隔时间数字；
        // 参数3 = 时间单位；
        Observable.interval(3, 1, TimeUnit.SECONDS)
                .subscribe(new LogObserver<Long>("interval"));
        // 注：interval默认在computation调度器上执行
        // 也可自定义指定线程调度器（第4个参数）：interval(long,long,TimeUnit,Scheduler)
    }

    private void testIntervalRange() {
        //发送事件的特点：每隔指定时间 就发送 事件，可指定发送的数据的数量
        //发送的事件序列 = 从0开始、无限递增1的的整数序列
        //作用类似于interval（），但可指定发送的数据的数量
        // 参数说明：
        // 参数1 = 事件序列起始点；
        // 参数2 = 事件数量；
        // 参数3 = 第1次事件延迟发送时间；
        // 参数4 = 间隔时间数字；
        // 参数5 = 时间单位
        Observable.intervalRange(3, 10, 2, 1, TimeUnit.SECONDS)
                .subscribe(new LogObserver<Long>("intervalRange"));
    }

    private void testRange() {
        // 发送事件的特点：连续发送 1个事件序列，可指定范围
        // 发送的事件序列 = 从0开始、无限递增1的的整数序列
        // 作用类似于intervalRange（），但区别在于：无延迟发送事件
        // 参数说明：
        // 参数1 = 事件序列起始点；
        // 参数2 = 事件数量；
        // 注：若设置为负数，则会抛出异常
        Observable.range(7, 10).subscribe(new LogObserver("range"));
    }

    private void testRangeLong() {
        //与range（）类似，此处不作过多描述
        Observable.rangeLong(5, 10).subscribe(new LogObserver("rangeLong"));
    }

    private void testMap() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            // 1. 被观察者发送事件 = 参数为整型 = 1、2、3
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                StackTrace.printStackTrace("testMap[subscribe]");
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
            //// 2. 使用Map变换操作符中的Function函数对被观察者发送的事件进行统一变换：整型变换成字符串类型
        }).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                Log.i(TAG, "使用 Map变换操作符 将事件" + integer
                        +"的参数从 整型"+integer + " 变换成 字符串类型" + integer);
                StackTrace.printStackTrace("testMap[apply]");
                return "字串 "+integer;
            }
        }).subscribe(new Consumer<String>() {
            // 3. 观察者接收事件时，是接收到变换后的事件 = 字符串类型
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
                StackTrace.printStackTrace("testMap[accept]");
            }
        });
    }

    private void testFlatMap() {
        //无序的将被观察者发送的整个事件序列进行变换
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
            // 采用flatMap（）变换操作符
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("我是事件 "+integer+" 拆分后的子事件 "+i);
                    // 通过flatMap中将被观察者生产的事件序列先进行拆分，再将每个事件转换为一个新的发送三个String事件
                    // 最终合并，再发送给被观察者
                }
                return Observable.fromIterable(list);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.i(TAG, "[testFlatMap]"+s);
            }
        });
        //新合并生成的事件序列顺序是无序的，即 与旧序列发送事件的顺序无关
    }

    private void testConcatMap() {
        //拆分 & 重新合并生成的事件序列 的顺序 = 被观察者旧序列生产的顺序
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
            // 采用concatMap（）变换操作符
        }).concatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("我是事件 " + integer + "拆分后的子事件" + i);
                    // 通过concatMap中将被观察者生产的事件序列先进行拆分，再将每个事件转换为一个新的发送三个String事件
                    // 最终合并，再发送给被观察者
                }
                return Observable.fromIterable(list);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.i(TAG, "[testConcatMap]"+s);
            }
        });
    }

    private void testBuffer() {
        // 被观察者 需要发送5个数字
        Observable.just(1, 2, 3, 4, 5)
                .buffer(3, 1) // 设置缓存区大小 & 步长
                // 缓存区大小 = 每次从被观察者中获取的事件数量
                // 步长 = 每次获取新事件的数量
                .subscribe(new Observer<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }
                    @Override
                    public void onNext(List<Integer> stringList) {
                        //
                        Log.d(TAG, "[testBuffer] 缓存区里的事件数量 = " +  stringList.size());
                        for (Integer value : stringList) {
                            Log.d(TAG, "[testBuffer] 事件 = " + value);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "[testBuffer] 对Error事件作出响应" );
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "[testBuffer] 对Complete事件作出响应");
                    }
                });
    }
}
