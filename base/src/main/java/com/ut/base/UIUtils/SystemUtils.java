package com.ut.base.UIUtils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * author : zhouyubin
 * time   : 2018/11/29
 * desc   :
 * version: 1.0
 */
public class SystemUtils {
    public static int getWindowHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public static int getWindowWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static int[] getWindowWH(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return new int[]{dm.widthPixels, dm.heightPixels};
    }

    public static int dp2px(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void showKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 判断是否同一天
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameDate(long date1, long date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(date2);
        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
                .get(Calendar.YEAR);
        boolean isSameMonth = isSameYear
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        boolean isSameDate = isSameMonth
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2
                .get(Calendar.DAY_OF_MONTH);
        return isSameDate;
    }

    public static String getTimeDate(long time) {
        return new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date(time));
    }

    /**
     * 版本号对比
     *
     * @param oldVersion
     * @param newVersion
     * @return error : 返回-2 既传入版本号格式有误
     * oldVersion > newVersion  return -1
     * oldVersion = newVersion  return 0
     * oldVersion < newVersion  return 1
     */
    public static int compareVersions(String oldVersion, String newVersion) {
        //返回结果: -2 ,-1 ,0 ,1
        int result = 0;
        String matchStr = "[0-9]+(\\.[0-9]+)*";
        oldVersion = oldVersion.trim();
        newVersion = newVersion.trim();
        //非版本号格式,返回error
        if (!oldVersion.matches(matchStr) || !newVersion.matches(matchStr)) {
            return -2;
        }
        String[] oldVs = oldVersion.split("\\.");
        String[] newVs = newVersion.split("\\.");
        int oldLen = oldVs.length;
        int newLen = newVs.length;
        int len = oldLen > newLen ? oldLen : newLen;
        for (int i = 0; i < len; i++) {
            if (i < oldLen && i < newLen) {
                int o = Integer.parseInt(oldVs[i]);
                int n = Integer.parseInt(newVs[i]);
                if (o > n) {
                    result = -1;
                    break;
                } else if (o < n) {
                    result = 1;
                    break;
                }
            } else {
                if (oldLen > newLen) {
                    for (int j = i; j < oldLen; j++) {
                        if (Integer.parseInt(oldVs[j]) > 0) {
                            result = -1;
                        }
                    }
                    break;
                } else if (oldLen < newLen) {
                    for (int j = i; j < newLen; j++) {
                        if (Integer.parseInt(newVs[j]) > 0) {
                            result = 1;
                        }
                    }
                    break;
                }
            }
        }
        return result;
    }

}
