package com.ut.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import java.io.Serializable;

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
    private String lockType;// 锁类型,
    private String status;// 处理状态,
    private String lockName;// 锁名称,
    private long applyTime;// 申请时间
    private String reason;
    private String headPic;// "http://cloudlockbuss.oss-cn-shenzhen.aliyuncs.com/img/f11ddf63f16a48d9bd17f8f07ba8f7c7",
    private String mobile;//申请人电话
    private int ruleType;
    private String mac;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getKeyType() {
        return keyType;
    }

    public void setKeyType(int keyType) {
        this.keyType = keyType;
    }

    public String getLockType() {
        return lockType;
    }

    public void setLockType(String lockType) {
        this.lockType = lockType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getRuleType() {
        return ruleType;
    }

    public void setRuleType(int ruleType) {
        this.ruleType = ruleType;
    }

    @Ignore
    private String applyTimeStr;

    @Ignore
    private String ruleTypeStr;

    @Ignore
    private String decStr;

    public String getApplyTimeStr() {
        return applyTimeStr;
    }

    public void setApplyTimeStr(String applyTimeStr) {
        this.applyTimeStr = applyTimeStr;
    }

    public String getRuleTypeStr() {
        return ruleTypeStr;
    }

    public void setRuleTypeStr(String ruleTypeStr) {
        this.ruleTypeStr = ruleTypeStr;
    }

    public String getDecStr() {
        return decStr;
    }

    public void setDecStr(String decStr) {
        this.decStr = decStr;
    }
}
