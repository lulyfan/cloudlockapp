package com.ut.module_lock.common;

import com.ut.database.entity.EnumCollection;
import com.ut.module_lock.R;

import java.util.HashMap;
import java.util.Map;

import kotlin.jvm.internal.Intrinsics;

/**
 * author : zhouyubin
 * time   : 2019/01/24
 * desc   :
 * version: 1.0
 */
public class LockTypeIcon {
    public static Map<Integer, Map<Integer, Integer>> LockTypeIconMap = new HashMap<>();

    static {
        Map<Integer, Integer> padLockIcon = new HashMap<>();
        padLockIcon.put(EnumCollection.UserType.ADMIN.ordinal(), R.mipmap.icon_lock_padlock_manager);
        padLockIcon.put(EnumCollection.UserType.AUTH.ordinal(), R.mipmap.icon_lock_padlock_auth);
        padLockIcon.put(EnumCollection.UserType.NORMAL.ordinal(), R.mipmap.icon_lock_padlock_normal);
        LockTypeIconMap.put(EnumCollection.LockType.PADLOCK.getType(), padLockIcon);
        Map<Integer, Integer> smartLockIcon = new HashMap<>();
        smartLockIcon.put(EnumCollection.UserType.ADMIN.ordinal(), R.mipmap.icon_lock_doorlock_manager);
        smartLockIcon.put(EnumCollection.UserType.AUTH.ordinal(), R.mipmap.icon_lock_doorlock_auth);
        smartLockIcon.put(EnumCollection.UserType.NORMAL.ordinal(), R.mipmap.icon_lock_doorlock_normal);
        LockTypeIconMap.put(EnumCollection.LockType.SMARTLOCK.getType(), smartLockIcon);
    }
}
