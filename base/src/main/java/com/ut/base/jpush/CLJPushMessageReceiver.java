package com.ut.base.jpush;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.ut.unilink.util.Log;

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

        Log.e(TAG, "onAliasOperatorResult " + jPushMessage.getAlias() + " " + JSON.toJSONString(jPushMessage.getTags()));

    }

    @Override
    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onCheckTagOperatorResult(context, jPushMessage);
        Log.e(TAG, "onCheckTagOperatorResult");
    }

    @Override
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onMobileNumberOperatorResult(context, jPushMessage);
        Log.e(TAG, "onMobileNumberOperatorResult");
    }

    @Override
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onTagOperatorResult(context, jPushMessage);
        Log.e(TAG, "onTagOperatorResult");
    }
}
