package com.ut.database.utils;

import android.text.TextUtils;

import com.ut.database.entity.DeviceKey;
import com.ut.database.entity.DeviceKeyAuth;
import com.ut.database.entity.EnumCollection;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * author : zhouyubin
 * time   : 2019/01/10
 * desc   :
 * version: 1.0
 */
public class DeviceKeyUtil {
    public static Boolean[] getWeekAuthData(String weekAuthDataString) {
        Boolean[] weekAuthData = new Boolean[]{false, false, false, false, false, false, false};
        if (!TextUtils.isEmpty(weekAuthDataString)) {
            String[] temp = weekAuthDataString.split(",");
            for (String i : temp) {
                int ti = Integer.parseInt(i);
                if (ti < 0 || ti >= weekAuthData.length) continue;
                weekAuthData[ti] = true;
            }
        }
        return weekAuthData;
    }

    public static String getTimeCtrlByWeekAuthData(Boolean[] weekAuthData) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < weekAuthData.length; i++) {
            if (weekAuthData[i]) {
                stringBuilder.append(i);
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }

    public static int getKeyStatus(boolean isAuth, String timeICtl, long timeEnd, int openLockCnt, int openLockCntUsed) {
        if (!isAuth) return EnumCollection.DeviceKeyStatus.NORMAL.ordinal();
        if (getKeyAuthType(isAuth, timeICtl) == EnumCollection.DeviceKeyAuthType.TIMELIMIT.ordinal()) {
            if (System.currentTimeMillis() > timeEnd) {
                return EnumCollection.DeviceKeyStatus.EXPIRED.ordinal();
            }
        } else {
            if (getDateFrom1970(System.currentTimeMillis()) > getDateFrom1970(timeEnd)) {
                return EnumCollection.DeviceKeyStatus.EXPIRED.ordinal();
            }
        }
        if (openLockCnt != 255 && openLockCnt - openLockCntUsed < 1) {
            return EnumCollection.DeviceKeyStatus.INVALID.ordinal();
        }
        return EnumCollection.DeviceKeyStatus.NORMAL.ordinal();
    }

    public static int getKeyAuthType(boolean isAuth, String timeICtl) {
        if (!isAuth) return EnumCollection.DeviceKeyAuthType.FOREVER.ordinal();
        if (!TextUtils.isEmpty(timeICtl)) {
            return EnumCollection.DeviceKeyAuthType.CYCLE.ordinal();
        }
        return EnumCollection.DeviceKeyAuthType.TIMELIMIT.ordinal();
    }

    public static long getDateFrom1970(long timeStamp) {
        return timeStamp / 1000 / 60 / 24;
    }


}
