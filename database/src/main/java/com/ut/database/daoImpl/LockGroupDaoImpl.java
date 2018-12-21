package com.ut.database.daoImpl;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Delete;

import com.ut.database.dao.LockGroupDao;
import com.ut.database.dao.LockKeyDao;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.Lock;
import com.ut.database.entity.LockGroup;
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
public class LockGroupDaoImpl {

    private LockGroupDao mLockGroupDao;

    public static LockGroupDaoImpl get() {
        return Holder.lockGroupDao;
    }


    private LockGroupDaoImpl() {
        mLockGroupDao = CloudLockDatabaseHolder.get().getLockGroupDao();
    }

    /**
     * 获取所有钥匙
     *
     * @return
     */
    public LiveData<List<LockGroup>> getAllLockGroup() {
        return mLockGroupDao.getAll();
    }

    /**
     * 批量添加
     *
     * @param lockGroups
     */
    public void insertAll(LockGroup... lockGroups) {
        mLockGroupDao.insertAll(lockGroups);
    }


    /**
     * 单个添加
     *
     * @param lockGroup
     */
    public void insert(LockGroup lockGroup) {
        mLockGroupDao.insert(lockGroup);
    }


    /**
     * @param lockGroup
     */
    public void delete(LockGroup lockGroup) {
        mLockGroupDao.delete(lockGroup);
    }

    /**
     * @param lockGroup
     */
    public void update(LockGroup lockGroup) {
        mLockGroupDao.updata(lockGroup);
    }

    private static class Holder {
        protected static LockGroupDaoImpl lockGroupDao = new LockGroupDaoImpl();
    }
}
