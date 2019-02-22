package com.ut.jpushlib;

import android.content.Context;

import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.service.JPushMessageReceiver;

/**
 * author : chenjiajun
 * time   : 2019/1/21
 * desc   :
 */
public class CLJPushMessageReceiver extends JPushMessageReceiver {

    private static final String TAG = "CLJPushMessageReceiver";

    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onAliasOperatorResult(context, jPushMessage);
        AliasOperatorHelper.getInstance().onAliasOperatorResult(context, jPushMessage);//当
    }

    @Override
    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onCheckTagOperatorResult(context, jPushMessage);
    }

    @Override
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onMobileNumberOperatorResult(context, jPushMessage);
    }

    @Override
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onTagOperatorResult(context, jPushMessage);
    }
}
