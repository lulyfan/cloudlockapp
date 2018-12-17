package com.ut.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * author : chenjiajun
 * time   : 2018/12/14
 * desc   :
 */
@Entity
public class UUID {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String uuid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
