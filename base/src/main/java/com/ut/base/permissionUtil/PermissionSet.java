package com.ut.base.permissionUtil;

import android.Manifest;

/**
 * author : zhouyubin
 * time   : 2018/12/19
 * desc   :
 * version: 1.0
 */
public class PermissionSet {
    public static final String[] BluetoothPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
    };
}
