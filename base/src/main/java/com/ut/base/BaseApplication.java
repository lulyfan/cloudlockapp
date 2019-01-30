package com.ut.base;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.alibaba.android.arouter.launcher.ARouter;
import com.baidu.mobstat.NativeCrashHandler;
import com.baidu.mobstat.StatService;
import com.example.operation.MyRetrofit;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
//import com.squareup.leakcanary.LeakCanary;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.User;
import com.ut.unilink.UnilinkManager;
import com.ut.unilink.cloudLock.CloudLock;

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

        initBaidu();

        initLeakCanary();
        
    }

    private void initLeakCanary() {
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not initLockKey your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
    }

    private void initBaidu() {
        // 打开调试开关，可以查看logcat日志。版本发布前，为避免影响性能，移除此代码
        // 查看方法：adb logcat -s sdkstat
        StatService.setDebugOn(true);

        // 开启自动埋点统计，为保证所有页面都能准确统计，建议在Application中调用。
        // 第三个参数：autoTrackWebview：
        // 如果设置为true，则自动track所有webview；如果设置为false，则不自动track webview，
        // 如需对webview进行统计，需要对特定webview调用trackWebView() 即可。
        // 重要：如果有对webview设置过webchromeclient，则需要调用trackWebView() 接口将WebChromeClient对象传入，
        // 否则开发者自定义的回调无法收到。
        StatService.autoTrace(this, true, false);
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

    public static void clearDataWhenLogout() {//退出登录时做清除数据操作
        CloudLockDatabaseHolder.get().clear();
        BaseApplication.deleteJpushAlias();
        MyRetrofit.get().closeWebSocket();
    }
}
