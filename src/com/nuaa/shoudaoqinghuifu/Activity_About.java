package com.nuaa.shoudaoqinghuifu;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Activity_About extends AppCompatActivity {
    @Bind(R.id.toolbar_about)
    Toolbar tb_about;

    private float startX = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.xml_about);

        ButterKnife.bind(this);

        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initView(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.parseColor("#0288d1"));
        }

        setSupportActionBar(tb_about);
        tb_about.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_About.this.finish();
                overridePendingTransition(R.anim.scale_stay,
                        R.anim.out_left_right);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {

            this.finish();
            overridePendingTransition(R.anim.scale_stay,
                    R.anim.out_left_right);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startX = event.getX();
        } else if (event.getAction() == MotionEvent.ACTION_UP
                && event.getX() - startX > 200) {
            this.finish();
            overridePendingTransition(R.anim.scale_stay,
                    R.anim.out_left_right);
        }

        return super.onTouchEvent(event);
    }
}
