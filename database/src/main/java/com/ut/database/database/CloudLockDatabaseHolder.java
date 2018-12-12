package com.ut.database.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.ut.database.dao.LockKeyDao;
import com.ut.database.dao.UserDao;

/**
 * author : zhouyubin
 * time   : 2018/12/04
 * desc   :
 * version: 1.0
 */
public class CloudLockDatabaseHolder {
    private static final String DBNAME = "cloudLock_database";
    private CloudLockRoomDatabase mDb = null;

    public static CloudLockDatabaseHolder get() {
        return Holder.INSTANCE;
    }

    public void init(Context context) {
        mDb = Room.databaseBuilder(context.getApplicationContext(),
                CloudLockRoomDatabase.class, DBNAME)
                .fallbackToDestructiveMigration()
                .addCallback(mRoomDatabaseCallback)
                .build();
    }

    public LockKeyDao getLockKeyDao() {
        checkdb();
        return mDb.mLockKeyDao;
    }

    private void checkdb() {
        if (mDb == null) {
            throw new RuntimeException("Please initialize the database first!");
        }
    }

    public UserDao getUserDao(){
        return mDb.userDao();
    }

    private static RoomDatabase.Callback mRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

        }
    };

    private static class Holder {
        protected static CloudLockDatabaseHolder INSTANCE = new CloudLockDatabaseHolder();
    }

}