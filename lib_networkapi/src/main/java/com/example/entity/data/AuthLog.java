package com.example.entity.data;

/**
 * author : zhouyubin
 * time   : 2018/07/17
 * desc   :
 * version: 1.0
 */
public class AuthLog {

    /**
     * mobile : 13534673710
     * lockMac : A4:34:F1:7A:BE:05
     * lockName : 我
     * authType : 1
     * createTime : 2018-07-16 10:29:22
     */

    private String mobile;
    private String username;
    private String lockMac;
    private String lockName;
    private int authType;
    private String lockType;
    private String startTime;
    private String endTime;
    private String createTime;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLockMac() {
        return lockMac;
    }

    public void setLockMac(String lockMac) {
        this.lockMac = lockMac;
    }

    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    public int getAuthType() {
        return authType;
    }

    public void setAuthType(int authType) {
        this.authType = authType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLockType() {
        return lockType;
    }

    public void setLockType(String lockType) {
        this.lockType = lockType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
