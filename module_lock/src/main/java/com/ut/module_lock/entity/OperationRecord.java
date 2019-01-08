package com.ut.module_lock.entity;

import com.ut.database.entity.Record;

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
}
