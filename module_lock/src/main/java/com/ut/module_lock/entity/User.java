package com.ut.module_lock.entity;

/**
 * author : zhouyubin
 * time   : 2018/11/27
 * desc   :
 * version: 1.0
 */
public class User {
    private String account;

    public User(String account) {
        this.account = account;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
