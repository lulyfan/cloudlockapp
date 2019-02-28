package com.ut.base.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.ut.base.AppManager;
import com.ut.jpushlib.CLJPushReceiver;

public class UTCLReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (CLJPushReceiver.REMOTELOGIN_ACTION.equals(intent.getAction())) {
            AppManager.getAppManager().currentActivity().remoteLogin();
        }
    }
}