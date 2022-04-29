package com.example.eventbus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity2";
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        findViewById(R.id.btnBack).setOnClickListener(this);
        textView = findViewById(R.id.textView2);
        Log.i(TAG, "register 1");
        EventBus.getDefault().register(this);
        Log.i(TAG, "register 2");
    }

    private static int count = 100;

    @Override
    public void onClick(View view) {
        EventBus.getDefault().post(new MessageEvent("msg from MainActivity2: " + (count++)));
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onReceiveMessage(MessageEvent msg) {
        textView.setText(msg.text);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}