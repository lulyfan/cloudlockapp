package com.ut.module_lock.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.ut.base.BaseApplication;
import com.ut.module_lock.R;

/**
 * author : zhouyubin
 * time   : 2018/11/26
 * desc   :
 * version: 1.0
 */
public class LockKey implements Parcelable {
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

    private String mac;
    private String deviceId;
    private String validTime;

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

    public int getLockType() {
        return lockType;
    }

    public void setLockType(int lockType) {
        this.lockType = lockType;
    }

    public int getKeyType() {
        return keyType;
    }

    public void setKeyType(int keyType) {
        this.keyType = keyType;
    }

    public int getElectricity() {
        return electricity;
    }

    public void setElectricity(int electricity) {
        this.electricity = electricity;
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

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getValidTime() {
        return validTime;
    }

    public void setValidTime(String validTime) {
        this.validTime = validTime;
    }

    public static Creator<LockKey> getCREATOR() {
        return CREATOR;
    }

    public static final Creator<LockKey> CREATOR = new Creator<LockKey>() {
        @Override
        public LockKey createFromParcel(Parcel in) {
            return new LockKey(in);
        }

        @Override
        public LockKey[] newArray(int size) {
            return new LockKey[size];
        }
    };

    protected LockKey(Parcel in) {
        name = in.readString();
        status = in.readInt();
        statusStr = in.readString();
        lockType = in.readInt();
        lockTypeStr = in.readString();
        keyType = in.readInt();
        keyTypeStr = in.readString();
        userType = in.readInt();
        userTypeStr = in.readString();
        electricity = in.readInt();
        electricityStr = in.readString();
        mac = in.readString();
        deviceId = in.readString();
        validTime = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(status);
        dest.writeString(statusStr);
        dest.writeInt(lockType);
        dest.writeString(lockTypeStr);
        dest.writeInt(keyType);
        dest.writeString(keyTypeStr);
        dest.writeInt(userType);
        dest.writeString(userTypeStr);
        dest.writeInt(electricity);
        dest.writeString(electricityStr);
        dest.writeString(mac);
        dest.writeString(deviceId);
        dest.writeString(validTime);
    }
}
