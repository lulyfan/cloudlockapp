package com.ut.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.DebugUtils;

import com.ut.database.utils.DeviceKeyUtil;

/**
 * author : zhouyubin
 * time   : 2019/01/09
 * desc   :
 * version: 1.0
 */
@Entity(tableName = "device_key")
public class DeviceKey implements Parcelable {
    @PrimaryKey
    private int keyID;//钥匙编号；
    private String name = "";//名字
    /**
     * {@link com.ut.database.entity.EnumCollection.DeviceKeyType}
     */
    private int keyType;//钥匙类型：指纹 卡片 密码 电子钥匙 手机蓝牙；
    private int keyCfg;//钥匙配置属性；
    private int keyInId;//钥匙内部编号

    /**
     * {@link com.ut.database.entity.EnumCollection.DeviceKeyStatus}
     */
    private int keyStatus;//0:正常;1:已过期;2:已失效;3:已冻结

    private int keyAuthType;//0:永久;1：限时;2:循环

    @Ignore
    private DeviceKeyAuth mDeviceKeyAuthData;

    public DeviceKey(int keyID, String name, int keyType, int keyCfg, int keyInId) {
        this.keyID = keyID;
        this.name = name;
        this.keyType = keyType;
        this.keyCfg = keyCfg;
        this.keyInId = keyInId;
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

    public DeviceKeyAuth getDeviceKeyAuthData() {
        return mDeviceKeyAuthData;
    }

    public int getKeyAuthType() {
        return keyAuthType;
    }

    public void setKeyAuthType(int keyAuthType) {
        this.keyAuthType = keyAuthType;
    }

    public void setDeviceKeyAuthData(DeviceKeyAuth deviceKeyAuthData) {
        mDeviceKeyAuthData = deviceKeyAuthData;
        this.keyAuthType = DeviceKeyUtil.getKeyAuthType(mDeviceKeyAuthData);
        this.keyStatus = DeviceKeyUtil.getKeyStatus(mDeviceKeyAuthData);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.keyID);
        dest.writeString(this.name);
        dest.writeInt(this.keyType);
        dest.writeInt(this.keyCfg);
        dest.writeInt(this.keyInId);
        dest.writeInt(this.keyStatus);
        dest.writeInt(this.keyAuthType);
        dest.writeParcelable(this.mDeviceKeyAuthData, flags);
    }

    protected DeviceKey(Parcel in) {
        this.keyID = in.readInt();
        this.name = in.readString();
        this.keyType = in.readInt();
        this.keyCfg = in.readInt();
        this.keyInId = in.readInt();
        this.keyStatus = in.readInt();
        this.keyAuthType = in.readInt();
        this.mDeviceKeyAuthData = in.readParcelable(DeviceKeyAuth.class.getClassLoader());
    }

    public static final Parcelable.Creator<DeviceKey> CREATOR = new Parcelable.Creator<DeviceKey>() {
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
