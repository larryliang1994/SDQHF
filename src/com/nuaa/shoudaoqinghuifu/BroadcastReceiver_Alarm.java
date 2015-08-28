package com.nuaa.shoudaoqinghuifu;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class BroadcastReceiver_Alarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Value.ACTION_ALARM)) {

            NotificationManager manager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    context);
            Memo memo = (Memo) intent.getSerializableExtra("memo");
            builder.setContentTitle(memo.content);
            builder.setSmallIcon(R.drawable.icon_outside_small);
            builder.setContentText(memo.address);
            builder.setAutoCancel(true);
            builder.setDefaults(Notification.DEFAULT_VIBRATE);
            builder.setOnlyAlertOnce(true);

            // 设置响应事件
            Intent myIntent = new Intent(context, Activity_CheckMemo.class);
            myIntent.putExtra("memo", memo);
            myIntent.putExtra("isBroadcast", true);

            PendingIntent pi = PendingIntent.getActivity(context, 0, myIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pi);

            manager.notify(1, builder.build());
        }
    }
}
