package com.nuaa.shoudaoqinghuifu;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.os.Looper;

import java.util.Calendar;
import java.util.Vector;

public class Service_Alarm extends Service {
    public static boolean useCalendar = true;
    private Vector<Memo> vector_memos = new Vector<>();
    private DBHelper helper;
    private Calendar current = Calendar.getInstance();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                Looper.prepare();

                // 读取数据库，获取所有备忘
                helper = new DBHelper(Service_Alarm.this, "MemoTbl");
                helper.creatTable();
                readDataBase();

                // 创建am
                for (int i = 0; i < vector_memos.size(); i++) {
                    Memo tmp = vector_memos.get(i);

                    Calendar tmpTime = Calendar.getInstance();
                    MyDate date_memo = tmp.date_memo;
                    tmpTime.set(date_memo.getYear(), date_memo.getMonth() - 1,
                            date_memo.getDay(), date_memo.getHour(), date_memo.getMinute());

                    // 未过期且needNotify
                    if (tmp.needNotify && current.before(tmpTime)) {
                        setAlarm(tmp, tmpTime);
                    }
                }
            }
        }).start();

        stopSelf();

        return START_STICKY;
    }

    public void setAlarm(Memo memo, Calendar tmpTime) {
        // 获取AlarmManager系统服务对象
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(getApplicationContext(),
                BroadcastReceiver_Alarm.class);
        intent.putExtra("memo", memo);
        intent.setAction(Value.ACTION_ALARM);
        // 第二个参数为ID，不可重复
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(),
                memo.id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 设置闹钟时间
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());

        c.set(Calendar.YEAR, tmpTime.get(Calendar.YEAR));
        c.set(Calendar.MONTH, tmpTime.get(Calendar.MONTH));
        c.set(Calendar.DAY_OF_MONTH, tmpTime.get(Calendar.DAY_OF_MONTH));
        c.set(Calendar.HOUR_OF_DAY, tmpTime.get(Calendar.HOUR_OF_DAY));
        c.set(Calendar.MINUTE, tmpTime.get(Calendar.MINUTE));

        // 将秒和毫秒设置为0
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
    }

    // 从数据库中读取数据
    private void readDataBase() {
        vector_memos.clear();

        Cursor cursor = helper.query();

        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            Memo memo = readDataWithCursor(cursor);

            vector_memos.add(memo);

            cursor.moveToNext();
        }
    }

    // 读取一条备忘
    private Memo readDataWithCursor(Cursor cursor) {
        String content = cursor.getString(1);
        String address = cursor.getString(2);
        String date_happen_s = cursor.getString(3);
        String date_memo_s = cursor.getString(4);
        String needNotify_s = cursor.getString(5);
        int id = cursor.getInt(6);

        String[] date_happen_array = date_happen_s.split("-");
        String[] date_memo_array = date_memo_s.split("-");

        MyDate date_happen = new MyDate(
                Integer.parseInt(date_happen_array[0]),
                Integer.parseInt(date_happen_array[1]),
                Integer.parseInt(date_happen_array[2]),
                Integer.parseInt(date_happen_array[3]),
                Integer.parseInt(date_happen_array[4]));

        MyDate date_memo = new MyDate(
                Integer.parseInt(date_memo_array[0]),
                Integer.parseInt(date_memo_array[1]),
                Integer.parseInt(date_memo_array[2]),
                Integer.parseInt(date_memo_array[3]),
                Integer.parseInt(date_memo_array[4]));

        boolean needNotify = "true".equals(needNotify_s);

        return new Memo(content, address, date_happen, date_memo, needNotify, id);
    }

}
