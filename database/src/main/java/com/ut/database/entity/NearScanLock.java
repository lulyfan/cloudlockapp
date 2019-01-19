package com.ut.database.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * author : zhouyubin
 * time   : 2018/12/19
 * desc   :
 * version: 1.0
 */
public class NearScanLock implements Parcelable {

    /**
     * keyStatus : 8
     * name : LS测试锁
     * id : 12
     * type : 1
     * mac : 33-33-22-A1-B0-66
     * bindStatus : 1
     * status : 0
     * lockEnterpriseId : 1
     */
    /**
     * {@link EnumCollection.KeyStatus}
     */
    private int keyStatus;
    private String name;
    private int id;
    private int type;
    private String mac;
    private int bindStatus;//0:未有人绑定  1：当前用户绑定 2：其他用户已绑定
    private String status;
    private int lockEnterpriseId;//锁企id

    private String flowNo;

    public int getKeyStatus() {
        return keyStatus;
    }

    public void setKeyStatus(int keyStatus) {
        this.keyStatus = keyStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getBindStatus() {
        return bindStatus;
    }

    public void setBindStatus(int bindStatus) {
        this.bindStatus = bindStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getLockEnterpriseId() {
        return lockEnterpriseId;
    }

    public void setLockEnterpriseId(int lockEnterpriseId) {
        this.lockEnterpriseId = lockEnterpriseId;
    }

    public String getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(String flowNo) {
        this.flowNo = flowNo;
    }

    public static Creator<NearScanLock> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.keyStatus);
        dest.writeString(this.name);
        dest.writeInt(this.id);
        dest.writeInt(this.type);
        dest.writeString(this.mac);
        dest.writeInt(this.bindStatus);
        dest.writeString(this.status);
        dest.writeInt(this.lockEnterpriseId);
        dest.writeString(this.flowNo);
    }

    public NearScanLock() {
    }

    protected NearScanLock(Parcel in) {
        this.keyStatus = in.readInt();
        this.name = in.readString();
        this.id = in.readInt();
        this.type = in.readInt();
        this.mac = in.readString();
        this.bindStatus = in.readInt();
        this.status = in.readString();
        this.lockEnterpriseId = in.readInt();
        this.flowNo = in.readString();
    }

    public static final Parcelable.Creator<NearScanLock> CREATOR = new Parcelable.Creator<NearScanLock>() {
        @Override
        public NearScanLock createFromParcel(Parcel source) {
            return new NearScanLock(source);
        }

        @Override
        public NearScanLock[] newArray(int size) {
            return new NearScanLock[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NearScanLock that = (NearScanLock) o;
        return Objects.equals(mac, that.mac);
    }

    @Override
    public int hashCode() {

        return Objects.hash(mac);
    }
}
