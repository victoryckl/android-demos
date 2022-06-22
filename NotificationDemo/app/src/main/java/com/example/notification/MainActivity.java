package com.example.notification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;

public class MainActivity extends AppCompatActivity {

    private NotificationManager nm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        findViewById(R.id.btn_send).setOnClickListener(v -> {
            sendSimpleNotification(MainActivity.this, nm);
        });

        NotificationChannels.createAllNotificationChannels(this);

        if (!isEnabled()) openNotificationAccess();
    }

    public void sendSimpleNotification(Context context, NotificationManager nm) {
        //创建点击通知时发送的广播
        Intent intent = new Intent(context, NotifyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pi = PendingIntent.getActivity(context,0,intent,0);

        //创建删除通知时发送的广播
        Intent deleteIntent = new Intent(context, NotificationMonitorService.class);
        deleteIntent.setAction(Intent.ACTION_DELETE);
        PendingIntent deletePendingIntent = PendingIntent.getService(context,0,deleteIntent,0);

        //创建通知
        NotificationCompat.Builder nb = new NotificationCompat.Builder(context, NotificationChannels.MEDIA)
                //设置通知左侧的小图标
                .setSmallIcon(R.drawable.shield2)
                //设置通知标题
                .setContentTitle("Simple notification")
                //设置通知内容
                .setContentText("Demo for simple notification!")
                //设置点击通知后自动删除通知
                .setAutoCancel(true)
                //设置显示通知时间
                .setShowWhen(true)
                //设置通知右侧的大图标
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.big))
                //设置删除通知时的响应事件
                .setDeleteIntent(deletePendingIntent)
                //设置点击通知时的响应事件
                .setContentIntent(pi);
        //发送通知
        nm.notify(1, nb.build());
    }


    /*
    将程序编译并安装到手机上，但此时该程序是无法监听到新增通知和删除通知的，
    还需要在"Settings > Security > Notification access"中，勾选NotificationMonitor。
    此时如果系统收到新的通知或者通知被删除就会打印出相应的log了。
    如果手机上没有安装使用NotificationListenerService类的APP，Notification access是不会显示出来的
     */
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";

    /*
    在程序启动时，执行Notification access的检测，查看是否访问Notification的权限。
    如果用户没有Enable Notification access，则弹出提示对话框，点击OK跳转到Notification access设置页面。
     */
    private boolean isEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private void openNotificationAccess() {
        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
    }
}