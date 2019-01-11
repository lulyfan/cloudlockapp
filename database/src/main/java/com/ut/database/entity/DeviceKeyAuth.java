package com.ut.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.ut.database.utils.DeviceKeyUtil;

/**
 * author : zhouyubin
 * time   : 2019/01/09
 * desc   :
 * version: 1.0
 */
@Entity(tableName = "device_key_auth")
public class DeviceKeyAuth implements Parcelable {
    @PrimaryKey
    private int AuthId;     //授权编号
    private int KeyID;         //钥匙ID
    private int OpenLockCnt;  //开锁次数   预留
    private String TimeICtl;         //时间控制,控制星期几有效
    private long TimeStart;     //开始时间
    private long TimeEnd;         //结束时间

    private int OpenLockCntUsed;//已开锁次数

    public DeviceKeyAuth() {
    }

    public DeviceKeyAuth(int authId, int keyID, int openLockCnt, String timeICtl, long timeStart, long timeEnd, int openLockCntUsed) {
        AuthId = authId;
        KeyID = keyID;
        OpenLockCnt = openLockCnt;
        TimeICtl = timeICtl;
        TimeStart = timeStart;
        TimeEnd = timeEnd;
        OpenLockCntUsed = openLockCntUsed;
    }

    public int getAuthId() {
        return AuthId;
    }

    public void setAuthId(int authId) {
        AuthId = authId;
    }

    public int getKeyID() {
        return KeyID;
    }

    public void setKeyID(int keyID) {
        KeyID = keyID;
    }

    public int getOpenLockCnt() {
        return OpenLockCnt;
    }

    public void setOpenLockCnt(int openLockCnt) {
        OpenLockCnt = openLockCnt;
    }

    public String getTimeICtl() {
        return TimeICtl;
    }

    public void setTimeICtl(String timeICtl) {
        this.TimeICtl = timeICtl;
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
            this.TimeICtl = stringBuilder.toString();
        } else {
            this.TimeICtl = "";
        }
    }

    public long getTimeStart() {
        return TimeStart;
    }

    public void setTimeStart(long timeStart) {
        TimeStart = timeStart;
    }

    public long getTimeEnd() {
        return TimeEnd;
    }

    public void setTimeEnd(long timeEnd) {
        TimeEnd = timeEnd;
    }

    public Boolean[] getWeekAuthData() {
        return DeviceKeyUtil.getWeekAuthData(this.TimeICtl);
    }

    public int getOpenLockCntUsed() {
        return OpenLockCntUsed;
    }

    public void setOpenLockCntUsed(int openLockCntUsed) {
        OpenLockCntUsed = openLockCntUsed;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.AuthId);
        dest.writeInt(this.KeyID);
        dest.writeInt(this.OpenLockCnt);
        dest.writeString(this.TimeICtl);
        dest.writeLong(this.TimeStart);
        dest.writeLong(this.TimeEnd);
        dest.writeInt(this.OpenLockCntUsed);
    }

    protected DeviceKeyAuth(Parcel in) {
        this.AuthId = in.readInt();
        this.KeyID = in.readInt();
        this.OpenLockCnt = in.readInt();
        this.TimeICtl = in.readString();
        this.TimeStart = in.readLong();
        this.TimeEnd = in.readLong();
        this.OpenLockCntUsed = in.readInt();
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
