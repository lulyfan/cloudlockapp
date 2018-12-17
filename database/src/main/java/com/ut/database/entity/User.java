package com.ut.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * author : chenjiajun
 * time   : 2018/12/10
 * desc   :
 */

@Entity(tableName = "user")
public class User {
    @PrimaryKey()
    @NonNull
    public long id; //1,
    public String account; //"admin",
    public String name; //"111",
    public String email; //"222",
    public String telNo; //"13680358940",
    public String createTime; //1542253076000,
    public int status; //0,
    public int sex; //1,
    public String headPic; //null,
    public int enableWebLogin; //1,
    public int enableSound; //1,
    public int enableAutoOpen; //1

    @NonNull
    public long getId() {
        return id;
    }

    public void setId(@NonNull long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public int isEnableWebLogin() {
        return enableWebLogin;
    }

    public void setEnableWebLogin(int enableWebLogin) {
        this.enableWebLogin = enableWebLogin;
    }

    public int isEnableSound() {
        return enableSound;
    }

    public void setEnableSound(int enableSound) {
        this.enableSound = enableSound;
    }

    public int isEnableAutoOpen() {
        return enableAutoOpen;
    }

    public void setEnableAutoOpen(int enableAutoOpen) {
        this.enableAutoOpen = enableAutoOpen;
    }
}
