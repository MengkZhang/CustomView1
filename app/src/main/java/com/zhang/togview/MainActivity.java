package com.zhang.togview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zhang.togview.widget.ToggleView;

public class MainActivity extends AppCompatActivity {

    private ToggleView mToggleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToggleView = (ToggleView) findViewById(R.id.toggleView);

        //需求：给mToggleView设置背景
        mToggleView.setSwitchBackgroundResource(R.mipmap.switch_background);
        //需求：给mToggleView设置滑块背景
        mToggleView.setSlideButtonResource(R.mipmap.slide_button);
        //需求：给mToggleView设置开关状态
        mToggleView.setSwitchState(true);

    }
}
