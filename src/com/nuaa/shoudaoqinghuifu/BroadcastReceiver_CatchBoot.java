package com.nuaa.shoudaoqinghuifu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BroadcastReceiver_CatchBoot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 获取开机广播，启动闹铃服务，同时关闭系统自带日历提醒
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Service_Alarm.useCalendar = false;

            Intent service = new Intent(context, Service_Alarm.class);
            context.startService(service);
        }
    }
}
