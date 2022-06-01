package com.example.aservice;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

public class AccessibilitySampleService extends AccessibilityService {
    private static final String TAG = "AccessService";
    private View mForbiddenLayout;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        // 可以来配置服务的一些设置，比如监控哪些app的哪些动作之类的
        Log.i(TAG, "[onServiceConnected]");

        AccessibilityServiceInfo serviceInfo = new AccessibilityServiceInfo();
        serviceInfo.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
                //| AccessibilityEvent.TYPE_WINDOWS_CHANGED
                //| AccessibilityEvent.TYPE_VIEW_CLICKED
                //| AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
                //| AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;

        serviceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        serviceInfo.notificationTimeout = 50;
        serviceInfo.flags = AccessibilityServiceInfo.DEFAULT;
        //serviceInfo.packageNames = new String[]{"cn.wps.note"};
        setServiceInfo(serviceInfo);

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // 设置悬浮窗类型
        mLayoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        } else {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        mLayoutParams.format = PixelFormat.RGBA_8888;

        //设置大小
        mLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;

        //设置位置
        mLayoutParams.x = 0;
        mLayoutParams.y = 0;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // 此方法是在主线程中回调过来的，所以消息是阻塞执行的
        // 当系统检测到与无障碍服务指定的事件过滤参数匹配的 `AccessibilityEvent 时，会回调此方法。
        // 例如，当用户点击某个按钮或将焦点置于应用中的某个界面控件上，而无障碍服务正在为其提供反馈时。
        // 出现这种情况时，系统会调用此方法，并传递关联的 AccessibilityEvent，服务随后可以对其进行解读并用其向用户提供反馈。
        // 此方法可能会在服务的整个生命周期内被调用多次。

        CharSequence pkg = event.getPackageName();
        Log.i(TAG, "[onAccessibilityEvent] " + event);

        if (TextUtils.isEmpty(pkg) || TextUtils.equals("com.android.systemui", pkg)) return; //可能是下拉通知栏，不处理

        if (TextUtils.equals(pkg, "cn.wps.note") || TextUtils.equals(pkg, "com.zhihu.android")) {
            showFloatingWindow();
        } else {
            if (!isFloatWindow(event)) hideFloatingWindow();
        }
    }

    @Override
    public void onInterrupt() {
        //当系统要中断服务正在提供的反馈（通常是为了响应将焦点移到其他控件等用户操作）时，会调用此方法。
        // 此方法可能会在服务的整个生命周期内被调用多次。
        Log.i(TAG, "[onInterrupt]");
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        Log.i(TAG, "[onKeyEvent] " + event);
        return super.onKeyEvent(event);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "[onUnbind]");
        //当系统将要关闭无障碍服务时，会调用此方法。使用此方法可执行任何一次性关闭流程，
        // 包括取消分配用户反馈系统服务，如音频管理器或设备振动器。
        return super.onUnbind(intent);
    }

    private void showFloatingWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                if (mForbiddenLayout == null) {
                    mForbiddenLayout = LayoutInflater.from(getApplicationContext()).inflate(R.layout.forbidden_layout, null);
                    mForbiddenLayout.findViewById(R.id.btn_exit).setOnClickListener(v -> {
                        Log.i(TAG, "button exit");
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
                        hideFloatingWindow();
                    });
                }
                // 将悬浮窗控件添加到WindowManager
                if (mForbiddenLayout.getParent() == null) {
                    mWindowManager.addView(mForbiddenLayout, mLayoutParams);
                }
            }
        }
    }

    private void hideFloatingWindow() {
        if (mForbiddenLayout != null && mForbiddenLayout.getParent() != null) {
            mWindowManager.removeView(mForbiddenLayout);
        }
    }

    private boolean isFloatWindow(AccessibilityEvent event) {
        if (TextUtils.equals(event.getPackageName(), getPackageName())) {
            if (mForbiddenLayout != null)
                return mForbiddenLayout.getClass().getName().equals(event.getClassName());
        }
        return false;
    }
}
