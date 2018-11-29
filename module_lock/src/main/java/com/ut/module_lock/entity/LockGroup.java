package com.ut.module_lock.entity;

/**
 * author : zhouyubin
 * time   : 2018/11/29
 * desc   :
 * version: 1.0
 */
public class LockGroup {
    private String name;
    private int type;//0为当前组，1为非当前组

    public LockGroup(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
