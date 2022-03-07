package com.example.recyclerviewdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<MoreTypeBean> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Random random = new Random();
        mData = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            MoreTypeBean bean = new MoreTypeBean();
            bean.pic = R.drawable.ic_launcher_foreground;
            bean.type = random.nextInt(3);
            mData.add(bean);
        }


        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        MoreTypeAdapter adapter = new MoreTypeAdapter(mData);
        recyclerView.setAdapter(adapter);
    }
}