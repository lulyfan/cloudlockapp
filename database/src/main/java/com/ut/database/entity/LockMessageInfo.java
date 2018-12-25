package com.ut.database.entity;

import android.arch.persistence.room.Entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * author : chenjiajun
 * time   : 2018/12/25
 * desc   :
 */
@Entity
public class LockMessageInfo {
    private long id;// 6,
    private String name;// "4",
    private String description;// "4",
    private long createTime;// 1544595271000,
    private int type;// 1,
    private String lockMac;// "123",
    private long meetingRomeId;// 1

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLockMac() {
        return lockMac;
    }

    public void setLockMac(String lockMac) {
        this.lockMac = lockMac;
    }

    public long getMeetingRomeId() {
        return meetingRomeId;
    }

    public void setMeetingRomeId(long meetingRomeId) {
        this.meetingRomeId = meetingRomeId;
    }

    public String createTimeformat(){
        return new SimpleDateFormat("yyyy/MM/dd  hh:mm", Locale.CHINA).format(new Date(createTime));
    }
}
