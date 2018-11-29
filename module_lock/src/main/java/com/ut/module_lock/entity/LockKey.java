package com.ut.module_lock.entity;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.ut.base.BaseActivity;
import com.ut.base.BaseApplication;
import com.ut.module_lock.R;

/**
 * author : zhouyubin
 * time   : 2018/11/26
 * desc   :
 * version: 1.0
 */
public class LockKey extends BaseObservable {
    private String name;
    private int status;
    private String statusStr;
    private int lockType;
    private String lockTypeStr;
    private int keyType;
    private String keyTypeStr;
    private int userType;
    private String userTypeStr;
    private int electricity;
    private String electricityStr;


    public LockKey(String name, int status, int lockType, int keyType, int userType, int electricity) {
        this.name = name;
        this.status = status;
        this.lockType = lockType;
        this.keyType = keyType;
        this.userType = userType;
        this.electricity = electricity;
    }

    public void init() {
        statusStr = BaseApplication.getAppContext().getResources().getStringArray(R.array.key_status)[status];
        lockTypeStr = BaseApplication.getAppContext().getResources().getStringArray(R.array.lock_type)[lockType];
        keyTypeStr = BaseApplication.getAppContext().getResources().getStringArray(R.array.key_type)[keyType];
        electricityStr = String.valueOf(electricity) + "%";
    }


    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public String getLockTypeStr() {
        return lockTypeStr;
    }

    public void setLockTypeStr(String lockTypeStr) {
        this.lockTypeStr = lockTypeStr;
    }

    public String getKeyTypeStr() {
        return keyTypeStr;
    }

    public void setKeyTypeStr(String keyTypeStr) {
        this.keyTypeStr = keyTypeStr;
    }

    public String getUserTypeStr() {
        return userTypeStr;
    }

    public void setUserTypeStr(String userTypeStr) {
        this.userTypeStr = userTypeStr;
    }

    public String getElectricityStr() {
        return electricityStr;
    }

    public void setElectricityStr(String electricityStr) {
        this.electricityStr = electricityStr;
    }
}
