package com.nuaa.shoudaoqinghuifu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Activity_Welcome extends Activity {
    @Bind(R.id.layout_welcome)
    LinearLayout layout;

    @Bind(R.id.imageView_welcome_expand)
    ImageView iv_expand;

    private DBHelper helper = new DBHelper(this, "TempTbl");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xml_welcome);

        ButterKnife.bind(this);

        // 设置启动动画
        final Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        animation.setDuration(2000);
        animation.setInterpolator(new DecelerateInterpolator());
        LayoutAnimationController lac = new LayoutAnimationController(animation);
        lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
        lac.setDelay(0.2f);
        layout.setLayoutAnimation(lac);

        SharedPreferences sp = this.getSharedPreferences("setting",
                Context.MODE_PRIVATE);

        if (sp.getBoolean("needTemp", true)) {
            // 查看是否需要写入模板
            helper.creatTable();
            writeDataBase();

            // 写完就不再写入
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("needTemp", false);
            editor.apply();
        }

        // 获取主题
        ThemeUtil.Theme = sp.getInt("Theme", 0);

        // 检查更新
        new Thread(new Runnable() {
            @Override
            public void run() {
                UmengUpdateAgent.update(Activity_Welcome.this);
            }
        }).start();

        // 延迟启动主界面
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation anim = AnimationUtils.loadAnimation(Activity_Welcome.this, R.anim.scale_expand_cycle);
                anim.setFillAfter(true);

                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
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
                                overridePendingTransition(R.anim.zoom_restore, 0);
                            }
                        } else {
                            Intent intent = new Intent(Activity_Welcome.this, Activity_Msg.class);
                            startActivity(intent);
                            Activity_Welcome.this.finish();
                            overridePendingTransition(R.anim.zoom_restore, 0);
                        }

                        helper.close();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                iv_expand.setVisibility(View.VISIBLE);
                iv_expand.startAnimation(anim);
            }
        }, 3000);
    }

    // 将自带模板写入数据库
    private void writeDataBase() {
        for (int i = 0; i < Value.temps.length; i++) {
            Temp temp = new Temp(Value.titles[i], Value.temps[i]);
            helper.insert("temp", temp);
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
}
