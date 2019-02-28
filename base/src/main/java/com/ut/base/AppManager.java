package com.ut.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.Stack;

/**
 * Activity管理类
 *
 * @author chenjiajun
 */
public class AppManager {
    private static Stack<BaseActivity> activityStack;
    private static AppManager instance;

    private AppManager() {
    }

    /**
     * 单一实例
     */
    public static AppManager getAppManager() {
        if (instance == null) {
            instance = new AppManager();
            activityStack = new Stack<BaseActivity>();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(BaseActivity activity) {
        boolean findOut = false;
        BaseActivity tmp = null;
        for (BaseActivity act : activityStack) {
            if (act.getClass().equals(activity.getClass())) {
                tmp = act;
                findOut = true;
                break;
            }
        }

        if (findOut) {
            activityStack.remove(tmp);
            activityStack.add(activity);
        } else {
            activityStack.add(activity);
        }

    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public BaseActivity currentActivity() {
        if (!activityStack.empty()) {
            return activityStack.lastElement();
        }
        return null;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        if (activity != null) {
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        Stack<BaseActivity> tmp = new Stack<>();
        tmp.addAll(activityStack);
        for (Activity activity : tmp) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
        tmp.clear();
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        if (activityStack == null) return;
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}