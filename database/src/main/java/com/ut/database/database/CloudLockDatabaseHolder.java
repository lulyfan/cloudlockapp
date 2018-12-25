package com.ut.database.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.ut.database.dao.LockGroupDao;
import com.ut.database.dao.LockKeyDao;
import com.ut.database.dao.LockMessageDao;
import com.ut.database.dao.LockUserDao;
import com.ut.database.dao.LockUserKeyDao;
import com.ut.database.dao.NotifyDao;
import com.ut.database.dao.SearchRecordDao;
import com.ut.database.dao.UUIDDao;
import com.ut.database.dao.UserDao;

/**
 * author : zhouyubin
 * time   : 2018/12/04
 * desc   :
 * version: 1.0
 */
public class CloudLockDatabaseHolder {
    private static final String DBNAME = "cloudLock_database.db";
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
        return mDb.lockKeyDao();
    }

    public UserDao getUserDao() {
        return mDb.userDao();
    }

    public UUIDDao getUUIDDao() {
        return mDb.uuidDao();
    }

    public LockMessageDao lockMessageDao() {
        return mDb.lockMessageDao();
    }

    public LockUserDao getLockUserDao() {
        return mDb.lockUserDao();
    }

    public LockUserKeyDao getLockUserKeyDao() {
        return mDb.lockUserKeyDao();
    }

    public LockGroupDao getLockGroupDao() {
        return mDb.lockGroupDao();
    }

    public SearchRecordDao getSearchRecordDao() {
        return mDb.searchRecordDao();
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
