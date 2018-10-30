package com.example.note.justdo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.hanks.htextview.scale.ScaleTextView;

public class WelcomeActivity extends Activity {
    private ScaleTextView textView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animation_start);
        textView = findViewById(R.id.less);
        SharedPreferences sp = getSharedPreferences("firstIn",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        final boolean isFirst = sp.getBoolean("isFirst",true);
        //判断得到的是否为第一次 若 是 第一次  则跳到引导页面
       // textView1 = (ScaleTextView) findViewById(R.id.just);
        // textView2 = (ScaleTextView) findViewById(R.id.textview2);
        // textView3 = (ScaleTextView) findViewById(R.id.textview3);

        //textView.setOnClickListener(new ClickListener());
       // textView1.setOnClickListener(new ClickListener());
        //textView2.setOnClickListener(new ClickListener());
        // textView3.setOnClickListener(new ClickListener());
        new Handler().postDelayed(new Runnable() {

            //延迟中执行的操作放在这里,跳转之后及时销毁
            @Override
            public void run() {
                textView.animateText("less is more");
            }
        },200 );
        new Handler().postDelayed(new Runnable() {

            //延迟中执行的操作放在这里,跳转之后及时销毁
            @Override
            public void run() {
                textView.animateText("just do");
            }
        },2000 );
        new Handler().postDelayed(new Runnable() {

            //延迟中执行的操作放在这里,跳转之后及时销毁
            @Override
            public void run() {
                if(isFirst){

                }
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        },4000 );


    }
}