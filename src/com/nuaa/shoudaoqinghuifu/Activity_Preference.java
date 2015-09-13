package com.nuaa.shoudaoqinghuifu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
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

    @Bind(R.id.navigationView_preference)
    NavigationView nv_preference;

    @Bind(R.id.drawerLayout_preference)
    DrawerLayout dw_preference;

    @Bind(R.id.toolbar_preference)
    Toolbar tb_preference;

    @Bind(R.id.imageView_preference_expand)
    ImageView iv_expand;

    private SharedPreferences sp;
    private DBHelper helper = new DBHelper(Activity_Preference.this, "TempTbl");
    private long exitTime = 0;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        ThemeUtil.onSetTheme(this, "other");

        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.xml_preference_body);

        ButterKnife.bind(this);

        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_items_main, menu);
        return true;
    }

    @SuppressWarnings("deprecation")
    private void initView() {
        tb_preference.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dw_preference.openDrawer(GravityCompat.START);
            }
        });
        tb_preference.setTitleTextColor(Color.parseColor("#ffffff"));

        nv_preference.setItemTextColor(ColorStateList.valueOf(Color.parseColor("#000000")));
        nv_preference.setItemIconTintList(null);
        nv_preference.getMenu().getItem(4).setChecked(true);
        nv_preference.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Intent intent = null;
                dw_preference.closeDrawer(GravityCompat.START);
                switch (menuItem.getItemId()) {
                    case R.id.navItem_msg:
                        intent = new Intent(Activity_Preference.this, Activity_Msg.class);
                        Activity_Preference.this.finish();
                        break;

                    case R.id.navItem_group:
                        intent = new Intent(Activity_Preference.this, Activity_Group.class);
                        Activity_Preference.this.finish();
                        break;

                    case R.id.navItem_memo:
                        intent = new Intent(Activity_Preference.this, Activity_Memo.class);
                        Activity_Preference.this.finish();
                        break;

                    case R.id.navItem_temp:
                        intent = new Intent(Activity_Preference.this, Activity_Temp.class);
                        Activity_Preference.this.finish();
                        break;

                    case R.id.navItem_setting:
                        intent = null;
                        break;
                }

                if (intent != null) {
                    startActivity(intent);
                    overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                }

                return true;
            }
        });

        GradientDrawable bgShape_temp = (GradientDrawable) iv_expand.getBackground();
        bgShape_temp.setColor(Color.parseColor("#ffffff"));

        // 设置主题处字样
        String themeName = "";
        switch (ThemeUtil.Theme) {
            case ThemeUtil.THEME_COLORFUL_LIGHT:
                themeName = "五彩";
                break;

            case ThemeUtil.THEME_COLORFUL_DEEP:
                themeName = "绚烂";
                break;

            case ThemeUtil.THEME_BLUE:
                themeName = "蔚蓝";
                break;

            case ThemeUtil.THEME_DARK:
                themeName = "酷黑";
                break;
        }
        findPreference("theme").setSummary("当前主题：" + themeName);
    }

    @Override
    public void setContentView(int layoutResID) {
        ViewGroup contentView = (ViewGroup) LayoutInflater.from(this).inflate(
                R.layout.xml_preference, new LinearLayout(this), false);

        ViewGroup contentWrapper = (ViewGroup) contentView.findViewById(R.id.content_wrapper);
        LayoutInflater.from(this).inflate(layoutResID, contentWrapper, true);

        getWindow().setContentView(contentView);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
                                         android.preference.Preference preference) {
        Intent intent;
        switch (preference.getKey()) {

            case "theme":
                String[] titles = {"五彩", "绚烂", "蔚蓝", "酷黑"};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setItems(titles, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                ThemeUtil.Theme = ThemeUtil.THEME_COLORFUL_LIGHT;
                                break;

                            case 1:
                                ThemeUtil.Theme = ThemeUtil.THEME_COLORFUL_DEEP;
                                break;

                            case 2:
                                ThemeUtil.Theme = ThemeUtil.THEME_BLUE;
                                break;

                            case 3:
                                ThemeUtil.Theme = ThemeUtil.THEME_DARK;
                                break;
                        }

                        // 写入文件
                        SharedPreferences sharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);
                        Editor editor = sharedPreferences.edit();
                        editor.putInt("Theme", ThemeUtil.Theme);
                        editor.apply();

                        Animation anim = AnimationUtils.loadAnimation(Activity_Preference.this, R.anim.scale_expand_cycle);
                        anim.setFillAfter(true);

                        anim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                // 刷新页面
                                Intent myIntent = new Intent(Activity_Preference.this, Activity_Preference.class);
                                startActivity(myIntent);
                                Activity_Preference.this.finish();
                                overridePendingTransition(R.anim.zoom_restore, 0);

                                Toast.makeText(Activity_Preference.this, "切换成功", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                        tb_preference.setVisibility(View.GONE);
                        iv_expand.setVisibility(View.VISIBLE);
                        iv_expand.startAnimation(anim);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();

                break;

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
                                editor.putInt("Theme", ThemeUtil.THEME_COLORFUL_LIGHT);
                                editor.apply();

                                // 刷新页面
                                ThemeUtil.Theme = ThemeUtil.THEME_COLORFUL_LIGHT;
                                Animation anim = AnimationUtils.loadAnimation(Activity_Preference.this, R.anim.scale_expand_cycle);
                                anim.setFillAfter(true);

                                anim.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        // 刷新页面
                                        Intent myIntent = new Intent(Activity_Preference.this, Activity_Preference.class);
                                        startActivity(myIntent);
                                        Activity_Preference.this.finish();
                                        overridePendingTransition(R.anim.zoom_restore, 0);
                                        Toast.makeText(Activity_Preference.this, "恢复成功", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });

                                tb_preference.setVisibility(View.GONE);
                                iv_expand.setVisibility(View.VISIBLE);
                                iv_expand.startAnimation(anim);

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
                FeedbackAgent agent = new FeedbackAgent(this);
                agent.startFeedbackActivity();

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
                    UmengUpdateAgent.setUpdateOnlyWifi(false);
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
                                    Toast.makeText(Activity_Preference.this, "Oops...网络出错了哦(Wifi下更新可能会出现此问题)", Toast.LENGTH_SHORT).show();
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
            // 收起抽屉
            if (dw_preference.isDrawerOpen(GravityCompat.START)) {
                dw_preference.closeDrawer(GravityCompat.START);
            } else if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
