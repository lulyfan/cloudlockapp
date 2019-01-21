package com.ut.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.DebugUtils;

import com.ut.database.utils.DeviceKeyUtil;

import java.util.Objects;

/**
 * author : zhouyubin
 * time   : 2019/01/09
 * desc   :
 * version: 1.0
 */
@Entity(tableName = "device_key")
public class DeviceKey implements Parcelable {
    @PrimaryKey
    private int deviceId;//主键

    private int keyID;//钥匙编号；
    private String name = "";//名字
    /**
     * {@link com.ut.database.entity.EnumCollection.DeviceKeyType}
     */
    private int keyType;//钥匙类型：指纹 卡片 密码 电子钥匙 手机蓝牙；
    private int keyCfg;//钥匙配置属性；
    private int keyInId;//钥匙内部编号

    private int isAuthKey;//是授权钥匙还是正常钥匙 0：正常钥匙;1:授权钥匙

    /**
     * {@link com.ut.database.entity.EnumCollection.DeviceKeyStatus}
     */
    private int keyStatus;//0:正常;1:已过期;2:已失效;3:已冻结

    /**
     * {@link com.ut.database.entity.EnumCollection.DeviceKeyAuthType}
     */
    private int keyAuthType;//0:永久;1：限时;2:循环

    private int authId;     //授权编号
    private int openLockCnt;  //开锁次数   预留
    private String timeICtl;         //时间控制,控制星期几有效
    private long timeStart;     //开始时间
    private long timeEnd;         //结束时间
    private int openLockCntUsed;//已开锁次数

    private int lockID;//锁对应的id

    public DeviceKey() {
    }

    public DeviceKey(int id, int keyID, String name, int keyType, int keyCfg, int keyInId) {
        this.deviceId = id;
        this.keyID = keyID;
        this.name = name;
        this.keyType = keyType;
        this.keyCfg = keyCfg;
        this.keyInId = keyInId;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getId() {
        return deviceId;
    }

    public void setId(int id) {
        this.deviceId = id;
    }


    public void initName(String[] names) {
        try {
            if (TextUtils.isEmpty(this.name)) {
                this.name = names[keyType] + keyInId;
            }
        } catch (Exception e) {
        }
    }

    public int getKeyID() {
        return keyID;
    }

    public int getRecordKeyId() {
        return -(lockID * 100 + 1);
    }

    public void setKeyID(int keyID) {
        this.keyID = keyID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getKeyType() {
        return keyType;
    }

    public void setKeyType(int keyType) {
        this.keyType = keyType;
    }

    public int getKeyCfg() {
        return keyCfg;
    }

    public void setKeyCfg(int keyCfg) {
        this.keyCfg = keyCfg;
    }

    public int getKeyInId() {
        return keyInId;
    }

    public void setKeyInId(int keyInId) {
        this.keyInId = keyInId;
    }

    public int getKeyStatus() {
        return keyStatus;
    }

    public void setKeyStatus(int keyStatus) {
        this.keyStatus = keyStatus;
    }

    public void setIsAuthKey(boolean isAuthKey) {
        this.isAuthKey = isAuthKey ? 1 : 0;
    }

    public void setIsAuthKey(int isAuthKey) {
        this.isAuthKey = isAuthKey;
    }

    public int getIsAuthKey() {
        return isAuthKey;
    }

    public int getKeyAuthType() {
        return keyAuthType;
    }

    public void setKeyAuthType(int keyAuthType) {
        this.keyAuthType = keyAuthType;
        if (keyAuthType == EnumCollection.DeviceKeyAuthType.FOREVER.ordinal()) {
            this.isAuthKey = 0;
        } else {
            this.isAuthKey = 1;
        }
    }

    public int getLockID() {
        return lockID;
    }

    public void setLockID(int lockID) {
        this.lockID = lockID;
    }

    public void setDeviceKeyAuthData(DeviceKeyAuth deviceKeyAuthData) {
        if (deviceKeyAuthData == null) return;

        this.authId = deviceKeyAuthData.getAuthId();
        this.openLockCnt = deviceKeyAuthData.getOpenLockCnt();
        this.timeICtl = deviceKeyAuthData.getTimeICtl();
        this.timeStart = deviceKeyAuthData.getTimeStart();
        this.timeEnd = deviceKeyAuthData.getTimeEnd();
        this.openLockCntUsed = deviceKeyAuthData.getOpenLockCntUsed();

        this.keyAuthType = DeviceKeyUtil.getKeyAuthType(this.isAuthKey == 1, this.timeICtl);
        if (this.keyStatus != EnumCollection.DeviceKeyStatus.FROZEN.ordinal()) {
            this.keyStatus = DeviceKeyUtil.getKeyStatus(this.isAuthKey == 1, this.timeICtl, this.timeEnd
                    , this.openLockCnt, this.openLockCntUsed);
        }
    }

    public void setUnfreezeStatus() {
        this.keyStatus = DeviceKeyUtil.getKeyStatus(this.isAuthKey == 1, this.timeICtl, this.timeEnd
                , this.openLockCnt, this.openLockCntUsed);
    }


    public int getAuthId() {
        return authId;
    }

    public void setAuthId(int authId) {
        this.authId = authId;
    }

    public int getOpenLockCnt() {
        return openLockCnt;
    }

    public void setOpenLockCnt(int openLockCnt) {
        this.openLockCnt = openLockCnt;
    }

    public String getTimeICtl() {
        return timeICtl;
    }

    public int[] getTimeICtlIntArr() {
        if (!TextUtils.isEmpty(this.timeICtl)) {
            String[] strings = this.timeICtl.split(",");
            int[] temp = new int[strings.length];
            for (int i = 0; i < temp.length; i++) {
                temp[i] = Integer.parseInt(strings[i]);
            }
            return temp;
        }
        return null;
    }

    public void setTimeICtl(String timeICtl) {
        this.timeICtl = timeICtl;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public int getOpenLockCntUsed() {
        return openLockCntUsed;
    }

    public void setOpenLockCntUsed(int openLockCntUsed) {
        this.openLockCntUsed = openLockCntUsed;
    }

    public Boolean[] getWeekAuthData() {
        return DeviceKeyUtil.getWeekAuthData(this.timeICtl);
    }

    public void setWeekAuthData(Boolean[] weekAuthData) {
        this.timeICtl = DeviceKeyUtil.getTimeCtrlByWeekAuthData(weekAuthData);
    }


    @Override
    public String toString() {
        return "DeviceKey{" +
                "id=" + deviceId +
                ", keyID=" + keyID +
                ", name='" + name + '\'' +
                ", keyType=" + keyType +
                ", keyCfg=" + keyCfg +
                ", keyInId=" + keyInId +
                ", isAuthKey=" + isAuthKey +
                ", keyStatus=" + keyStatus +
                ", keyAuthType=" + keyAuthType +
                ", authId=" + authId +
                ", openLockCnt=" + openLockCnt +
                ", timeICtl='" + timeICtl + '\'' +
                ", timeStart=" + timeStart +
                ", timeEnd=" + timeEnd +
                ", openLockCntUsed=" + openLockCntUsed +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceKey deviceKey = (DeviceKey) o;
        return deviceId == deviceKey.deviceId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(deviceId);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.deviceId);
        dest.writeInt(this.keyID);
        dest.writeString(this.name);
        dest.writeInt(this.keyType);
        dest.writeInt(this.keyCfg);
        dest.writeInt(this.keyInId);
        dest.writeInt(this.isAuthKey == 1 ? 1 : 0);
        dest.writeInt(this.keyStatus);
        dest.writeInt(this.keyAuthType);
        dest.writeInt(this.authId);
        dest.writeInt(this.openLockCnt);
        dest.writeString(this.timeICtl);
        dest.writeLong(this.timeStart);
        dest.writeLong(this.timeEnd);
        dest.writeInt(this.openLockCntUsed);
        dest.writeInt(this.lockID);
    }

    protected DeviceKey(Parcel in) {
        this.deviceId = in.readInt();
        this.keyID = in.readInt();
        this.name = in.readString();
        this.keyType = in.readInt();
        this.keyCfg = in.readInt();
        this.keyInId = in.readInt();
        this.isAuthKey = in.readInt();
        this.keyStatus = in.readInt();
        this.keyAuthType = in.readInt();
        this.authId = in.readInt();
        this.openLockCnt = in.readInt();
        this.timeICtl = in.readString();
        this.timeStart = in.readLong();
        this.timeEnd = in.readLong();
        this.openLockCntUsed = in.readInt();
        this.lockID = in.readInt();
    }

    public static final Creator<DeviceKey> CREATOR = new Creator<DeviceKey>() {
        @Override
        public DeviceKey createFromParcel(Parcel source) {
            return new DeviceKey(source);
        }

        @Override
        public DeviceKey[] newArray(int size) {
            return new DeviceKey[size];
        }
    };
}
