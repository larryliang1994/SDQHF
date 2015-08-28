package com.nuaa.shoudaoqinghuifu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Activity_Welcome extends Activity {
    @Bind(R.id.layout_welcome)
    LinearLayout layout;

    public static String picturePath = null;
    private DBHelper helper = new DBHelper(this, "TempTbl");

    //TODO 语音备忘录

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xml_welcome);

        ButterKnife.bind(this);

        // 设置启动动画
        Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        animation.setDuration(1000);
        animation.setInterpolator(new DecelerateInterpolator());
        LayoutAnimationController lac = new LayoutAnimationController(animation);
        lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
        lac.setDelay(0.2f);
        layout.setLayoutAnimation(lac);

        // 判断是否有自定义壁纸
        SharedPreferences sp = this.getSharedPreferences("setting",
                Context.MODE_PRIVATE);
        picturePath = sp.getString("picturePath", null);

        if (sp.getBoolean("needTemp", true)) {
            // 查看是否需要写入模板
            helper.creatTable();
            writeDataBase();

            // 写完就不再写入
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("needTemp", false);
            editor.apply();
        }

        // 延迟启动主界面
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = getSharedPreferences("setting", MODE_PRIVATE);
                String launch = sp.getString("launch", null);
                if (launch != null) {
                    Intent intent = null;
                    switch (launch) {
                        case "msg":
                            intent = new Intent(Activity_Welcome.this, Activity_Msg.class);
                            break;

                        case "group":
                            intent = new Intent(Activity_Welcome.this, Activity_Group.class);
                            break;

                        case "memo":
                            intent = new Intent(Activity_Welcome.this, Activity_Memo.class);
                            break;

                        case "temp":
                            intent = new Intent(Activity_Welcome.this, Activity_Temp.class);
                            break;
                    }
                    if (intent != null) {
                        startActivity(intent);
                        Activity_Welcome.this.finish();
                        overridePendingTransition(R.anim.in_right_left, R.anim.scale_stay);
                    }
                } else {
                    Intent intent = new Intent(Activity_Welcome.this, Activity_Msg.class);
                    startActivity(intent);
                    Activity_Welcome.this.finish();
                    overridePendingTransition(R.anim.in_right_left, R.anim.scale_stay);
                }

                helper.close();
            }
        }, 2000);
    }

    // 将自带模板写入数据库
    private void writeDataBase() {
        for (int i = 0; i < Value.temps.length; i++) {
            Temp temp = new Temp(Value.titles[i], Value.temps[i]);
            helper.insert("temp", temp);
        }
    }
}
