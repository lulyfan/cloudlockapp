package com.ut.base.Utils;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import java.util.List;
import java.util.Map;

/**
 * author : zhouyubin
 * time   : 2018/11/14
 * desc   :
 * version: 1.0
 */
public class UTLog {
    public static final boolean isDebug = true;
    public static final boolean isSaveToFile = false;

    static {
        if (isDebug) {
            FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                    .showThreadInfo(false)  // 是否打印线程号,默认true
                    .methodCount(5)         // 展示几个方法数,默认2
                    .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
//                .logStrategy(customLog) //是否更换打印输出,默认为logcat
                    .tag("CLOCKLOCK")   // 全局的tag
                    .build();
            Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
            if (isSaveToFile) {
                Logger.addLogAdapter(new DiskLogAdapter());
            }
        }
    }

    public static void d(String msg) {
        if (isDebug)
            Logger.d(msg);
    }

    public static void d(String tag, String msg) {
        if (isDebug) {
            Logger.log(Logger.DEBUG, tag, msg, null);
//            Logger.t(tag);
//            Logger.d(msg);
        }
    }

    public static void e(String msg) {
        if (isDebug)
            Logger.e(msg);
    }

    public static void e(String tag, String msg) {
        if (isDebug) {
            Logger.t(tag);
            Logger.e(msg);
        }
    }

    public static void w(String msg) {
        if (isDebug)
            Logger.w(msg);
    }

    public static void w(String tag, String msg) {
        if (isDebug) {
            Logger.t(tag);
            Logger.w(msg);
        }
    }

    public static void i(String msg) {
        if (isDebug)
            Logger.i(msg);
    }

    public static void i(String tag, String msg) {
        if (isDebug) {
            Logger.t(tag);
            Logger.i(msg);
        }
    }

    public static void v(String msg) {
        if (isDebug)
            Logger.v(msg);
    }

    public static void v(String tag, String msg) {
        if (isDebug) {
            Logger.t(tag);
            Logger.v(msg);
        }
    }

    public static void map(Map msg) {
        if (isDebug)
            Logger.d(msg);
    }

    public static void map(String tag, Map msg) {
        if (isDebug) {
            Logger.t(tag);
            Logger.d(msg);
        }
    }

    public static void list(List msg) {
        if (isDebug)
            Logger.d(msg);
    }

    public static void list(String tag, List msg) {
        if (isDebug) {
            Logger.t(tag);
            Logger.d(msg);
        }
    }

    public static void json(String msg) {
        if (isDebug)
            Logger.json(msg);
    }

    public static void json(String tag, String msg) {
        if (isDebug) {
            Logger.t(tag);
            Logger.json(msg);
        }
    }
}
