package com.ut.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.ut.database.utils.DeviceKeyUtil;

/**
 * author : zhouyubin
 * time   : 2019/01/09
 * desc   :
 * version: 1.0
 */
public class DeviceKeyAuth implements Parcelable {
    private int authId;     //授权编号
    private int keyID;         //钥匙ID
    private int openLockCnt;  //开锁次数   预留
    private String timeICtl;         //时间控制,控制星期几有效
    private long timeStart;     //开始时间
    private long timeEnd;         //结束时间

    private int openLockCntUsed;//已开锁次数

    public DeviceKeyAuth() {
    }

    public DeviceKeyAuth(int authId, int keyID, int openLockCnt, long timeStart, long timeEnd) {
        this.authId = authId;
        this.keyID = keyID;
        this.openLockCnt = openLockCnt;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public int getAuthId() {
        return authId;
    }

    public void setAuthId(int authId) {
        this.authId = authId;
    }

    public int getKeyID() {
        return keyID;
    }

    public void setKeyID(int keyID) {
        this.keyID = keyID;
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

    public void setTimeICtl(String timeICtl) {
        this.timeICtl = timeICtl;
    }

    public void setTimeICtl(int[] timeICtl) {
        StringBuilder stringBuilder = new StringBuilder();
        if (timeICtl != null) {
            for (int i = 0; i < timeICtl.length; i++) {
                if (i != 0) {
                    stringBuilder.append(",");
                }
                stringBuilder.append(timeICtl[i]);
            }
            this.timeICtl = stringBuilder.toString();
        } else {
            this.timeICtl = "";
        }
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

    public Boolean[] getWeekAuthData() {
        return DeviceKeyUtil.getWeekAuthData(this.timeICtl);
    }

    public int getOpenLockCntUsed() {
        return openLockCntUsed;
    }

    public void setOpenLockCntUsed(int openLockCntUsed) {
        this.openLockCntUsed = openLockCntUsed;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.authId);
        dest.writeInt(this.keyID);
        dest.writeInt(this.openLockCnt);
        dest.writeString(this.timeICtl);
        dest.writeLong(this.timeStart);
        dest.writeLong(this.timeEnd);
        dest.writeInt(this.openLockCntUsed);
    }

    protected DeviceKeyAuth(Parcel in) {
        this.authId = in.readInt();
        this.keyID = in.readInt();
        this.openLockCnt = in.readInt();
        this.timeICtl = in.readString();
        this.timeStart = in.readLong();
        this.timeEnd = in.readLong();
        this.openLockCntUsed = in.readInt();
    }

    public static final Parcelable.Creator<DeviceKeyAuth> CREATOR = new Parcelable.Creator<DeviceKeyAuth>() {
        @Override
        public DeviceKeyAuth createFromParcel(Parcel source) {
            return new DeviceKeyAuth(source);
        }

        @Override
        public DeviceKeyAuth[] newArray(int size) {
            return new DeviceKeyAuth[size];
        }
    };
}
