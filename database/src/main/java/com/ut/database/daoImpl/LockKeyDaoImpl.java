package com.ut.database.daoImpl;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.ut.database.dao.LockKeyDao;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.LockKey;

import java.util.List;

/**
 * author : zhouyubin
 * time   : 2018/12/04
 * desc   :
 * version: 1.0
 */
public class LockKeyDaoImpl {

    private LockKeyDao mLockKeyDao;

    public static LockKeyDaoImpl get() {
        return Holder.lockKeyDao;
    }


    private LockKeyDaoImpl() {
        mLockKeyDao = CloudLockDatabaseHolder.get().getLockKeyDao();
    }

    /**
     * 获取所有钥匙
     *
     * @return
     */
    public LiveData<List<LockKey>> getAllLockKey() {
        return mLockKeyDao.getAll();
    }

    public void insert(LockKey... lockKeys) {
        
    }


    private static class Holder {
        protected static LockKeyDaoImpl lockKeyDao = new LockKeyDaoImpl();
    }
}
