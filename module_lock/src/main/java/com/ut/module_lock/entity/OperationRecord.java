package com.ut.module_lock.entity;

import java.io.Serializable;
import java.util.List;

/**
 * author : chenjiajun
 * time   : 2018/12/3
 * desc   :
 */
public class OperationRecord implements Serializable {

    private String time;
    private List<Record> records;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    public static class Record {
        private String icon;
        private String operator;
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

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }
}
