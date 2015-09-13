package com.nuaa.shoudaoqinghuifu;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Activity_SendMsg extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    @Bind(R.id.floatingActionButton_sendmsg_send)
    RippleView fab_send;

    @Bind(R.id.editText_sendmsg_names)
    EditText et_names;

    @Bind(R.id.editText_sendmsg_content)
    EditText et_content;

    @Bind(R.id.layout_sendmsg)
    RelativeLayout layout_sendmsg;

    @Bind(R.id.toolbar_sendmsg)
    Toolbar tb_sendmsg;

    @Bind(R.id.scrollView_sendmsg)
    ScrollView sv_sendmsg;

    @Bind(R.id.imageView_sendmsg_pen)
    ImageView iv_pen;

    @Bind(R.id.imageButton_sendmsg_temp)
    ImageButton ibtn_temp;

    @Bind(R.id.imageButton_sendmsg_settime)
    ImageButton ibtn_settime;

    private static TextView tv_receiver;
    private static Switch sw_settime, sw_temp;
    private SmsManager smsManager;
    private static String date = null;
    private static int mYear, mMonth, mDay, mHour, mMinute;
    private ArrayList<String> phones = new ArrayList<>(); // 联系人号码
    private ArrayList<String> g_names = new ArrayList<>();
    private boolean isGroup = false, isTemp = false, hasTemp = false, fromGroup = false;
    private static boolean isOnTime = false;
    private float scale; // 用于dp转px
    private String[] names = null;
    private float startX = 0.0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeUtil.onSetTheme(this, "msg");

        setContentView(R.layout.xml_sendmsg);

        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fab_send.setElevation(4.0f);
        }

        setSupportActionBar(tb_sendmsg);
        tb_sendmsg.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 若来自群组，需启动Group，若来自模板，需启动Temp
                if (isGroup) {
                    Intent intent = new Intent(Activity_SendMsg.this, Activity_Group.class);
                    startActivity(intent);
                } else if (isTemp) {
                    Intent intent = new Intent(Activity_SendMsg.this, Activity_Temp.class);
                    startActivity(intent);
                }

                Activity_SendMsg.this.setResult(RESULT_CANCELED);
                Activity_SendMsg.this.finish();
                overridePendingTransition(R.anim.scale_stay,
                        R.anim.out_left_right);
            }
        });

        tv_receiver = (TextView) findViewById(R.id.textView_sendmsg_receiver);
        sw_settime = (Switch) findViewById(R.id.switch_sendmsg_menu_settime);
        sw_temp = (Switch) findViewById(R.id.switch_sendmsg_menu_temp);

        // 设置左下角图标颜色
        GradientDrawable bgShape_temp = (GradientDrawable) ibtn_temp.getBackground();
        bgShape_temp.setColor(getResources().getColor(R.color.red));
        GradientDrawable bgShape_settime = (GradientDrawable) ibtn_settime.getBackground();
        bgShape_settime.setColor(getResources().getColor(R.color.red));
        GradientDrawable bgShape_fab = (GradientDrawable) fab_send.getBackground();
        bgShape_fab.setColor(getResources().getColor(R.color.red));

        // 获取默认信息管理器对象
        smsManager = SmsManager.getDefault();

        // 来自一键发送
        Group group = (Group) getIntent().getSerializableExtra("group");
        if (group != null) {
            isGroup = true;
            fromGroup = true;
            et_names.setText(group.getName());
            phones.clear();
            ArrayList<Member> members = group.members;
            for (int i = 0; i < members.size(); i++) {
                phones.add(members.get(i).phone);
                g_names.add(members.get(i).name);
            }
        }

        // 来自模板
        String temp = getIntent().getStringExtra("temp");
        if (temp != null) {
            isTemp = true;
            hasTemp = true;
            sw_temp.setChecked(true);
            et_content.setText(temp);
        }

        scale = getApplicationContext().getResources().getDisplayMetrics().density;

        // 夺取滑动事件
        et_content.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }

        });

        SoftKeyboardStateHelper softKeyboardStateHelper = new SoftKeyboardStateHelper(layout_sendmsg);
        softKeyboardStateHelper.addSoftKeyboardStateListener(new SoftKeyboardStateHelper.SoftKeyboardStateListener() {
            @Override
            public void onSoftKeyboardOpened(int keyboardHeightInPx) {
                // 设置笔的位置
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        (int) (30 * scale + 0.5f),
                        (int) (25 * scale + 0.5f));
                lp.setMargins(15, 0, 0, 0);
                iv_pen.setLayoutParams(lp);

                // 至少占5行
                et_content.setMinLines(5);

                setFabAnimation();
            }

            @Override
            public void onSoftKeyboardClosed() {
                setFabAnimation();
            }
        });

        fab_send.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (!et_names.getText().toString().isEmpty()
                        && !et_content.getText().toString().isEmpty()) {
                    if (isOnTime) {
                        MyDate mydate = new MyDate(mYear, mMonth, mDay, mHour, mMinute);

                        Msg msg = new Msg();
                        if (!fromGroup) {
                            String new_names = "";

                            for (String name : names) {
                                new_names += "0" + name + ",";
                            }

                            msg.setName(new_names);
                        } else {
                            String new_names = "";

                            new_names += et_names.getText().toString() + "@@@";

                            for (int i = 0; i < g_names.size(); i++) {
                                new_names += "0" + g_names.get(i) + ",";
                            }

                            msg.setName(new_names);
                        }
                        msg.setContent(et_content.getText().toString());
                        msg.setSendtime(mydate);

                        onTimeSend(msg);

                        // 插入数据库
                        DBHelper helper = new DBHelper(Activity_SendMsg.this, "MsgTbl");
                        helper.creatTable();
                        helper.insert("msg", msg);
                        helper.close();

                        msg.setName(et_names.getText().toString());//因为此时msg的name已经更新
                        Intent intent = new Intent(Activity_SendMsg.this, Activity_Msg.class);
                        intent.putExtra("msg", msg);

                        if (isGroup || isTemp) {
                            startActivity(intent);
                        }

                        Toast.makeText(Activity_SendMsg.this, "已经准备好发送了", Toast.LENGTH_SHORT).show();

                        Activity_SendMsg.this.setResult(RESULT_OK, intent);
                        Activity_SendMsg.this.finish();
                        overridePendingTransition(R.anim.scale_stay,
                                R.anim.out_left_right);
                    } else {
                        // 把短信进行拆分
                        ArrayList<String> contents = smsManager
                                .divideMessage(et_content.getText().toString() + "【来自“收到请回复”客户端】");

                        for (int i = 0; i < phones.size(); i++) {
                            // 参数（要发送的号码，信息中心的号码Null，内容，，)
                            smsManager.sendMultipartTextMessage(phones.get(i), null, contents, null, null);
                        }

                        Calendar current = Calendar.getInstance();
                        MyDate mydate = new MyDate(current.get(Calendar.YEAR),
                                current.get(Calendar.MONTH) + 1,
                                current.get(Calendar.DAY_OF_MONTH),
                                current.get(Calendar.HOUR_OF_DAY),
                                current.get(Calendar.MINUTE));

                        Msg msg = new Msg();

                        if (!fromGroup) {
                            String new_names = "";

                            for (String name : names) {
                                new_names += "0" + name + ",";
                            }

                            msg.setName(new_names);

                        } else {
                            String new_names = "";

                            new_names += et_names.getText().toString() + "@@@";

                            for (int i = 0; i < g_names.size(); i++) {
                                new_names += "0" + g_names.get(i) + ",";
                            }

                            msg.setName(new_names);
                        }

                        msg.setContent(et_content.getText().toString());
                        msg.setSendtime(mydate);

                        // 插入数据库
                        DBHelper helper = new DBHelper(Activity_SendMsg.this, "MsgTbl");
                        helper.creatTable();
                        helper.insert("msg", msg);
                        helper.close();

                        msg.setName(et_names.getText().toString());
                        Intent intent = new Intent(Activity_SendMsg.this, Activity_Msg.class);
                        intent.putExtra("msg", msg);

                        if (isGroup || isTemp) {
                            startActivity(intent);
                        }

                        Toast.makeText(Activity_SendMsg.this, "发送成功", Toast.LENGTH_SHORT).show();

                        Activity_SendMsg.this.setResult(RESULT_OK, intent);
                        Activity_SendMsg.this.finish();
                        overridePendingTransition(R.anim.scale_stay,
                                R.anim.out_left_right);
                    }
                } else if (et_names.getText().toString().isEmpty()) {
                    Toast.makeText(Activity_SendMsg.this, "请输入收件人", Toast.LENGTH_SHORT).show();
                } else if (et_content.getText().toString().isEmpty()) {
                    Toast.makeText(Activity_SendMsg.this, "请输入正文", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setFabAnimation() {
        if (fab_send.getVisibility() == View.GONE) {
            Animation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setInterpolator(new DecelerateInterpolator());
            animation.setFillAfter(true);
            animation.setDuration(1000);
            animation.setStartOffset(200);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    fab_send.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            fab_send.startAnimation(animation);

        } else {
            Animation animation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setInterpolator(new AnticipateInterpolator());
            animation.setFillAfter(true);
            animation.setDuration(1000);
            animation.setStartOffset(200);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    fab_send.setVisibility(View.GONE);
                    sv_sendmsg.smoothScrollTo(0, (int) (32 * scale + 0.5f));
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            fab_send.startAnimation(animation);
        }
    }

    @OnClick({R.id.imageButton_sendmsg_add, R.id.editText_sendmsg_names,
            R.id.switch_sendmsg_menu_settime, R.id.switch_sendmsg_menu_temp})
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.switch_sendmsg_menu_temp:
                if (sw_temp.isChecked()) {
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

                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);

                    mBuilder.setItems(titles, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cursor.moveToPosition(which);
                            et_content.setText(cursor.getString(2));

                            tHelper.close();

                            hasTemp = true;
                        }
                    });

                    AlertDialog alertDialog = mBuilder.create();
                    alertDialog.setCanceledOnTouchOutside(true);
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (!hasTemp) {
                                sw_temp.setChecked(!sw_temp.isChecked());
                            }
                        }
                    });
                    alertDialog.show();

                } else {
                    hasTemp = false;
                    et_content.setText("");
                }

                break;

            case R.id.switch_sendmsg_menu_settime:
                if (!isOnTime) { // 非定时，则弹出时间设置

                    MyDate myDate = getMyDate("date");
                    assert myDate != null;
                    DateDialog dateDialog = new DateDialog(this, this, myDate.getYear(), myDate.getMonth(), myDate.getDay());
                    dateDialog.show();
                } else { // 否则取消设定好的定时
                    isOnTime = false;
                    tv_receiver.setText("接收者");

                    Toast.makeText(this, "已取消定时发送", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.editText_sendmsg_names:
            case R.id.imageButton_sendmsg_add:

                String[] name = {"从通讯录中选取", "从我的群组中选取"};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setItems(name, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 通讯录中选取
                        if (which == 0) {
                            dialog.dismiss();

                            Intent openActivity = new Intent(Activity_SendMsg.this,
                                    Activity_Contacts.class);
                            openActivity.putExtra("names", names);

                            startActivityForResult(openActivity,
                                    Value.CHOOSE_CONTACTS);
                            overridePendingTransition(R.anim.in_right_left,
                                    R.anim.scale_stay);
                        } else { // 我的群组中选取
                            fromGroup = true;

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

                            AlertDialog.Builder gBuilder = new AlertDialog.Builder(Activity_SendMsg.this);

                            gBuilder.setItems(names, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    cursor.moveToPosition(which);
                                    et_names.setText(cursor.getString(1));
                                    String member_s = cursor.getString(2);

                                    String[] member_split = member_s.split("#!#");

                                    // 获取选取的组所有成员的手机号
                                    phones.clear();
                                    g_names.clear();
                                    for (String aMember_split : member_split) {
                                        String[] member_each = aMember_split.split(":");

                                        phones.add(member_each[1]);

                                        g_names.add(member_each[0]);
                                    }

                                    tHelper.close();
                                }
                            });

                            AlertDialog gDialog = gBuilder.create();
                            gDialog.setCanceledOnTouchOutside(true);
                            gDialog.show();
                        }
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();

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

    private MyDate getMyDate(String which) {
        if ("date".equals(which)) {
            int year, month, day;

            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);

            return new MyDate(year, month, day, 0, 0);
        } else if ("time".equals(which)) {
            int hour, minute;

            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);

            return new MyDate(0, 0, 0, hour, minute);
        } else {
            return null;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear++;
        date = null;
        date = year + "-" + monthOfYear + "-" + dayOfMonth;

        mYear = year;
        mMonth = monthOfYear;
        mDay = dayOfMonth;

        MyDate myDate = getMyDate("time");
        assert myDate != null;
        TimeDialog dialog = new TimeDialog(this, this, myDate.getHour(), myDate.getMinute(), true);
        dialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (minute >= 10) {
            date += " " + hourOfDay + ":" + minute;
        } else {
            date += " " + hourOfDay + ":0" + minute;
        }

        tv_receiver.append("  (定时:" + date + ")");
        date = null;

        mHour = hourOfDay;
        mMinute = minute;

        isOnTime = true;
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
                    String names_s = data.getStringExtra("names");

                    et_names.setText(names_s.replace(";", ","));

                    names = names_s.split("; ");

                    phones = data.getStringArrayListExtra("phones");
                }
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
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

    // 时间选择对话框
    public static class TimeDialog extends TimePickerDialog implements DialogInterface.OnCancelListener,
            DialogInterface.OnClickListener {

        public TimeDialog(Context context, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView) {
            super(context, callBack, hourOfDay, minute, is24HourView);
            setOnCancelListener(this);
            setButton(TimePickerDialog.BUTTON_NEGATIVE, "取消", this);
            setButton(TimePickerDialog.BUTTON_POSITIVE, "完成", this);
        }

        @Override
        protected void onStop() {
        }


        @Override
        public void onCancel(DialogInterface dialog) {
            sw_settime.setChecked(false);
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == TimePickerDialog.BUTTON_NEGATIVE) {
                sw_settime.setChecked(false);
            } else if (which == TimePickerDialog.BUTTON_POSITIVE) {
                super.onClick(dialog, which);
            }
        }
    }

    // 日期选择对话框
    public static class DateDialog extends DatePickerDialog implements DialogInterface.OnCancelListener,
            DialogInterface.OnClickListener {

        public DateDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
            super(context, callBack, year, monthOfYear, dayOfMonth);
            setOnCancelListener(this);
            setButton(TimePickerDialog.BUTTON_NEGATIVE, "取消", this);
            setButton(TimePickerDialog.BUTTON_POSITIVE, "完成", this);
        }

        @Override
        protected void onStop() {
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            sw_settime.setChecked(false);
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DatePickerDialog.BUTTON_NEGATIVE) {
                sw_settime.setChecked(false);
            } else if (which == DatePickerDialog.BUTTON_POSITIVE) {
                super.onClick(dialog, which);
            }
        }
    }
}
