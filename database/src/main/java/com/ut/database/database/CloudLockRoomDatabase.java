package com.ut.database.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.ut.database.dao.LockKeyDao;
import com.ut.database.entity.LockKey;

/**
 * author : zhouyubin
 * time   : 2018/12/04
 * desc   :
 * version: 1.0
 */
@Database(entities = {LockKey.class}, version = 1)
public abstract class CloudLockRoomDatabase extends RoomDatabase {

    public LockKeyDao mLockKeyDao;


}
