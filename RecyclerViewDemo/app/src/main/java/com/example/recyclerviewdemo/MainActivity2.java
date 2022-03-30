package com.example.recyclerviewdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.Arrays;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        List<Fruit> fruitList = Arrays.asList(this.fruits);
        FruitAdapter fruitAdapter = new FruitAdapter(fruitList);

        RecyclerView recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(fruitAdapter);
    }

    private Fruit[] fruits = new Fruit[] {
            new Fruit("Apple01", R.drawable.f01),
            new Fruit("Apple02", R.drawable.f02),
            new Fruit("Apple03", R.drawable.f03),
            new Fruit("Apple04", R.drawable.f04),
            new Fruit("Apple05", R.drawable.f05),
            new Fruit("Apple06", R.drawable.f06),
            new Fruit("Apple07", R.drawable.f07),
            new Fruit("Apple08", R.drawable.f08),
            new Fruit("Apple09", R.drawable.f09),
            new Fruit("Apple10", R.drawable.f10),
            new Fruit("Apple11", R.drawable.f11),
            new Fruit("Apple01", R.drawable.f01),
            new Fruit("Apple02", R.drawable.f02),
            new Fruit("Apple03", R.drawable.f03),
            new Fruit("Apple04", R.drawable.f04),
            new Fruit("Apple05", R.drawable.f05),
            new Fruit("Apple06", R.drawable.f06),
            new Fruit("Apple07", R.drawable.f07),
            new Fruit("Apple08", R.drawable.f08),
            new Fruit("Apple09", R.drawable.f09),
            new Fruit("Apple10", R.drawable.f10),
            new Fruit("Apple11", R.drawable.f11)
    };
}