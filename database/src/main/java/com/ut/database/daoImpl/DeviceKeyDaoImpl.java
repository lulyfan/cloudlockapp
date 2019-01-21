package com.ut.database.daoImpl;

import android.arch.lifecycle.LiveData;

import com.ut.database.dao.DeviceKeyDao;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.DeviceKey;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * author : zhouyubin
 * time   : 2019/01/09
 * desc   :
 * version: 1.0
 */
public class DeviceKeyDaoImpl implements DeviceKeyDao {
    private DeviceKeyDao mDeviceKeyDao;

    public static DeviceKeyDaoImpl get() {
        return Holder.instance;
    }

    private DeviceKeyDaoImpl() {
        mDeviceKeyDao = CloudLockDatabaseHolder.get().getDeviceKeyDao();
    }

    @Override
    public void insertDeviceKeys(DeviceKey... keys) {
        mDeviceKeyDao.insertDeviceKeys(keys);
    }

    @Override
    public void insertDeviceKeys(List<DeviceKey> keys) {
        mDeviceKeyDao.insertDeviceKeys(keys);
    }

    @Override
    public LiveData<List<DeviceKey>> findDeviceKeysByType(int lockId, int type) {
        return mDeviceKeyDao.findDeviceKeysByType(lockId, type);
    }

    @Override
    public LiveData<List<DeviceKey>> getAll() {
        return mDeviceKeyDao.getAll();
    }

    @Override
    public void deleteAll() {
        mDeviceKeyDao.deleteAll();
    }

    @Override
    public void deleteKeyByLockId(int lockId) {
        mDeviceKeyDao.deleteKeyByLockId(lockId);
    }

    @Override
    public void delete(DeviceKey... deviceKeys) {
        mDeviceKeyDao.delete(deviceKeys);
    }

    private static final class Holder {
        static final DeviceKeyDaoImpl instance = new DeviceKeyDaoImpl();
    }
}
