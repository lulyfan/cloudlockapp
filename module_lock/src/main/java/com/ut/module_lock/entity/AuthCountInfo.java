package com.ut.module_lock.entity;

public class AuthCountInfo {
    private int authId;
    private int authCount;      //授权开锁次数
    private int openLockCount;  //已开锁次数

    public AuthCountInfo(int authId, int authCount, int openLockCount) {
        this.authId = authId;
        this.authCount = authCount;
        this.openLockCount = openLockCount;
    }

    public int getAuthId() {
        return authId;
    }

    public void setAuthId(int authId) {
        this.authId = authId;
    }

    public int getAuthCount() {
        return authCount;
    }

    public void setAuthCount(int authCount) {
        this.authCount = authCount;
    }

    public int getOpenLockCount() {
        return openLockCount;
    }

    public void setOpenLockCount(int openLockCount) {
        this.openLockCount = openLockCount;
    }

    @Override
    public String toString() {
        return "authId:" + authId + " 开锁限制次数:" + authCount + " 已开锁次数:" + openLockCount;
    }
}
