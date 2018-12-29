package com.ut.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * author : chenjiajun
 * time   : 2018/12/25
 * desc   :
 */

@Entity
public class LockMessage implements Serializable {
    @NonNull
    @PrimaryKey
    private String lockMac;// "123",
    private long id;// 4,
    private String name;// "4",
    private String lockName;// "蓝牙锁",
    private String description;// "4",
    private int type;// 1,
    private int unReadCount;// 3,
    private long createTime;// 1544595271000


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLockMac() {
        return lockMac;
    }

    public void setLockMac(String lockMac) {
        this.lockMac = lockMac;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String createTimeformat() {
        return new SimpleDateFormat("yyyy/MM/dd  hh:mm", Locale.CHINA).format(new Date(createTime));
    }
}
