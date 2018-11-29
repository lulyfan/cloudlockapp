package com.ut.module_lock.entity;

/**
 * author : zhouyubin
 * time   : 2018/11/28
 * desc   :
 * version: 1.0
 */
public class BleDevice {
    private String name;
    private boolean isActive;

    public BleDevice(String name, boolean isActive) {
        this.name = name;
        this.isActive = isActive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
