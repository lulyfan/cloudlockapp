package com.ut.module_msg.model;

/**
 * author : chenjiajun
 * time   : 2018/11/28
 * desc   :
 */
public class OperationRecord {

    private String icon;
    private String operator;
    private long date;
    private String desc;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
