package com.example.entity.data;

/**
 * author : zhouyubin
 * time   : 2018/07/17
 * desc   :
 * version: 1.0
 */
public class OperateLog {

    /**
     * mobile : 13534673710
     * lockMac : F0:F8:F2:D2:52:DF
     * lockName : 保险箱锁
     * operType : 0
     * createTime : 2018-07-17 14:01:28
     */

    private String mobile;
    private String username;
    private String lockMac;
    private String lockName;
    private int operType;
    private String lockType;
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

    public int getOperType() {
        return operType;
    }

    public void setOperType(int operType) {
        this.operType = operType;
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

    @Override
    public String toString() {
        return "OperateLog{" +
                "mobile='" + mobile + '\'' +
                ", username='" + username + '\'' +
                ", lockMac='" + lockMac + '\'' +
                ", lockName='" + lockName + '\'' +
                ", operType=" + operType +
                ", lockType='" + lockType + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
