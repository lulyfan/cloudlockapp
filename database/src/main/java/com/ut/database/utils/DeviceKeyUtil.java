package com.ut.database.utils;

import android.text.TextUtils;

import com.ut.database.entity.DeviceKey;
import com.ut.database.entity.DeviceKeyAuth;
import com.ut.database.entity.EnumCollection;

/**
 * author : zhouyubin
 * time   : 2019/01/10
 * desc   :
 * version: 1.0
 */
public class DeviceKeyUtil {
    public static Boolean[] weekAuthData = new Boolean[]{false, false, false, false, false, false, false};

    public static Boolean[] getWeekAuthData(String weekAuthDataString) {
        if (!TextUtils.isEmpty(weekAuthDataString)) {
            String[] temp = weekAuthDataString.split(",");
            for (String i : temp) {
                int ti = Integer.parseInt(i);
                weekAuthData[ti] = true;
            }
        }
        return weekAuthData;
    }

    public static int getKeyStatus(DeviceKeyAuth deviceKeyAuth) {
        if (getKeyAuthType(deviceKeyAuth) == EnumCollection.DeviceKeyAuthType.TIMELIMIT.ordinal()) {
            if (System.currentTimeMillis() > deviceKeyAuth.getTimeEnd()) {
                return EnumCollection.DeviceKeyStatus.EXPIRED.ordinal();
            }
        } else {
            if (getDateFrom1970(System.currentTimeMillis()) > getDateFrom1970(deviceKeyAuth.getTimeEnd())) {
                return EnumCollection.DeviceKeyStatus.EXPIRED.ordinal();
            }
        }
        if (deviceKeyAuth.getOpenLockCnt() - deviceKeyAuth.getOpenLockCntUsed() < 1) {
            return EnumCollection.DeviceKeyStatus.INVALID.ordinal();
        }
        return EnumCollection.DeviceKeyStatus.NORMAL.ordinal();
    }

    public static int getKeyAuthType(DeviceKeyAuth deviceKeyAuth) {
        if (!TextUtils.isEmpty(deviceKeyAuth.getTimeICtl())) {
            return EnumCollection.DeviceKeyAuthType.CYCLE.ordinal();
        }
        return EnumCollection.DeviceKeyAuthType.TIMELIMIT.ordinal();
    }

    public static long getDateFrom1970(long timeStamp) {
        return timeStamp / 1000 / 60 / 24;
    }
}
