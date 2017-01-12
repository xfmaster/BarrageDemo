package com.main.xf.barragedemo;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnBarrageInterface {
    private List<String> list = new ArrayList<>();
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BarrageView mBarrageView = (BarrageView) findViewById(R.id.barrage);
        mBarrageView.setBarrageTextColor(Color.GREEN);
        mBarrageView.setIsRepeat(false);
        mBarrageView.setMbarrageInterface(this);
        mBarrageView.startBarrage();
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.add("哈哈哈哈" + index);
                index++;
            }
        });
    }

    @Override
    public String getBarrageText(int position) {
        if (list.size() == 0) return null;
        return list.get(position);
    }

    @Override
    public int getBarrageCount() {
        return list.size();
    }
}
