package com.ut.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Lock {

    /**
     * modelNum :
     * electric : 100
     * latitude :
     * blueKey :
     * type : 1
     * mac : 33-33-22-A1-B0-66
     * adminPwd :
     * createTime : 1544596624000
     * name : LS测试锁
     * lockVersion :
     * id : 12
     * userType : 1
     * initTime : 1544596511000
     * status : 0
     * longitude :
     */

    private String modelNum;
    private int electric;
    private String latitude;
    private String blueKey;
    private int type;
    private String mac;
    private String adminPwd;
    private long createTime;
    private String name;
    private String lockVersion;
    @PrimaryKey
    private int id;
    private int userType;
    private long initTime;
    private String status;
    private String longitude;

    public String getModelNum() {
        return modelNum;
    }

    public void setModelNum(String modelNum) {
        this.modelNum = modelNum;
    }

    public int getElectric() {
        return electric;
    }

    public void setElectric(int electric) {
        this.electric = electric;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getBlueKey() {
        return blueKey;
    }

    public void setBlueKey(String blueKey) {
        this.blueKey = blueKey;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getAdminPwd() {
        return adminPwd;
    }

    public void setAdminPwd(String adminPwd) {
        this.adminPwd = adminPwd;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLockVersion() {
        return lockVersion;
    }

    public void setLockVersion(String lockVersion) {
        this.lockVersion = lockVersion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public long getInitTime() {
        return initTime;
    }

    public void setInitTime(long initTime) {
        this.initTime = initTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
