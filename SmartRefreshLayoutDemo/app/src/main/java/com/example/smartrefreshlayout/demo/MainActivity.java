package com.example.smartrefreshlayout.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private BaseRecyclerAdapter<Void> mAdapter;
    private RefreshLayout mRefreshLayout;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new BaseRecyclerAdapter<Void>(initData(10), android.R.layout.simple_list_item_2) {
            @Override
            protected void onBindViewHolder(SmartViewHolder holder, Void model, int position) {
                holder.text(android.R.id.text1, getString(R.string.item_example_number_title, position));
                holder.text(android.R.id.text2, getString(R.string.item_example_number_abstract, position));
                holder.textColorId(android.R.id.text2, R.color.colorTextAssistant);
            }
        };

        ListView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(mAdapter);

        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRefreshLayout.setEnableAutoLoadMore(true);//开启自动加载功能（非必须）

        //触发自动刷新
        mRefreshLayout.autoRefresh();

        RefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                Log.i(TAG, "[onRefresh]");
                //refreshlayout.finishRefresh(2000/*,false, false*/);//传入false表示刷新失败
                mHandler.postDelayed(() -> {
                    //if (random.nextBoolean()) {
                        //如果刷新成功
                        mAdapter.refresh(initData(40));
                        if (mAdapter.getItemCount() <= 30) {
                            //还有多的数据
                            refreshlayout.finishRefresh();
                        } else {
                            //没有更多数据（上拉加载功能将显示没有更多数据）
                            refreshlayout.finishRefreshWithNoMoreData();
                        }
                    //} else {
                        //刷新失败
                    //    refreshlayout.finishRefresh(false);
                    //    if (mAdapter.isEmpty()) {
                            //mLoadingLayout.showError();
                            //mLoadingLayout.setErrorText("随机触发刷新失败演示！");
                    //    }
                    //}
                }, 2000);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                Log.i(TAG, "[onLoadMore]");
                //refreshlayout.finishLoadMore(2000/*,false,false*/);//传入false表示加载失败

                mHandler.postDelayed(() -> {
                    //if (random.nextBoolean()) {
                        //如果刷新成功
                        mAdapter.loadMore(initData(10));
                        if (mAdapter.getItemCount() <= 30) {
                            //还有多的数据
                            refreshlayout.finishLoadMore();
                        } else {
                            //没有更多数据（上拉加载功能将显示没有更多数据）
                            Toast.makeText(getApplication(), "数据全部加载完毕", Toast.LENGTH_SHORT).show();
                            refreshlayout.finishLoadMoreWithNoMoreData();//将不会再次触发加载更多事件
                        }
                    //} else {
                    //    //刷新失败
                    //    refreshlayout.finishLoadMore(false);
                    //}
                }, 2000);
            }
        });
    }

    private final Random random = new Random();

    private Collection<Void> initData(int max) {
        int min = Math.min(10, max);
        max = Math.max(0, max);
        if (max > min) {
            return Arrays.asList(new Void[min + random.nextInt(max - min)]);
        } else {
            return Arrays.asList(new Void[min]);
        }
    }
}