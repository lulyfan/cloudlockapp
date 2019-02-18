package com.ut.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class LockUser {

    /**
     * createTime : 1545201549000
     * name : 老张
     * userId : 7
     * telNo : 13128571507
     */

    private long createTime;
    private String name;
    @PrimaryKey
    private long userId;
    private String telNo;
    private String headPic;

    private int keyStatus;

    @Ignore
    private String keyStatusStr;

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    public int getKeyStatus() {
        return keyStatus;
    }

    public void setKeyStatus(int keyStatus) {
        this.keyStatus = keyStatus;
    }

    public String getKeyStatusStr() {
        return keyStatusStr;
    }

    public void setKeyStatusStr(String keyStatusStr) {
        this.keyStatusStr = keyStatusStr;
    }
}
