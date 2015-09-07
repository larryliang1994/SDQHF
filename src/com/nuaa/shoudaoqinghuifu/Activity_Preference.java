package com.nuaa.shoudaoqinghuifu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.drakeet.materialdialog.MaterialDialog;

public class Activity_Preference extends PreferenceActivity {
    @Bind(R.id.progressBar_preference)
    ProgressBar pb;

    private float startX = 0.0f;
    private SharedPreferences sp;
    private boolean isTemp = false;
    private DBHelper helper = new DBHelper(Activity_Preference.this, "TempTbl");

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.xml_preference);

        ButterKnife.bind(this);

        isTemp = getIntent().getBooleanExtra("isTemp", false);
    }

    @Override
    public void setContentView(int layoutResID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.blue_status));
        }

        ViewGroup contentView = (ViewGroup) LayoutInflater.from(this).inflate(
                R.layout.actionbar_preference, new LinearLayout(this), false);

        ViewGroup contentWrapper = (ViewGroup) contentView.findViewById(R.id.content_wrapper);
        LayoutInflater.from(this).inflate(layoutResID, contentWrapper, true);

        Toolbar tb_preference = (Toolbar) contentView.findViewById(R.id.toolbar_preference);
        tb_preference.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTemp) {
                    startActivity(new Intent(Activity_Preference.this, Activity_Temp.class));
                }

                Activity_Preference.this.finish();
                overridePendingTransition(R.anim.scale_stay,
                        R.anim.out_left_right);
            }
        });

        getWindow().setContentView(contentView);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
                                         android.preference.Preference preference) {
        Intent intent;
        switch (preference.getKey()) {

            case "reset":
                final MaterialDialog mDialog = new MaterialDialog(Activity_Preference.this)
                        .setCanceledOnTouchOutside(true)
                        .setMessage("真的要恢复默认设置吗？");
                mDialog.setPositiveButton("真的",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sp = Activity_Preference.this.getSharedPreferences(
                                        "setting", Context.MODE_PRIVATE);
                                Editor editor = sp.edit();
                                editor.clear();
                                editor.apply();

                                // 写入自带模板
                                helper.clear();
                                helper.creatTable();
                                writeDataBase();

                                // 写完就不再写入
                                editor.putBoolean("needTemp", false);
                                editor.apply();

                                Toast.makeText(Activity_Preference.this, "恢复成功",
                                        Toast.LENGTH_SHORT).show();
                                mDialog.dismiss();
                            }
                        })
                        .setNegativeButton("假的", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                            }
                        });
                mDialog.show();
                break;

            case "feedBack":
                intent = new Intent(this, Activity_Feedback.class);

                startActivity(intent);

                overridePendingTransition(R.anim.in_right_left,
                        R.anim.scale_stay);

                break;

            case "about":
                intent = new Intent(this, Activity_About.class);

                startActivity(intent);

                overridePendingTransition(R.anim.in_right_left,
                        R.anim.scale_stay);
                break;

            case "share":
                // 进度条出现，3秒后消失
                pb.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pb.setVisibility(View.GONE);
                    }
                }, 3000);


                intent = new Intent(Intent.ACTION_SEND);

                intent.setType("text/plain"); // 纯文本
                intent.putExtra(Intent.EXTRA_TEXT, Value.SHAER_TEXT);
                intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

//                intent.setType("image/*");
//                File file = new File(Value.appHome + File.separator
//                        + "icon_outside.png");
//                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
//
//                file = new File(Value.appHome + File.separator
//                        + "main.png");
//                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
//
//                file = new File(Value.appHome + File.separator
//                        + "memo.png");
//                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
//
//                file = new File(Value.appHome + File.separator
//                        + "msg.png");
//                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
//
//                file = new File(Value.appHome + File.separator
//                        + "temp.png");
//                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

                startActivity(Intent.createChooser(intent, getTitle()));

                break;
            case "update":

                // 获取网络连接管理器对象（系统服务对象）
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                // 获取网络状态
                NetworkInfo info = cm.getActiveNetworkInfo();

                if (info != null && info.isAvailable()) {
                    // 若网络可用，检查更新
                    UmengUpdateAgent.setUpdateAutoPopup(false);
                    UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
                        @Override
                        public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                            switch (updateStatus) {
                                case UpdateStatus.NoneWifi:
                                case UpdateStatus.Yes: // has update
                                    UmengUpdateAgent.showUpdateDialog(Activity_Preference.this, updateInfo);
                                    break;

                                case UpdateStatus.No: // has no update
                                    Toast.makeText(Activity_Preference.this, "已经是最新版本了", Toast.LENGTH_SHORT).show();
                                    break;

                                case UpdateStatus.Timeout: // time out
                                    Toast.makeText(Activity_Preference.this, "Oops...网络出错了哦", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    });
                    UmengUpdateAgent.forceUpdate(this);
                } else {
                    Toast.makeText(this, "Oops...网络出错了哦", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
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

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (isTemp) {
                startActivity(new Intent(this, Activity_Temp.class));
            }

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
            if (isTemp) {
                startActivity(new Intent(this, Activity_Temp.class));
            }

            this.finish();
            overridePendingTransition(R.anim.scale_stay,
                    R.anim.out_left_right);
        }

        return super.onTouchEvent(event);
    }
}
