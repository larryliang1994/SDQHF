package com.nuaa.shoudaoqinghuifu;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Activity_Feedback extends AppCompatActivity implements View.OnTouchListener {
    @Bind(R.id.editText_feedback_content)
    EditText edt_content;

    @Bind(R.id.editText_feedback_contact_edit)
    EditText edt_contact;

    @Bind(R.id.toolbar_feedback)
    Toolbar tb_feedback;

    private float startX = 0.0f; // 起始坐标

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.xml_feedback);

        ButterKnife.bind(this);

        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void initView() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.blue_status));
        }

        setSupportActionBar(tb_feedback);
        tb_feedback.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_Feedback.this.finish();
                overridePendingTransition(R.anim.scale_stay,
                        R.anim.out_left_right);
            }
        });
        
        edt_content.setOnTouchListener(this);
        edt_contact.setOnTouchListener(this);
    }

    @SuppressLint("UnlocalizedSms")
    @OnClick({R.id.button_submit})
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_submit:
                if (edt_content.getText().toString().isEmpty()) {
                    Toast.makeText(this, "请填入反馈内容", Toast.LENGTH_SHORT).show();
                } else {
                    SmsManager smsManager = SmsManager.getDefault();

                    smsManager.sendTextMessage("15695293001", null,
                            "设备型号：" + android.os.Build.MODEL
                                    + "  版本号：" + android.os.Build.VERSION.RELEASE
                                    + "  联系方式：" + edt_contact.getText().toString()
                                    + "  反馈内容：" + edt_content.getText().toString(), null, null);

                    Toast.makeText(this, "感谢您的宝贵意见，我们会尽快处理", Toast.LENGTH_SHORT)
                            .show();
                    this.finish();
                    overridePendingTransition(R.anim.scale_stay,
                            R.anim.out_left_right);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    // 按下返回键返回上层
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
        // 按下
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startX = event.getX();
        }
        // 松开且为从左向右滑
        else if (event.getAction() == MotionEvent.ACTION_UP
                && event.getX() - startX > 200) {
            this.finish();
            overridePendingTransition(R.anim.scale_stay,
                    R.anim.out_left_right);
        }

        return super.onTouchEvent(event);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startX = event.getX();
        }
        // 松开且为从左向右滑
        else if (event.getAction() == MotionEvent.ACTION_UP
                && event.getX() - startX > 200) {
            this.finish();
            overridePendingTransition(R.anim.scale_stay,
                    R.anim.out_left_right);
        }

        return super.onTouchEvent(event);
    }
}
