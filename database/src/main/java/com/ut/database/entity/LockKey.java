package com.ut.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * author : zhouyubin
 * time   : 2018/12/04
 * desc   :
 * version: 1.0
 */
@Entity(tableName = "lock_key")
public class LockKey {
    @PrimaryKey
    @NonNull
    public String id;
    public String name;
    public int status;
    public int lockType;
    public int keyType;
    public int userType;
    public int electricity;


}
