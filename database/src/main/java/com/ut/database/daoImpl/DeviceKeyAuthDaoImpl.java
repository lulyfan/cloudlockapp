package com.ut.database.daoImpl;

import android.arch.lifecycle.LiveData;

import com.ut.database.dao.DeviceKeyAuthDao;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.DeviceKeyAuth;

import java.util.List;

/**
 * author : zhouyubin
 * time   : 2019/01/09
 * desc   :
 * version: 1.0
 */
public class DeviceKeyAuthDaoImpl implements DeviceKeyAuthDao {
    private DeviceKeyAuthDao mDeviceKeyAuthDao;

    public static DeviceKeyAuthDaoImpl get() {
        return Holder.instance;
    }

    private DeviceKeyAuthDaoImpl() {
        mDeviceKeyAuthDao = CloudLockDatabaseHolder.get().getDeviceKeyAuthDao();
    }


    @Override
    public void insertDeviceKeyAuths(DeviceKeyAuth... keys) {
        mDeviceKeyAuthDao.insertDeviceKeyAuths(keys);
    }

    @Override
    public void insertDeviceKeyAuths(List<DeviceKeyAuth> keys) {
        mDeviceKeyAuthDao.insertDeviceKeyAuths(keys);
    }

    @Override
    public LiveData<List<DeviceKeyAuth>> getAll() {
        return mDeviceKeyAuthDao.getAll();
    }

    @Override
    public void deleteAll() {
        mDeviceKeyAuthDao.deleteAll();
    }

    private static final class Holder {
        static final DeviceKeyAuthDaoImpl instance = new DeviceKeyAuthDaoImpl();
    }
}
