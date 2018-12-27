package com.ut.base;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.operation.MyRetrofit;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.User;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * author : zhouyubin
 * time   : 2018/11/13
 * desc   :
 * version: 1.0
 */
public class BaseApplication extends MultiDexApplication {
    private static Context INSTANCE = null;
    public static final String WEBSOCKET_APP_ID = "cloudlockbuss";

    public static Context getAppContext() {
        return INSTANCE;
    }

    public static User mUser;

    private Scheduler uiScheduler;

    @Override
    public void onCreate() {
        super.onCreate();

        INSTANCE = this;

        //初始化Arouter
        initARouter();

        //初始化logger
        initLogger();

        //初始化数据库
        initDatabase();

        //主线程调度器，用于RxJava
        uiScheduler = Schedulers.from(new UiExecutor());

        //TODO 临时只添加极光推送，后面改为多种推送
        initJpush();
    }

    private void initJpush() {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }

    private void initDatabase() {
        CloudLockDatabaseHolder.get().init(this);
    }

    private void initLogger() {
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    private void initARouter() {
        if (BuildConfig.DEBUG) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this); // 尽可能早，推荐在Application中初始化
    }

    private static int sequence = 0;

    public static void setUser(User user) {
        mUser = user;
        if (user == null) {
            return;
        }
        JPushInterface.setAlias(getAppContext(), sequence++, String.valueOf(mUser.getId()));
        MyRetrofit.get().setWebSocketListener(new WebSocketDataHandler());
        MyRetrofit.get().sendUserId((int) user.getId(), WEBSOCKET_APP_ID);
    }

    //TODO 临时用极光
    public static void deleteJpushAlias() {
        JPushInterface.deleteAlias(getAppContext(), sequence++);
    }

    public static User getUser() {
        if (mUser == null) {
            mUser = new User();
            mUser.setId(-1);
        }
        return mUser;
    }

    public static Scheduler getUiScheduler() {
        BaseApplication application = (BaseApplication) getAppContext();
        return application.uiScheduler;
    }
}
