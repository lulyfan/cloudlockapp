package com.ut.module_mine;

public class GlobalData {

    private static GlobalData instance = new GlobalData();

    public static GlobalData getInstance() {
        return instance;
    }

    private GlobalData() {

    }

    public String changeLockMacs;
    public String receiverPhone;
    public int changeLockCounts;
}
