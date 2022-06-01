package com.example.eventbus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusBuilder;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btnJump).setOnClickListener(this);
        textView = findViewById(R.id.textView);
        Log.i(TAG, "register 1");
        EventBus.getDefault().register(this);
        Log.i(TAG, "register 2");
    }

    private int count = 1;

    @Override
    public void onClick(View view) {
        EventBus.getDefault().postSticky(new MessageEvent("msg from MainActivity: " + count++));
        startActivity(new Intent(this, MainActivity2.class));
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveMessage(MessageEvent msg) {
        textView.setText(msg.text);
    }
}