package com.ut.module_msg.model;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.ut.base.BaseApplication;
import com.ut.module_msg.R;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * author : chenjiajun
 * time   : 2018/11/30
 * desc   :
 */
@Entity
public class ApplyMessage implements Serializable {
    @PrimaryKey()
    private int id;// 申请记录ID,
    private String url;
    private String hint;
    private String userName;
    private int keyType;
    private int lockType;// 锁类型,
    private String status;// 处理状态,
    private String lockName;// 锁名称,
    private long applyTime;// 申请时间
    private String reason;
    private String headPic;// "http://cloudlockbuss.oss-cn-shenzhen.aliyuncs.com/img/f11ddf63f16a48d9bd17f8f07ba8f7c7",


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public int getKeyType() {
        return keyType;
    }

    public void setKeyType(int keyType) {
        this.keyType = keyType;
    }

    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLockType() {
        return lockType;
    }

    public void setLockType(int lockType) {
        this.lockType = lockType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    public long getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(long applyTime) {
        this.applyTime = applyTime;
    }

    public String applyTimeString() {
        return new SimpleDateFormat("yyyy/MM/dd  HH:mm", Locale.getDefault()).format(new Date(getApplyTime()));
    }

    public String lockTypeString() {
        switch (getLockType()) {
            case 3:
                return BaseApplication.getAppContext().getString(R.string.once_time);
            case 2:
                return BaseApplication.getAppContext().getString(R.string.limit_time);
            case 4:
                return BaseApplication.getAppContext().getString(R.string.loop);
            case 1:
                return BaseApplication.getAppContext().getString(R.string.permanent);
        }
        return "";
    }

    @SuppressLint("StringFormatMatches")
    public String description() {
        String string = BaseApplication.getAppContext().getString(R.string.apply_desc);
        return String.format(string, lockTypeString(), lockName);
    }
}
