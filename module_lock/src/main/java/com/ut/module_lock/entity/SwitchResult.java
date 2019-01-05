package com.ut.module_lock.entity;

/**
 * author : zhouyubin
 * time   : 2019/01/04
 * desc   :
 * version: 1.0
 */
public class SwitchResult {
    public boolean oldVar;
    public boolean result;

    public SwitchResult(boolean oldVar, boolean result) {
        this.oldVar = oldVar;
        this.result = result;
    }
}
