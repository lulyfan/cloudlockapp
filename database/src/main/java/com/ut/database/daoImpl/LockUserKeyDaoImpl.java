package com.ut.database.daoImpl;

import android.arch.lifecycle.LiveData;

import com.ut.database.dao.LockUserKeyDao;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.LockUserKey;

import java.util.List;

public class LockUserKeyDaoImpl {

    private LockUserKeyDao mLockUserKeyDao;

    public static LockUserKeyDaoImpl get() {
        return Holder.lockUserKeyDao;
    }

    private LockUserKeyDaoImpl() {
        mLockUserKeyDao = CloudLockDatabaseHolder.get().getLockUserKeyDao();
    }

    static class Holder {
        static LockUserKeyDaoImpl lockUserKeyDao = new LockUserKeyDaoImpl();
    }

    public LiveData<List<LockUserKey>> getAll() {
        return mLockUserKeyDao.getAll();
    }

    public void insert(List<LockUserKey> lockUserKeys) {
        mLockUserKeyDao.insert(lockUserKeys);
    }

    public void deleteById(int keyId) {
        LockUserKey lockUserKey = mLockUserKeyDao.getById(keyId);
        mLockUserKeyDao.delete(lockUserKey);
    }
}
