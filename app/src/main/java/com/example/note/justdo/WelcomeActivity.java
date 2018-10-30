package com.example.note.justdo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.note.justdo.Amap.NewMap;
import com.hanks.htextview.base.AnimationListener;
import com.hanks.htextview.base.HTextView;
import com.hanks.htextview.fade.FadeTextView;
import com.hanks.htextview.scale.ScaleTextView;

public class WelcomeActivity extends Activity {
    private ScaleTextView textView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animation_start);
        textView = findViewById(R.id.less);
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
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        },4000 );


    }
}