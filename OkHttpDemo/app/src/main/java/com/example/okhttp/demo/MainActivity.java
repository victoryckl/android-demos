package com.example.okhttp.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String URL = "http://www.baidu.com";
    private OkHttpClient mOkHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_async: testGetAsync(); break;
            case R.id.btn_get_sync: testGetSync(); break;
        }
    }

    private void testGetAsync() {
        Request request = new Request.Builder()
                .url(URL)
                .get() //默认就是GET请求，可以不写
                .build();
        Call call = mOkHttpClient.newCall(request);
        Log.i(TAG, "testGetAsync 1");
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "[onFailure] "+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG, "[onResponse] "+response);
            }
        });
        Log.i(TAG, "testGetAsync 2");
    }

    private void testGetSync() {
        Request request = new Request.Builder()
                .url(URL)
                .build();
        final Call call = mOkHttpClient.newCall(request);
        Log.i(TAG, "testGetSync 1");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = call.execute();
                    Log.i(TAG, "[run] response="+response);
                } catch (IOException e) { e.printStackTrace(); }
            }
        }).start();
        Log.i(TAG, "testGetSync 2");
    }
}
