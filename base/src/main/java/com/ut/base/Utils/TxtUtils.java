package com.ut.base.Utils;

import android.text.TextUtils;

/**
 * author : zhouyubin
 * time   : 2018/11/27
 * desc   :
 * version: 1.0
 */
public class TxtUtils {
    /**
     * 账号处理，取前4后4，中间*，如：1234******5678
     **/
    public static String toEncryptAccount(String account) {
        String temp = account;
        if (!TextUtils.isEmpty(account) && account.length() >= 3) {
            temp = account.substring(0, 3) + "****"
                    + account.substring(account.length() - 3, account.length());
        }
        return temp;
    }
}
