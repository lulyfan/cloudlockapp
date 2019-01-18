package com.ut.cloudlock.jpush;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ut.base.R;
import com.ut.base.Utils.UTLog;
import com.ut.cloudlock.activity.MainActivity;

import cn.jpush.android.api.JPushInterface;

/**
 * author : chenjiajun
 * time   : 2019/1/18
 * desc   :
 */
public class CLJPushReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(action)) {
            //自定义消息

            Bundle extras = intent.getExtras();

            if(extras != null) {

                String title = extras.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
                String content = extras.getString(JPushInterface.EXTRA_ALERT);
                int id = extras.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);

                Intent i = new Intent(context, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                i.putExtra("openTab", "message");

                Notification builder = new Notification.Builder(context)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true)
                        .setContentIntent(PendingIntent.getActivity(context, (int)(Math.random() * 100), i, PendingIntent.FLAG_UPDATE_CURRENT))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .build();

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if(notificationManager != null) {
                    notificationManager.notify(id, builder);
                }

                String dataJson= extras.getString(JPushInterface.EXTRA_EXTRA);
                UTLog.e("Jpush data ---> "+ dataJson);
            }
        }
    }
}
