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

    public void insertAll(List<LockKey> lockKeys) {
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
    public LiveData<List<LockKey>> getLockByGroupId(long groupId) {
        return mLockKeyDao.getLockByGroupId(groupId);
    }


    /**
     * 根据锁名称获取锁
     *
     * @param name
     * @return
     */
    public LiveData<List<LockKey>> getLockByName(String name) {
        return mLockKeyDao.getLockByName(name+"%");
    }

    public LiveData<LockKey> getLockByMac(String mac) {
        return mLockKeyDao.getByMac(mac);
    }

    public void deleteByMac(String mac) {
        mLockKeyDao.deleteByMac(mac);
    }

    public void deleteByKeyId(int keyId) {
        mLockKeyDao.deleteByKeyId(keyId);
    }

    public LiveData<List<LockKey>> getAdminLock() {
        return mLockKeyDao.getAdminLock();
    }

    /**
     * 清除所有锁数据
     */
    public void deleteAll() {
        mLockKeyDao.deleteAll();
    }

    public void updateKeyStatus(int keyId, int keyStatus) {
        mLockKeyDao.updateKeyStatus(keyId, keyStatus);
    }

    public void updateKeyAuth(int keyId, int userType) {
        mLockKeyDao.updateKeyAuth(keyId, userType);
    }

    public void updateLockName(int lockId, String lockName) {
        mLockKeyDao.updateLockName(lockId, lockName);
    }

    private static class Holder {
        protected static LockKeyDaoImpl lockKeyDao = new LockKeyDaoImpl();
    }
}
