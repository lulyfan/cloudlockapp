package com.ut.database.entity;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class LockUserKey {

    /**
     * ruleType : 1
     * keyId : 18
     * lockName : 锁3333333333
     * mac : 33-33-22-A1-B0-66
     */

    private int ruleType;
    @PrimaryKey
    private int keyId;
    private String lockName;
    private String mac;

    public int getRuleType() {
        return ruleType;
    }

    public void setRuleType(int ruleType) {
        this.ruleType = ruleType;
    }

    public int getKeyId() {
        return keyId;
    }

    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }

    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
