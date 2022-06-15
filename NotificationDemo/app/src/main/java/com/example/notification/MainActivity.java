package com.example.notification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

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
                .setSmallIcon(R.drawable.small)
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
}