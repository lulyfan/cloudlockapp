package com.ut.base;

import android.app.Application;
import android.content.Context;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.operation.MyRetrofit;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.User;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * author : zhouyubin
 * time   : 2018/11/13
 * desc   :
 * version: 1.0
 */
public class BaseApplication extends Application {
    private static Context INSTANCE = null;

    public static Context getAppContext() {
        return INSTANCE;
    }

    private static User mUser;

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

        MyRetrofit.get().setNoLoginListener(() ->
                Observable.just(this)
                        .subscribeOn(Schedulers.io())
                        .map(context -> {
                            CloudLockDatabaseHolder.get().getUUIDDao().deleteUUID();
                            CloudLockDatabaseHolder.get().getUserDao().deleteAllUsers();
                            return RouterUtil.LoginModulePath.Login;
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(url -> ARouter.getInstance().build(url).navigation()));
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

    public static void setUser(User user) {
        mUser = user;
    }

    public static User getUser() {
        return mUser;
    }
}
