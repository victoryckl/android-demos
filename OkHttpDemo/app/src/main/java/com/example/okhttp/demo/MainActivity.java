package com.example.okhttp.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.util.FileUtils;
import com.example.util.StackTrace;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.BufferedSink;

// https://www.jianshu.com/p/da4a806e599b

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String URL = "https://www.baidu.com";
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
            case R.id.btn_post_string: postString(); break;
            case R.id.btn_post_buffer: postBuffer(); break;
            case R.id.btn_post_file: postFile(); break;
            case R.id.btn_interceptor: interceptor(); break;
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
        /*
        通过 Call#execute() 来提交请求，注意这种方式会阻塞调用线程，
        所以在Android中应放在子线程中执行，否则有可能引起ANR异常，
        Android3.0 以后已经不允许在主线程访问网络。
         */
        new Thread(() -> {
                try {
                    Response response = call.execute();
                    Log.i(TAG, "[run] response="+response);
                } catch (IOException e) { e.printStackTrace(); }
            }).start();
        Log.i(TAG, "testGetSync 2");
    }

    private void postString() {
        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
        String requestBody = "hello, i am test with okhttp";
        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(RequestBody.create(mediaType, requestBody))
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "[onFailure] "+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG, "[onResponse] "+response);
            }
        });
    }

    private void postBuffer() {
        RequestBody requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.parse("text/x-markdown; charset=utf-8");
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.writeUtf8("i am from BufferedSink");
            }
        };

        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(requestBody)
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "[onFailure] "+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG, "[onResponse]"+response);
            }
        });
    }

    private void postFile() {
        File file = new File(getFilesDir(), "test.txt");
        FileUtils.writeFile(file, ("i am from file:"+file.getAbsolutePath()).getBytes());

        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(RequestBody.create(mediaType, file))
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "[onFailure] "+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG, "[onResponse] " + response);
            }
        });
    }


    private void interceptor() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LogInterceptor("1"))
                .addInterceptor(new LogInterceptor("2"))
                .build();

        Request request = new Request.Builder()
                .url("http://www.publicobject.com/helloworld.txt")
                .header("User-Agent", "OkHttp Example")
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "[onFailure] "+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG, "[onResponse] " + response);
            }
        });
    }


    private class LogInterceptor implements Interceptor {
        private static final String TAG = "LogInterceptor";

        private String tag;

        private LogInterceptor(String name) {
            tag = TAG+"."+name;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            long startTime = System.currentTimeMillis();
            Log.i(tag, String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));

            Response response = chain.proceed(request);

            long endTime = System.currentTimeMillis();
            Log.d(tag, String.format("Received response for %s in %dms%n%s",
                    response.request().url(), (endTime - startTime), response.headers()));

            StackTrace.printStackTrace(tag);

            return response;
        }
    }



    private interface Inter1 {
        void method1();
    }

    private interface Inter2 {
        void method1();
        void method2();
    }

    private class Clazz1 implements Inter1,Inter2 {
        @Override
        public void method1() {

        }

        @Override
        public void method2() {

        }
    }

    //Java中的接口是支持多继承的
    //接口之间是继承关系，而非实现关系
    private interface Inter3 extends Inter1, Inter2 {
        void method1();
        void method3();
    }

    private class Clazz2 implements Inter3, Inter1 {
        @Override
        public void method1() {

        }

        @Override
        public void method2() {

        }

        @Override
        public void method3() {

        }
    }

}
