package com.ut.base;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.operation.MyRetrofit;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.commoncomponent.CLToast;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.User;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

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

        MyRetrofit.get().setNoLoginListener(() -> {
            Observable.just(this)
                    .subscribeOn(Schedulers.io())
                    .map(context -> {
                        CloudLockDatabaseHolder.get().getUUIDDao().deleteUUID();
                        CloudLockDatabaseHolder.get().getUserDao().deleteAllUsers();
                        return RouterUtil.LoginModulePath.Login;
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(url -> {
//                                CLToast.showAtBottom(getAppContext(), "还没登录");
                                Toast.makeText(getAppContext(), "还没登录", Toast.LENGTH_LONG).show();
                                ARouter.getInstance().build(url).navigation();
                            }
                    );
        });

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
