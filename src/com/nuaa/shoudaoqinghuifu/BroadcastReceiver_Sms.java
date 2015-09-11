package com.nuaa.shoudaoqinghuifu;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class BroadcastReceiver_Sms extends BroadcastReceiver {
    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Object[] objs = (Object[]) bundle.get("pdus");

        if (objs != null) {
            SmsMessage[] msgs = new SmsMessage[objs.length];

            for (int i = 0; i < msgs.length; i++) {
                SmsMessage msg = SmsMessage.createFromPdu((byte[]) objs[i]);

                // 获取短信的发送者号码
                String phoneNunber = msg.getDisplayOriginatingAddress();

                // 查询系统数据库，匹配人名
                String name = getContactNameWithPhone(context, phoneNunber);

                // 查询本地数据库，顺便打勾
                if (name != null && !"".equals(name)) {
                    readDatabase(context, name);
                }
            }
        }
    }

    private String getContactNameWithPhone(Context context, String phoneNum) {
        String contactName = "";
        ContentResolver cr = context.getContentResolver();
        Cursor pCur = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
                new String[]{phoneNum}, null);
        if (pCur.moveToFirst()) {
            contactName = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            pCur.close();
        }
        return contactName;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void readDatabase(Context context, String name) {
        DBHelper helper = new DBHelper(context, "MsgTbl");
        helper.creatTable();

        Cursor cursor = helper.query();
        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            String names = cursor.getString(1);

            // 若有这个人，并且其为0，则将其0设为1
            if (names.contains("0" + name + ",")) {
                names = names.replace("0" + name + ",", "1" + name + ",");

                Msg msg = readDataWithCursor(cursor, names);

                int b = cursor.getInt(0);

                int a = helper.update(cursor.getInt(0) + "", "msg", msg);

                Toast.makeText(context, name + "已回复", Toast.LENGTH_SHORT).show();
            }

            cursor.moveToNext();
        }
    }

    // 读取一条消息
    private Msg readDataWithCursor(Cursor cursor, String name) {
        String content = cursor.getString(2);
        String sendtime = cursor.getString(3);

        String[] sendtimearray = sendtime.split("-");
        MyDate send_time = new MyDate(
                Integer.parseInt(sendtimearray[0]),
                Integer.parseInt(sendtimearray[1]),
                Integer.parseInt(sendtimearray[2]),
                Integer.parseInt(sendtimearray[3]),
                Integer.parseInt(sendtimearray[4]));

        return new Msg(name, content, send_time);
    }
}
