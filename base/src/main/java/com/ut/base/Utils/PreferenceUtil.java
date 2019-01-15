package com.ut.base.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharePreference基类
 * 这里只写了对最常用的三种基本数据类型的操作。
 */
public class PreferenceUtil {
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private final String FILE_NAME = "userinfo";

    private static PreferenceUtil instance;

    public static PreferenceUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (PreferenceUtil.class) {
                instance = new PreferenceUtil(context);
            }
        }

        return instance;
    }

    private PreferenceUtil(Context context) {
        sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public void setString(String key, String value) {
        sp.edit().putString(key, value).apply();
    }

    public String getString(String key) {
        return sp.getString(key, null);
    }

    public void setBoolean(String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }

    public void setInt(String key, int value) {
        sp.edit().putInt(key, value).apply();
    }

    public int getInt(String key) {
        return sp.getInt(key, 0);
    }

    public void setLong(String key, long value) {
        sp.edit().putLong(key, value).apply();
    }

    public long getLong(String key) {
        return sp.getLong(key, 0);
    }
}
