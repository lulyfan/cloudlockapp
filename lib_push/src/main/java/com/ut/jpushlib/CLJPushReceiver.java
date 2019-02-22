package com.ut.jpushlib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


import cn.jpush.android.api.JPushInterface;

/**
 * author : chenjiajun
 * time   : 2019/1/18
 * desc   : 极光推送接收器
 */
public class CLJPushReceiver extends BroadcastReceiver {
    public static final String REMOTELOGIN_ACTION = "com.ut.cloudlock.remotelogin";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(action)) {
            //自定义消息
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String dataJson = extras.getString(JPushInterface.EXTRA_MESSAGE);
                if (!TextUtils.isEmpty(dataJson)) {
                    JSONObject jsonObject = JSON.parseObject(dataJson);
                    int code = jsonObject.getIntValue("code");
                    if (code == 521) {//异地登录
                        context.sendBroadcast(new Intent(REMOTELOGIN_ACTION));
                    }
                }
            }
        }
    }
}
