package com.nuaa.shoudaoqinghuifu;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Activity_AddMemo extends AppCompatActivity implements View.OnTouchListener {
    @Bind(R.id.editText_addmemo_content)
    EditText edt_content;

    @Bind(R.id.editText_addmemo_address)
    EditText edt_address;

    @Bind(R.id.checkBox_addmemo)
    CheckBox cb_needNotify;

    @Bind(R.id.scrollView_addmemo)
    ScrollView sv;

    @Bind(R.id.toolbar_addmemo)
    Toolbar tb_addmemo;

    private static EditText edt_time_happen;
    private static EditText edt_time_memo;

    private float startX = 0.0f;
    private Msg msg;
    private int position;
    private static String date = null;
    private static boolean isHappen = false, isMemo = false;
    private boolean isModify = false, isMsg = false, fromCheck = false;
    private static int year_happen, month_happen, day_happen, hour_happen, minute_happen;
    private static int year_memo, month_memo, day_memo, hour_memo, minute_memo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xml_addmemo);

        ButterKnife.bind(this);

        initView();

        position = getIntent().getIntExtra("position", -1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_items_other, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void initView() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.parseColor("#512da8"));
        }

        setSupportActionBar(tb_addmemo);
        tb_addmemo.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 若来自消息中心，需启动Msg
                if (isMsg) {
                    Intent intent = new Intent(Activity_AddMemo.this, Activity_Msg.class);
                    startActivity(intent);
                }

                // 直接返回
                Activity_AddMemo.this.setResult(RESULT_CANCELED);
                Activity_AddMemo.this.finish();
                overridePendingTransition(R.anim.scale_stay,
                        R.anim.out_left_right);
            }
        });
        tb_addmemo.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_ok:
                        String content = edt_content.getText().toString();
                        String address = edt_address.getText().toString();

                        if (content.isEmpty()) {
                            Toast.makeText(Activity_AddMemo.this, "请输入备忘内容", Toast.LENGTH_SHORT).show();
                        } else if (address.isEmpty()) {
                            Toast.makeText(Activity_AddMemo.this, "请输入地址", Toast.LENGTH_SHORT).show();
                        } else if (year_happen == 0 || hour_happen == 0) {
                            Toast.makeText(Activity_AddMemo.this, "请选择事件时间", Toast.LENGTH_SHORT).show();
                        } else if (cb_needNotify.isChecked()
                                && (year_memo == 0 || hour_memo == 0)) {
                            Toast.makeText(Activity_AddMemo.this, "请选择提醒时间", Toast.LENGTH_SHORT).show();
                        } else {
                            MyDate date_happen = new MyDate(year_happen, month_happen,
                                    day_happen, hour_happen, minute_happen);
                            MyDate date_memo = new MyDate(year_memo, month_memo, day_memo,
                                    hour_memo, minute_memo);
                            Memo memo = new Memo();
                            memo.content = content;
                            memo.address = address;
                            memo.date_happen = date_happen;
                            memo.date_memo = date_memo;
                            memo.needNotify = cb_needNotify.isChecked();
                            memo.id = memo.hashCode();

                            if (isModify && position != -1) {

                                Memo tmp = Activity_Memo.vector_memos.get(position);

                                Calendar current = Calendar.getInstance();
                                Calendar tmpTime = Calendar.getInstance();

                                MyDate date_memo_tmp = tmp.date_memo;
                                tmpTime.set(date_memo_tmp.year, date_memo_tmp.month - 1,
                                        date_memo_tmp.day, date_memo_tmp.hour,
                                        date_memo_tmp.minute);

                                // 需要提醒且未过期
                                if (tmp.needNotify && current.before(tmpTime)) {

                                    AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                    Intent myintent = new Intent(getApplicationContext(),
                                            BroadcastReceiver_Alarm.class);
                                    myintent.setAction(Value.ACTION_ALARM);
                                    // 根据ID找回闹钟并删除
                                    PendingIntent pi = PendingIntent.getBroadcast(
                                            getApplicationContext(), tmp.id, myintent,
                                            PendingIntent.FLAG_UPDATE_CURRENT);

                                    am.cancel(pi);
                                }
                            }

                            if (fromCheck) {
                                // 应对从CheckMemo中修改的情况
                                DBHelper helper = new DBHelper(Activity_AddMemo.this, "MemoTbl");
                                Cursor cursor = helper.query();
                                cursor.moveToPosition(position);
                                helper.update(String.valueOf(cursor.getInt(0)), "memo", memo);
                                helper.close();
                            }

                            // 准备启动闹钟监听
                            if (memo.needNotify) {
                                Activity_Memo.needNotify = true;
                            }

                            Intent i = new Intent(Activity_AddMemo.this, Activity_Memo.class);
                            Activity_Memo.needSort = true;
                            i.putExtra("memo", memo);
                            i.putExtra("position", position);
                            setResult(RESULT_OK, i);

                            if (isMsg) {
                                startActivity(i);
                            }

                            Activity_AddMemo.this.finish();

                            Activity_AddMemo.this.overridePendingTransition(android.R.anim.fade_in, R.anim.scale_stay);

                            if (isModify) {
                                Toast.makeText(Activity_AddMemo.this, "修改完成", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Activity_AddMemo.this, "添加完成", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                }
                return true;
            }
        });

        // 来自修改
        Memo memo = (Memo) getIntent().getSerializableExtra("memo");
        if (memo != null) {
            isModify = true;
        }
        // 来自一键添加
        else {
            msg = (Msg) getIntent().getSerializableExtra("msg");
            if (msg != null) {
                isMsg = true;
            }
        }

        edt_time_happen = (EditText) findViewById(R.id.editText_addmemo_time_happen);
        edt_time_memo = (EditText) findViewById(R.id.editText_addmemo_time_memo);

        fromCheck = getIntent().getBooleanExtra("fromCheck", false);

        // 若来自修改，则初始化内容
        if (isModify) {
            assert memo != null;
            edt_content.setText(memo.content);
            edt_time_happen.setText(memo.date_happen.toString());
            edt_address.setText(memo.address);

            if (memo.needNotify) {
                cb_needNotify.setChecked(true);
                edt_time_memo.setText(memo.date_memo.toString());
            }

            String date_happen_s = memo.date_happen.toSave();
            String date_memo_s = memo.date_memo.toSave();
            String[] date_happen_array = date_happen_s.split("-");
            String[] date_memo_array = date_memo_s.split("-");

            year_happen = Integer.parseInt(date_happen_array[0]);
            month_happen = Integer.parseInt(date_happen_array[1]);
            day_happen = Integer.parseInt(date_happen_array[2]);
            hour_happen = Integer.parseInt(date_happen_array[3]);
            minute_happen = Integer.parseInt(date_happen_array[4]);

            year_memo = Integer.parseInt(date_memo_array[0]);
            month_memo = Integer.parseInt(date_memo_array[1]);
            day_memo = Integer.parseInt(date_memo_array[2]);
            hour_memo = Integer.parseInt(date_memo_array[3]);
            minute_memo = Integer.parseInt(date_memo_array[4]);
        }
        // 来自消息
        else if (isMsg) {
            assert msg != null;
            edt_content.setText(msg.content);
        }

        edt_content.setOnTouchListener(this);
        edt_time_happen.setOnTouchListener(this);
        edt_time_memo.setOnTouchListener(this);
        edt_address.setOnTouchListener(this);
        cb_needNotify.setOnTouchListener(this);
    }

    @OnClick({R.id.editText_addmemo_time_happen, R.id.editText_addmemo_time_memo,
        R.id.editText_addmemo_content, R.id.checkBox_addmemo})
    public void onClick(View v) {
        SetDateDialog sdd;

        switch (v.getId()) {
            case R.id.editText_addmemo_time_happen:
                isHappen = true;

                sdd = new SetDateDialog();
                sdd.show(getFragmentManager(), "DatePicker");
                break;

            case R.id.editText_addmemo_time_memo:
                if(cb_needNotify.isChecked()) {
                    isMemo = true;

                    sdd = new SetDateDialog();
                    sdd.show(getFragmentManager(), "DatePicker");
                }

                break;

            case R.id.editText_addmemo_content:
                //这里必须要给一个延迟，如果不加延迟则没有效果。我现在还没想明白为什么
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        //将ScrollView滚动到底
                        sv.fullScroll(View.FOCUS_DOWN);
                    }
                }, 200);
                break;

            case R.id.checkBox_addmemo:
                if(cb_needNotify.isChecked()){
                    edt_time_memo.setTextColor(Color.parseColor("#212121"));
                    edt_time_memo.setHintTextColor(Color.parseColor("#99000000"));
                } else {
                    edt_time_memo.setTextColor(Color.parseColor("#9fb6b6b6"));
                    edt_time_memo.setHintTextColor(Color.parseColor("#9fb6b6b6"));
                }
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            // 若来自消息中心，需启动Msg
            if (isMsg) {
                Intent intent = new Intent(this, Activity_Msg.class);
                startActivity(intent);
            }

            this.setResult(RESULT_CANCELED);
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
            // 若来自消息中心，需启动Msg
            if (isMsg) {
                Intent intent = new Intent(this, Activity_Msg.class);
                startActivity(intent);
            }

            this.setResult(RESULT_CANCELED);
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
        } else if (event.getAction() == MotionEvent.ACTION_UP
                && event.getX() - startX > 200) {
            // 若来自消息中心，需启动Msg
            if (isMsg) {
                Intent intent = new Intent(this, Activity_Msg.class);
                startActivity(intent);
            }

            this.setResult(RESULT_CANCELED);
            this.finish();
            overridePendingTransition(R.anim.scale_stay,
                    R.anim.out_left_right);
        }

        return super.onTouchEvent(event);
    }

    // 日期选择对话框
    public static class SetDateDialog extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            this.setCancelable(true);

            int year, month, day;

            if ((isHappen && year_happen != 0)
                    || (isMemo && year_happen != 0 && year_memo == 0)) {
                year = year_happen;
                month = month_happen - 1;
                day = day_happen;
            } else if ((isHappen) || (isMemo && year_memo == 0)) {
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            } else {
                year = year_memo;
                month = month_memo - 1;
                day = day_memo;
            }

            return new DatePickerDialog(getActivity(), this,
                    year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            monthOfYear++;
            date = null;
            date = year + "-" + monthOfYear + "-" + dayOfMonth;

            if (isHappen) {
                year_happen = year;
                month_happen = monthOfYear;
                day_happen = dayOfMonth;
            }

            if (isMemo) {
                year_memo = year;
                month_memo = monthOfYear;
                day_memo = dayOfMonth;
            }

            SetTimeDialog std = new SetTimeDialog();
            std.show(getFragmentManager(), "TimePicker");
        }
    }

    // 时间选择对话框
    public static class SetTimeDialog extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            this.setCancelable(false);

            int hour, minute;

            if ((isHappen && hour_happen != 0)
                    || (isMemo && hour_happen != 0 && hour_memo == 0)) {
                hour = hour_happen;
                minute = minute_happen;
            } else if ((isHappen) || (isMemo && hour_memo == 0)) {
                final Calendar c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);
            } else {
                hour = hour_memo;
                minute = minute_memo;
            }

            return new TimePickerDialog(getActivity(), this,
                    hour, minute, DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (minute >= 10) {
                date += " " + hourOfDay + ":" + minute;
            } else {
                date += " " + hourOfDay + ":0" + minute;
            }

            if (isHappen) {
                isHappen = false;

                edt_time_happen.setText(date);
                date = null;

                hour_happen = hourOfDay;
                minute_happen = minute;
            } else if (isMemo) {
                isMemo = false;
                edt_time_memo.setText(date);
                date = null;

                hour_memo = hourOfDay;
                minute_memo = minute;
            }
        }
    }
}