package com.ut.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Record {

    @PrimaryKey(autoGenerate = true)
    private long id;//1,
    private String icon;
    private String operator;
    private long userId;//1,
    private long lockId;//0,
    private long keyId;//0,
    private int type;//0,
    private String keyName;//钥匙名称
    private String description;//"开锁啦",
    private long createTime;//1542937352000
    private String time;
    private String userName;
    private String headPic;
    private String date;

    private int openLockType; //手机开锁、无感开锁、指纹开锁、IC卡开锁、电子钥匙开锁、密码开锁


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getLockId() {
        return lockId;
    }

    public void setLockId(long lockId) {
        this.lockId = lockId;
    }

    public long getKeyId() {
        return keyId;
    }

    public void setKeyId(long keyId) {
        this.keyId = keyId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public int getOpenLockType() {
        return openLockType;
    }

    public void setOpenLockType(int openLockType) {
        this.openLockType = openLockType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Record record = (Record) o;
        return lockId == record.lockId &&
                keyId == record.keyId &&
                type == record.type &&
                createTime == record.createTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lockId, keyId, type, createTime);
    }
}