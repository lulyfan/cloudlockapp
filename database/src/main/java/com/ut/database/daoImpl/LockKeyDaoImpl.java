package com.ut.database.daoImpl;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.ut.database.dao.LockKeyDao;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.LockKey;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * author : zhouyubin
 * time   : 2018/12/04
 * desc   :
 * version: 1.0
 */
public class LockKeyDaoImpl {

    private LockKeyDao mLockKeyDao;
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

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

    /**
     * 批量添加
     *
     * @param lockKeys
     */
    public void insertAll(LockKey... lockKeys) {
        mLockKeyDao.insertAll(lockKeys);
    }

    /**
     * 单个添加
     *
     * @param lockKey
     */
    public void insert(LockKey lockKey) {
        mLockKeyDao.insert(lockKey);
    }

    /**
     * 根据锁组id获取锁
     *
     * @param groupId
     * @return
     */
    public LiveData<List<LockKey>> getLockByGroupId(int groupId) {
        return mLockKeyDao.getLockByGroupId(groupId);
    }


    /**
     * 根据锁名称获取锁
     *
     * @param name
     * @return
     */
    public LiveData<List<LockKey>> getLockByName(String name) {
        return mLockKeyDao.getLockByName(name);
    }

    private static class Holder {
        protected static LockKeyDaoImpl lockKeyDao = new LockKeyDaoImpl();
    }
}
