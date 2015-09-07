package com.nuaa.shoudaoqinghuifu;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.drakeet.materialdialog.MaterialDialog;

public class Activity_CheckMemo extends AppCompatActivity implements View.OnTouchListener {
    @Bind(R.id.textView_checkmemo_time_happen)
    TextView tv_time_happen;

    @Bind(R.id.textView_checkmemo_content)
    TextView tv_content;

    @Bind(R.id.textView_checkmemo_address)
    TextView tv_address;

    @Bind(R.id.textView_checkmemo_time_memo)
    TextView tv_time_memo;

    @Bind(R.id.toolbar_checkmemo)
    Toolbar tb_checkmemo;

    @Bind(R.id.scrollView_checkMemo)
    ScrollView sv;

    private float startX = 0.0f; // 起始坐标
    private Memo memo;
    private int position;
    private boolean hasModify = false, isBroadcast = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.xml_checkmemo);

        ButterKnife.bind(this);

        initView();

        position = getIntent().getIntExtra("position", -1);

        // 如果来自于通知栏，则取消该闹钟
        if (getIntent().getBooleanExtra("isBroadcast", false)) {
            isBroadcast = true;

            // 获取AlarmManager系统服务对象
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent myIntent = new Intent(getApplicationContext(),
                    BroadcastReceiver_Alarm.class);
            myIntent.setAction(Value.ACTION_ALARM);
            // 第二个参数为ID，不可重复
            PendingIntent pi = PendingIntent.getBroadcast(
                    getApplicationContext(), ((Memo) getIntent()
                            .getSerializableExtra("memo")).id, myIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            am.cancel(pi);

            // 把修改和删除按钮删掉
            tb_checkmemo.hideOverflowMenu();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_items_check, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void initView() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.purple_status));
        }

        setSupportActionBar(tb_checkmemo);
        tb_checkmemo.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(Activity_CheckMemo.this, Activity_Memo.class);

                // 若来自推送，需启动Memo
                if (isBroadcast) {
                    backIntent.putExtra("isBroadcast", true);
                    startActivity(backIntent);
                } else if (hasModify) {
                    backIntent.putExtra("memo", memo);
                    backIntent.putExtra("hasModify", true);
                    backIntent.putExtra("position", position);
                    Activity_CheckMemo.this.setResult(RESULT_OK, backIntent);
                } else {
                    Activity_CheckMemo.this.setResult(RESULT_CANCELED, backIntent);
                }

                Activity_CheckMemo.this.finish();
                overridePendingTransition(R.anim.scale_stay,
                        R.anim.out_left_right);
            }
        });
        tb_checkmemo.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_modify:
                        Intent intent = new Intent(Activity_CheckMemo.this, Activity_AddMemo.class);
                        intent.putExtra("memo", memo);
                        intent.putExtra("position", position);
                        intent.putExtra("fromCheck", true);
                        startActivityForResult(intent, Value.MODIFY_MEMO);
                        overridePendingTransition(R.anim.in_right_left,
                                R.anim.scale_stay);
                        break;

                    case R.id.item_delete:
                        final MaterialDialog mDialog = new MaterialDialog(Activity_CheckMemo.this)
                                .setCanceledOnTouchOutside(true)
                                .setTitle("删除")
                                .setMessage("真的要删除这条备忘吗？");
                        mDialog.setPositiveButton("真的",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        {
                                            if (position != -1) {

                                                if (Activity_Memo.vector_memos.get(position).needNotify) {
                                                    // 删除该闹钟
                                                    AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                                    Intent myIntent = new Intent(
                                                            getApplicationContext(),
                                                            BroadcastReceiver_Alarm.class);
                                                    myIntent.setAction(Value.ACTION_ALARM);
                                                    PendingIntent pi = PendingIntent
                                                            .getBroadcast(
                                                                    getApplicationContext(),
                                                                    Activity_Memo.vector_memos
                                                                            .get(position).id,
                                                                    myIntent,
                                                                    PendingIntent.FLAG_UPDATE_CURRENT);
                                                    am.cancel(pi);
                                                }

                                                // 可能为常驻提醒，cancel掉
                                                NotificationManager notificationManager = (NotificationManager) Activity_CheckMemo.this
                                                        .getSystemService(Context.NOTIFICATION_SERVICE);
                                                notificationManager.cancel(Activity_Memo.vector_memos
                                                        .get(position).id);

                                                // 从数据库中删除该备忘
                                                DBHelper helper = new DBHelper(Activity_CheckMemo.this, "MemoTbl");
                                                Cursor cursor = helper.query();
                                                cursor.moveToPosition(position);
                                                helper.delete(cursor.getInt(0));
                                                helper.close();

                                                Intent backIntent = new Intent(
                                                        Activity_CheckMemo.this,
                                                        Activity_Memo.class);

                                                Activity_CheckMemo.this.setResult(
                                                        RESULT_OK, backIntent);

                                                backIntent.putExtra("hasDelete", true);
                                                backIntent.putExtra("position", position);

                                                Activity_CheckMemo.this.finish();
                                                overridePendingTransition(R.anim.scale_stay,
                                                        R.anim.out_left_right);

                                                Toast.makeText(Activity_CheckMemo.this, "已删除",
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(Activity_CheckMemo.this,
                                                        "Oops...好像出错了，再来一遍试试？",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
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
                }
                return true;
            }
        });

        memo = (Memo) getIntent().getSerializableExtra("memo");

        tb_checkmemo.setTitle(memo.getContent());

        tv_time_happen.setText(memo.date_happen.toString());
        tv_time_happen.setOnTouchListener(this);

        tv_content.setText(memo.content);
        tv_content.setOnTouchListener(this);

        tv_address.setText(memo.address);
        tv_address.setOnTouchListener(this);

        if (!memo.needNotify) {
            tv_time_memo.setText("无提醒");
        } else {
            tv_time_memo.setText(memo.date_memo.toString());
            tv_time_memo.setOnTouchListener(this);
        }

        sv.setOnTouchListener(this);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Value.MODIFY_MEMO:
                if (resultCode == RESULT_OK) {
                    memo = (Memo) data.getSerializableExtra("memo");

                    tb_checkmemo.setTitle(memo.getContent());
                    tv_time_happen.setText(memo.date_happen.toString());
                    tv_content.setText(memo.content);
                    tv_address.setText(memo.address);
                    if (!memo.needNotify) {
                        tv_time_memo.setText("无提醒");
                    } else {
                        tv_time_memo.setText(memo.date_memo.toString());
                    }

                    hasModify = true;
                }
                break;

            default:
                break;
        }
    }

    // 按下返回键返回上层
    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            Intent backIntent = new Intent(this, Activity_Memo.class);

            // 若来自推送，需启动Memo
            if (isBroadcast) {
                backIntent.putExtra("isBroadcast", true);
                startActivity(backIntent);
            } else if (hasModify) {
                backIntent.putExtra("memo", memo);
                backIntent.putExtra("hasModify", true);
                backIntent.putExtra("position", position);
                this.setResult(RESULT_OK, backIntent);
            } else {
                this.setResult(RESULT_CANCELED, backIntent);
            }

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
            Intent backIntent = new Intent(this, Activity_Memo.class);

            // 若来自推送，需启动Memo
            if (isBroadcast) {
                backIntent.putExtra("isBroadcast", true);
                startActivity(backIntent);
            } else if (hasModify) {
                backIntent.putExtra("memo", memo);
                backIntent.putExtra("hasModify", true);
                backIntent.putExtra("position", position);
                this.setResult(RESULT_OK, backIntent);
            } else {
                this.setResult(RESULT_CANCELED, backIntent);
            }

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
            Intent backIntent = new Intent(this, Activity_Memo.class);

            // 若来自推送，需启动Memo
            if (isBroadcast) {
                backIntent.putExtra("isBroadcast", true);
                startActivity(backIntent);
            } else if (hasModify) {
                backIntent.putExtra("memo", memo);
                backIntent.putExtra("hasModify", true);
                backIntent.putExtra("position", position);
                this.setResult(RESULT_OK, backIntent);
            } else {
                this.setResult(RESULT_CANCELED, backIntent);
            }

            this.finish();
            overridePendingTransition(R.anim.scale_stay,
                    R.anim.out_left_right);
        }

        return super.onTouchEvent(event);
    }
}
