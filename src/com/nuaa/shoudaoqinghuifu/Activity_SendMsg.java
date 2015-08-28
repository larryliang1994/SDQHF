package com.nuaa.shoudaoqinghuifu;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.telephony.SmsManager;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Activity_SendMsg extends Activity {
    @Bind(R.id.imageButton_sendmsg_send)
    ImageButton ibtn_send;

    @Bind(R.id.editText_sendmsg_names)
    EditText et_names;

    @Bind(R.id.editText_sendmsg_content)
    EditText et_content;

    @Bind(R.id.linearLayout_sendmsg_menu)
    LinearLayout ll_menu;

    private static TextView tv_title;

    private SmsManager smsManager;
    private static String date = null;
    private static int mYear, mMonth, mDay, mHour, mMinute;
    private ArrayList<String> phones = new ArrayList<>(); // 联系人号码
    private boolean hasMenu = false, isGroup = false, isTemp = false;
    private static boolean isOnTime = false;
    private float startX = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xml_sendmsg);

        ButterKnife.bind(this);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        ll_menu.setVisibility(View.GONE);
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.textView_sendmsg_title);

        ibtn_send.setBackgroundColor(Color.TRANSPARENT);

        // 获取默认信息管理器对象
        smsManager = SmsManager.getDefault();

        // 来自一键发送
        Group group = (Group) getIntent().getSerializableExtra("group");
        if (group != null) {
            isGroup = true;
            et_names.setText(group.getName());
            phones.clear();
            ArrayList<Member> members = group.members;
            for (int i = 0; i < members.size(); i++) {
                phones.add(members.get(i).phone);
            }
        }

        // 来自模板
        String temp = getIntent().getStringExtra("temp");
        if (temp != null) {
            isTemp = true;
            et_content.setText(temp);
        }
    }

    @OnClick({R.id.imageButton_sendmsg_back, R.id.imageButton_sendmsg_add,
            R.id.imageButton_sendmsg_menu_temp, R.id.imageButton_sendmsg_send,
            R.id.imageButton_sendmsg_menu, R.id.imageButton_sendmsg_menu_settime,
            R.id.imageButton_sendmsg_menu_urgent, R.id.editText_sendmsg_names,
            R.id.editText_sendmsg_content})
    public void onClick(View v) {
        Toast toast;
        switch (v.getId()) {
            case R.id.imageButton_sendmsg_back:
                // 若来自群组，需启动Group，若来自模板，需启动Temp
                if (isGroup) {
                    Intent intent = new Intent(this, Activity_Group.class);
                    startActivity(intent);
                } else if (isTemp) {
                    Intent intent = new Intent(this, Activity_Temp.class);
                    startActivity(intent);
                }

                this.setResult(RESULT_CANCELED);
                this.finish();
                overridePendingTransition(R.anim.scale_stay,
                        R.anim.out_left_right);
                break;

            case R.id.imageButton_sendmsg_menu:
                if (!hasMenu) {
                    ll_menu.setVisibility(View.VISIBLE);
                    hasMenu = true;
                } else {
                    ll_menu.setVisibility(View.GONE);
                    hasMenu = false;
                }

                // 关闭软键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0);
                break;

            case R.id.editText_sendmsg_content:
                ll_menu.setVisibility(View.GONE);
                break;

            case R.id.imageButton_sendmsg_menu_temp:
                AlertDialog.Builder tempBuilder = new AlertDialog.Builder(
                        Activity_SendMsg.this);

                tempBuilder.setTitle("选一个吧");

                // 读取模板
                final DBHelper tHelper = new DBHelper(this, "TempTbl");
                final Cursor cursor = tHelper.query();
                cursor.moveToFirst();

                String[] titles = new String[cursor.getCount()];
                for (int i = 0; i < cursor.getCount(); i++) {
                    String title = cursor.getString(1);
                    titles[i] = title;

                    cursor.moveToNext();
                }

                // 参数（数据列表，默认索引（-1表示不选中），事件处理）
                tempBuilder.setSingleChoiceItems(titles, -1,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                cursor.moveToPosition(which);
                                et_content.setText(cursor.getString(2));

                                tHelper.close();

                                // 选择完就关掉
                                dialog.dismiss();
                                ll_menu.setVisibility(View.GONE);
                            }
                        });

                tempBuilder.show();

                break;

            case R.id.imageButton_sendmsg_menu_settime:
                if (!isOnTime) { // 非定时，则弹出时间设置
                    SetDateDialog sdd = new SetDateDialog();
                    sdd.show(getFragmentManager(), "DatePicker");
                } else { // 否则取消设定好的定时
                    isOnTime = false;
                    tv_title.setText("新建消息");

                    Toast.makeText(this, "已取消定时发送", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.imageButton_sendmsg_menu_urgent:
                toast = Toast.makeText(this, "还没有实现呢。。\\n做好了就告诉你吧", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                break;

            case R.id.editText_sendmsg_names:
            case R.id.imageButton_sendmsg_add:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                String[] name = {"从通讯录中选取", "从我的群组中选取"};

                builder.setItems(name, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int which) {
                        if (which == 0) {
                            Intent openActivity = new Intent(Activity_SendMsg.this,
                                    Activity_Contacts.class);
                            startActivityForResult(openActivity,
                                    Value.CHOOSE_CONTACTS);
                            overridePendingTransition(R.anim.in_right_left,
                                    R.anim.scale_stay);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    Activity_SendMsg.this);

                            builder.setTitle("选一个吧");

                            // 读取群组
                            final DBHelper tHelper = new DBHelper(Activity_SendMsg.this, "GroupTbl");
                            final Cursor cursor = tHelper.query();
                            cursor.moveToFirst();

                            String[] names = new String[cursor.getCount()];
                            for (int i = 0; i < cursor.getCount(); i++) {
                                String name = cursor.getString(1);
                                names[i] = name;

                                cursor.moveToNext();
                            }

                            // 参数（数据列表，默认索引（-1表示不选中），事件处理）
                            builder.setSingleChoiceItems(names, -1,
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            cursor.moveToPosition(which);
                                            et_names.setText(cursor.getString(1));
                                            String member_s = cursor.getString(2);

                                            String[] member_split = member_s.split("#!#");

                                            // 获取选取的组所有成员的手机号
                                            phones.clear();
                                            for (String aMember_split : member_split) {
                                                String[] member_each = aMember_split.split(":");

                                                phones.add(member_each[1]);
                                            }

                                            tHelper.close();

                                            // 选择完就关掉
                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.setCanceledOnTouchOutside(true);
                            dialog.show();
                        }
                    }
                });
                builder.show();

                break;

            case R.id.imageButton_sendmsg_send:
                if (!et_names.getText().toString().isEmpty()
                        && !et_content.getText().toString().isEmpty()) {
                    if (isOnTime) {
                        MyDate mydate = new MyDate(mYear, mMonth, mDay, mHour, mMinute);

                        Msg msg = new Msg();
                        msg.setName(et_names.getText().toString());
                        msg.setContent(et_content.getText().toString());
                        msg.setSendtime(mydate);

                        onTimeSend(msg);

                        // 插入数据库
                        DBHelper helper = new DBHelper(Activity_SendMsg.this, "MsgTbl");
                        helper.creatTable();
                        helper.insert("msg", msg);
                        helper.close();

                        Intent intent = new Intent(this, Activity_Msg.class);
                        intent.putExtra("msg", msg);

                        if (isGroup || isTemp) {
                            startActivity(intent);
                        }

                        Toast.makeText(this, "已经准备好发送了", Toast.LENGTH_SHORT).show();

                        this.setResult(RESULT_OK, intent);
                        this.finish();
                        overridePendingTransition(R.anim.scale_stay,
                                R.anim.out_left_right);
                    } else {
                        // 把短信进行拆分
                        ArrayList<String> contents = smsManager
                                .divideMessage(et_content.getText().toString());

                        // 发送短信
                        int size = contents.size();

                        for (int i = 0; i < phones.size(); i++) {
                            // 参数（要发送的号码，信息中心的号码Null，内容，，)
                            for (int j = 0; j < size; j++) {
                                if (j == size - 1) {
                                    smsManager.sendTextMessage(phones.get(i), null, contents.get(j) + "【来自“收到请回复”客户端】", null, null);
                                } else {
                                    smsManager.sendTextMessage(phones.get(i), null, contents.get(j), null, null);
                                }
                            }
                        }

                        Calendar current = Calendar.getInstance();
                        MyDate mydate = new MyDate(current.get(Calendar.YEAR),
                                current.get(Calendar.MONTH) + 1,
                                current.get(Calendar.DAY_OF_MONTH),
                                current.get(Calendar.HOUR_OF_DAY),
                                current.get(Calendar.MINUTE));

                        Msg msg = new Msg();
                        msg.setName(et_names.getText().toString());
                        msg.setContent(et_content.getText().toString());
                        msg.setSendtime(mydate);

                        // 插入数据库
                        DBHelper helper = new DBHelper(Activity_SendMsg.this, "MsgTbl");
                        helper.creatTable();
                        helper.insert("msg", msg);
                        helper.close();

                        Intent intent = new Intent(this, Activity_Msg.class);
                        intent.putExtra("msg", msg);

                        if (isGroup || isTemp) {
                            startActivity(intent);
                        }

                        Toast.makeText(this, "发送成功", Toast.LENGTH_SHORT).show();

                        this.setResult(RESULT_OK, intent);
                        this.finish();
                        overridePendingTransition(R.anim.scale_stay,
                                R.anim.out_left_right);
                    }
                } else if (et_names.getText().toString().isEmpty()) {
                    Toast.makeText(this, "请输入收件人", Toast.LENGTH_SHORT).show();
                } else if (et_content.getText().toString().isEmpty()) {
                    Toast.makeText(this, "请输入正文", Toast.LENGTH_SHORT).show();
                }

                break;

            default:
                break;
        }
    }

    // 日期选择对话框
    public static class SetDateDialog extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            this.setCancelable(true);

            int year, month, day;

            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this,
                    year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            monthOfYear++;
            date = null;
            date = year + "-" + monthOfYear + "-" + dayOfMonth;

            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;

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

            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);

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

            tv_title.append("  (定时:" + date + ")");
            date = null;

            mHour = hourOfDay;
            mMinute = minute;

            isOnTime = true;

            //Toast.makeText(Activity_SendMsg.this, "已设置为定时发送", Toast.LENGTH_SHORT).show();
        }
    }

    // 定时发送广播
    private void onTimeSend(Msg msg) {
        // 获取AlarmManager系统服务对象
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(getApplicationContext(),
                BroadcastReceiver_SendMsg.class);
        intent.putExtra("msg", msg);
        intent.putExtra("phones", phones);
        intent.setAction(Value.ACTION_SENDMSG);
        // 第二个参数为ID，不可重复
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(),
                am.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 设置闹钟时间
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());

        c.set(Calendar.YEAR, mYear);
        c.set(Calendar.MONTH, mMonth - 1);
        c.set(Calendar.DAY_OF_MONTH, mDay);
        c.set(Calendar.HOUR_OF_DAY, mHour);
        c.set(Calendar.MINUTE, mMinute);

        // 将秒和毫秒设置为0
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Value.CHOOSE_CONTACTS:
                if (resultCode == RESULT_OK) {
                    et_names.setText(data.getStringExtra("names"));
                    // names = data.getStringArrayListExtra("names");
                    phones = data.getStringArrayListExtra("phones");
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
            // 若来自群组，需启动Group，若来自模板，需启动Temp
            if (isGroup) {
                Intent intent = new Intent(this, Activity_Group.class);
                startActivity(intent);
            } else if (isTemp) {
                Intent intent = new Intent(this, Activity_Temp.class);
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
            // 若来自群组，需启动Group，若来自模板，需启动Temp
            if (isGroup) {
                Intent intent = new Intent(this, Activity_Group.class);
                startActivity(intent);
            } else if (isTemp) {
                Intent intent = new Intent(this, Activity_Temp.class);
                startActivity(intent);
            }

            this.setResult(RESULT_CANCELED);
            this.finish();
            overridePendingTransition(R.anim.scale_stay,
                    R.anim.out_left_right);
        }

        return super.onTouchEvent(event);
    }
}
