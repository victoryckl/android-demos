package com.example.screenshot;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ScreenShotTestActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "ScreenShotTestActivity";

    private TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenshot_test);

        findViewById(R.id.button).setOnClickListener(this);
        findViewById(R.id.button_delay).setOnClickListener(this);
        textView2 = findViewById(R.id.textView2);

        WebView webView = findViewById(R.id.webview);

        String html = "<html>\n" +
                "\n" +
                "<body style=\"background-color:yellow\">\n" +
                "<h2 style=\"background-color:red\">Here is a WebView</h2>\n" +
                "<p style=\"background-color:green\">This is a paragraph.</p>\n" +
                "</body>\n" +
                "\n" +
                "</html>";
        webView.loadData(html, "text/html; charset=UTF-8", null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                takeShot();
                break;
            case R.id.button_delay:
                mHandler.removeMessages(1);
                mHandler.sendEmptyMessageDelayed(1, 5*1000);
                break;
            default:
                break;
        }
    }

    private void takeShot() {
        String path = Environment.getExternalStorageDirectory().getPath()
                +"/Screenshots/shot_"+System.currentTimeMillis()+".png";
        path = ScreenShotUtil.getInstance().takeScreenshot(ScreenShotTestActivity.this, path);
        textView2.setText(path==null? "failed" : path);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    takeShot();
                    break;
            }
        }
    };

    //hook webview，解决system.uid程序无法使用WebView的问题
    //Caused by: java.lang.UnsupportedOperationException: For security reasons, WebView is not allowed in privileged processes
    //https://blog.csdn.net/qq_33890656/article/details/69656285
    private void hookWebView() {
        if (android.os.Process.myUid() != Process.SYSTEM_UID) {
            return;// 如果是非系统进程则按正常程序走
        }
        int sdkInt = Build.VERSION.SDK_INT;
        try {
            Class<?> factoryClass = Class.forName("android.webkit.WebViewFactory");
            Field field = factoryClass.getDeclaredField("sProviderInstance");
            field.setAccessible(true);
            Object sProviderInstance = field.get(null);
            if (sProviderInstance != null) {
                Log.e(TAG, "sProviderInstance isn't null");
                return;
            }
            Method getProviderClassMethod;
            if (sdkInt > 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getProviderClass");
            } else if (sdkInt == 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getFactoryClass");
            } else {
                Log.e(TAG, "Don't need to Hook WebView");
                return;
            }
            getProviderClassMethod.setAccessible(true);
            Class<?> factoryProviderClass = (Class<?>) getProviderClassMethod.invoke(factoryClass);
            Class<?> delegateClass = Class.forName("android.webkit.WebViewDelegate");
            Constructor<?> delegateConstructor = delegateClass.getDeclaredConstructor();
            delegateConstructor.setAccessible(true);
            if (sdkInt < 26) {//低于Android O版本
                Constructor<?> providerConstructor = factoryProviderClass.getConstructor(delegateClass);
                if (providerConstructor != null) {
                    providerConstructor.setAccessible(true);
                    sProviderInstance = providerConstructor.newInstance(delegateConstructor.newInstance());
                }
            } else {
                Field chromiumMethodName = factoryClass.getDeclaredField("CHROMIUM_WEBVIEW_FACTORY_METHOD");
                chromiumMethodName.setAccessible(true);
                String chromiumMethodNameStr = (String) chromiumMethodName.get(null);
                if (chromiumMethodNameStr == null) {
                    chromiumMethodNameStr = "create";
                }
                Method staticFactory = factoryProviderClass.getMethod(chromiumMethodNameStr, delegateClass);
                if (staticFactory != null) {
                    sProviderInstance = staticFactory.invoke(null, delegateConstructor.newInstance());
                }
            }
            if (sProviderInstance != null) {
                field.set("sProviderInstance", sProviderInstance);
                Log.e(TAG, "Hook success!");
            } else {
                Log.e(TAG, "Hook failed!");
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
