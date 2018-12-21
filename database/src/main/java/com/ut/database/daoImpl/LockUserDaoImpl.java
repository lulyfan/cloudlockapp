package com.ut.database.daoImpl;

import android.arch.lifecycle.LiveData;

import com.ut.database.dao.LockUserDao;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.LockUser;

import java.util.List;

public class LockUserDaoImpl {
    private LockUserDao mLockUserDao;

    private LockUserDaoImpl() {
        mLockUserDao = CloudLockDatabaseHolder.get().getLockUserDao();
    }

    public static LockUserDaoImpl get() {
        return Holder.lockUserDao;
    }

    public LiveData<List<LockUser>> getAll() {
        return mLockUserDao.getAll();
    }

    public void insert(List<LockUser> lockUsers) {
        mLockUserDao.insert(lockUsers);
    }

    static class Holder {
        static LockUserDaoImpl lockUserDao = new LockUserDaoImpl();
    }
}
