package com.example.rx.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

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
            case R.id.button_create1: testCreate1(); break;
            case R.id.button_create2: testCreate2(); break;
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
            case R.id.btn_concat: testConcat(); break;
            case R.id.btn_concatArray: testConcatArray(); break;
            case R.id.btn_merge: testMerge(); break;
            case R.id.btn_mergeArray: testMergeArray(); break;
            case R.id.btn_concat_error: testConcatError(); break;
            case R.id.btn_zip: testZip(); break;
            case R.id.btn_combine_latest: testCombineLatest(); break;
            case R.id.btn_reduce: testReduce(); break;
            case R.id.btn_collect: testCollect(); break;
            case R.id.btn_start_with: testStartWith(); break;
            case R.id.btn_count: testCount(); break;
        }
    }

    private void testCreate1() {
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
        Observer<Integer> observer = new LogObserver<>("testCreate1");

        // 步骤3：通过订阅（subscribe）连接观察者和被观察者
        observable.subscribe(observer);
    }

    private void testCreate2() {
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
        .subscribe(new LogObserver<Integer>("testCreate2"));
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

    private void testConcat() {
        // 组合多个被观察者一起发送数据，合并后 按发送顺序串行执行
        // 被观察者数量≤4个
        Observable.concat(
                Observable.just(1,2),
                Observable.fromArray(new Integer[]{3,4}),
                Observable.range(5,3))
        .subscribe(new LogObserver("testConcat"));
    }

    private void testConcatArray() {
        // 组合多个被观察者一起发送数据，合并后 按发送顺序串行执行
        // 被观察者数量可>4个
        Observable.concatArray(
                Observable.just(1,2),
                Observable.fromArray(new Integer[]{3,4}),
                Observable.range(5,3),
                Observable.just(8,9),
                Observable.just(10,11),
                Observable.just(12,13))
                .subscribe(new LogObserver("testConcatArray"));
    }

    private void testMerge() {
        // 组合多个被观察者一起发送数据，合并后 按时间线并行执行
        // 被观察者数量≤4个
        Observable.merge(
                //// 从0开始发送、共发送3个数据、第1次事件延迟发送时间 = 1s、间隔时间 = 1s
                Observable.intervalRange(0, 3, 1, 1, TimeUnit.SECONDS),
                // 从3开始发送、共发送3个数据、第1次事件延迟发送时间 = 1s、间隔时间 = 1s
                Observable.intervalRange(3, 3, 1, 1, TimeUnit.SECONDS)
        ).subscribe(new LogObserver("testMerge"));
    }

    private void testMergeArray() {
        // 组合多个被观察者一起发送数据，合并后 按时间线并行执行
        // 被观察者数量可>4个
        Observable.merge(
                //// 从0开始发送、共发送3个数据、第1次事件延迟发送时间 = 1s、间隔时间 = 1s
                Observable.intervalRange(0, 3, 1, 1, TimeUnit.SECONDS),
                // 从3开始发送、共发送3个数据、第1次事件延迟发送时间 = 1s、间隔时间 = 1s
                Observable.intervalRange(3, 3, 1, 1, TimeUnit.SECONDS)
        ).subscribe(new LogObserver("testMergeArray"));
    }

    private void testConcatError() {
        //未使用concatDelayError（）的情况
        Observable.concat(
                Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                        e.onNext(1);
                        e.onNext(2);
                        e.onNext(3);
                        // 发送Error事件，因为无使用concatDelayError，所以第2个Observable将不会发送事件
                        e.onError(new NullPointerException());
                        e.onComplete();
                    }
                }),
                Observable.just(4,5,6))
                .subscribe(new LogObserver<Integer>("testConcatError"));
        //第1个被观察者发送Error事件后，第2个被观察者则不会继续发送事件


        /////////////////////////////////////
        //使用了 concatDelayError（）的情况
        Observable.concatArrayDelayError(
                Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                        e.onNext(1);
                        e.onNext(2);
                        e.onNext(3);
                        // 发送Error事件，因为无使用concatDelayError，所以第2个Observable将不会发送事件
                        e.onError(new NullPointerException());
                        e.onComplete();
                    }
                }),
                Observable.just(4,5,6))
                .subscribe(new LogObserver<Integer>("testConcatError"));
        //第1个被观察者的Error事件将在第2个被观察者发送完事件后再继续发送
    }

    private void testZip() {
        //合并 多个被观察者（Observable）发送的事件，生成一个新的事件序列（即组合过后的事件序列），并最终发送
        //事件组合方式 = 严格按照原先事件序列 进行对位合并
        //最终合并的事件数量 = 多个被观察者（Observable）中数量最少的数量

        //创建第1个被观察者
        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                Log.i(TAG, "被观察者1发送了事件1");
                e.onNext(1);
                //为了方便展示效果，所以在发送事件后加入1s的延迟
                Thread.sleep(1000);

                Log.i(TAG, "被观察者1发送了事件2");
                e.onNext(2);
                Thread.sleep(1000);

                Log.i(TAG, "被观察者1发送了事件3");
                e.onNext(3);
                Thread.sleep(1000);

                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());// 设置被观察者1在工作线程1中工作

        //创建第2个被观察者
        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                Log.i(TAG, "被观察者2发送了事件A");
                e.onNext("A");
                Thread.sleep(1000);

                Log.i(TAG, "被观察者2发送了事件B");
                e.onNext("B");
                Thread.sleep(1000);

                Log.i(TAG, "被观察者2发送了事件C");
                e.onNext("C");
                Thread.sleep(1000);

                Log.i(TAG, "被观察者2发送了事件D");
                e.onNext("D");
                Thread.sleep(1000);

                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread());// 设置被观察者2在工作线程2中工作
        // 假设不作线程控制，则该两个被观察者会在同一个线程中工作，即发送事件存在先后顺序，而不是同时发送

        //使用zip变换操作符进行事件合并
        //注：创建BiFunction对象传入的第3个参数 = 合并后数据的数据类型
        Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(Integer integer, String s) throws Exception {
                return integer+s;
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(TAG, "onSubscribe");
            }

            @Override
            public void onNext(String value) {
                Log.i(TAG, "最终接收到的事件 =  " + value);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError");
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete");
            }
        });
    }

    private void testCombineLatest() {
        //当两个Observables中的任何一个发送了数据后，将先发送了数据的Observables 的最新（最后）一个数据 与 另外一个Observable发送的每个数据结合，最终基于该函数的结果发送数据
        //与Zip（）的区别：Zip（） = 按个数合并，即1对1合并；CombineLatest（） = 按时间合并，即在同一个时间点上合并
        Observable.combineLatest(
                Observable.just("A", "B", "C"), //// 第1个发送数据事件的Observable
                Observable.intervalRange(0, 3, 1, 1, TimeUnit.SECONDS),
                new BiFunction<String, Long, String>() {
                    @Override
                    public String apply(String o1, Long o2) throws Exception {
                        // o1 = 第1个Observable发送的最新（最后）1个数据
                        // o2 = 第2个Observable发送的每1个数据
                        Log.i(TAG, "合并的数据是： "+ o1 + "和"+ o2);
                        return o1 + o2;
                        //// 即第1个Observable发送的最后1个数据 与 第2个Observable发送的每1个数据进行合并
                    }
                }
        ).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.i(TAG, "合并的结果是： "+s);
            }
        });
    }

    private void testReduce() {
        //把被观察者需要发送的事件聚合成1个事件 & 发送
        //聚合的逻辑根据需求撰写，但本质都是前2个数据聚合，然后与后1个数据继续进行聚合，依次类推
        Observable.just(1,2,3,4,5,6)
                .reduce(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer s1, Integer s2) throws Exception {
                        Log.i(TAG, "本次计算的数据是： "+s1 +" 乘 "+ s2);
                        return s1*s2;
                        // 本次聚合的逻辑是：全部数据相乘起来
                        // 原理：第1次取前2个数据相乘，之后每次获取到的数据 = 返回的数据x原始下1个数据每
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.i(TAG, "最终计算结果："+integer);
            }
        });
    }

    private void testCollect() {
        //将被观察者Observable发送的数据事件收集到一个数据结构里
        Observable.just(1,2,3,4,5,6)
                .collect(
                        // 1. 创建数据结构（容器），用于收集被观察者发送的数据
                        new Callable<ArrayList<Integer>>() {
                            @Override
                            public ArrayList<Integer> call() throws Exception {
                                return new ArrayList<>();
                            }
                            // 2. 对发送的数据进行收集
                        }, new BiConsumer<ArrayList<Integer>, Integer>() {
                            @Override
                            public void accept(ArrayList<Integer> list, Integer integer) throws Exception {
                                // 参数说明：list = 容器，integer = 后者数据
                                list.add(integer);// 对发送的数据进行收集
                            }
                        })
                .subscribe(new Consumer<ArrayList<Integer>>() {
                    @Override
                    public void accept(ArrayList<Integer> list) throws Exception {
                        Log.i(TAG, "本次发送的数据是： "+list);
                    }
                });
    }

    private void testStartWith() {
        //在一个被观察者发送事件前，追加发送一些数据 / 一个新的被观察者
        //追加数据顺序 = 后调用先追加
        Observable.just(4, 5, 6)
                .startWith(0) // 追加单个数据 = startWith()
                .startWithArray(1, 2, 3) // 追加多个数据 = startWithArray()
                .subscribe(new LogObserver<Integer>("testStartWith"));

        Log.i(TAG, "-------------------------------------");

        Observable.just(4,5,6)
                .startWith(Observable.just(1,2,3))
                .subscribe(new LogObserver<Integer>("testStartWith2"));
    }

    private void testCount() {
        //统计被观察者发送事件的数量
        Observable.just(1,2,3,4,5,6,7)
                .count()
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long count) throws Exception {
                        Log.i(TAG, "发送的事件数量 =  "+count);
                    }
                });
    }
}
