package com.ut.cloudlock;

import com.ut.base.BaseApplication;
import com.ut.database.database.CloudLockDatabaseHolder;

/**
 * author : zhouyubin
 * time   : 2018/11/13
 * desc   :
 * version: 1.0
 */
public class MyApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        CloudLockDatabaseHolder.get().init(this);
    }
}
