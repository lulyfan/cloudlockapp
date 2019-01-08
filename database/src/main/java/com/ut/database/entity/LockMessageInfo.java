package com.ut.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * author : chenjiajun
 * time   : 2018/12/25
 * desc   :
 */
@Entity
public class LockMessageInfo {
    @PrimaryKey
    private long id;// 422,
    private String name;// "锁相关通知",
    private String description;// "您的🎃🎃🎃电子钥匙已被删除",
    private long createTime;// 1546063528000,
    private int type;// 0,
    private String lockMac;// "33-33-22-A1-B0-60",
    private long meetingRomeId;// 0

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

    public String createTimeFormat() {
        if (isToday(createTime)) {
            return new SimpleDateFormat("hh:mm", Locale.getDefault()).format(new Date(createTime));
        }

        return new SimpleDateFormat("yyyy/MM/dd  hh:mm", Locale.getDefault()).format(new Date(createTime));
    }

    private boolean isToday(long time) {
        Calendar today = Calendar.getInstance();
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(time);
        return today.get(Calendar.YEAR) == date.get(Calendar.YEAR)
                && today.get(Calendar.MONTH) == date.get(Calendar.MONTH)
                && today.get(Calendar.DATE) == date.get(Calendar.DATE);
    }
}
