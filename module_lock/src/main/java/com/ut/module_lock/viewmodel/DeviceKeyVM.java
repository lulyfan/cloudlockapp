package com.ut.module_lock.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;

import com.ut.database.daoImpl.DeviceKeyDaoImpl;
import com.ut.database.entity.DeviceKey;
import com.ut.database.entity.DeviceKeyAuth;
import com.ut.database.entity.EnumCollection;
import com.ut.database.entity.LockKey;
import com.ut.module_lock.R;
import com.ut.module_lock.entity.AuthCountInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


/**
 * author : zhouyubin
 * time   : 2019/01/08
 * desc   :
 * version: 1.0
 */
public class DeviceKeyVM extends BaseViewModel {
    private ScheduledExecutorService mExecutorService = Executors.newSingleThreadScheduledExecutor();
    private List<DeviceKey> mAllDeviceKey = new ArrayList<>();
    private List<DeviceKeyAuth> mAllDeviceKeyAuth = new ArrayList<>();
    private Observer<List<DeviceKey>> observer1 = null;
    private LockKey mLockKey = null;

    public DeviceKeyVM(@NonNull Application application) {
        super(application);
    }

    public void setLockKey(LockKey lockKey) {
        this.mLockKey = lockKey;
    }

    public LiveData<List<DeviceKey>> getDeviceKeys(int deviceKeyType) {
        return DeviceKeyDaoImpl.get().findDeviceKeysByType(deviceKeyType);
    }

    public int connectLock() {
        
        return 0;
    }

    public void getDevieKey() {
        //TODO 初始化假数据
        DeviceKey deviceKey1 = new DeviceKey(0, 0, "", 0, 0, 0);
        DeviceKey deviceKey2 = new DeviceKey(1, 1, "", 0, 1, 1);
        DeviceKey deviceKey3 = new DeviceKey(2, 2, "", 1, 1, 0);
        DeviceKey deviceKey4 = new DeviceKey(3, 3, "", 2, 0, 0);
        DeviceKey deviceKey5 = new DeviceKey(4, 4, "", 2, 1, 1);
        DeviceKey deviceKey6 = new DeviceKey(5, 5, "", 4, 0, 0);
        DeviceKey deviceKey7 = new DeviceKey(6, 6, "", 4, 1, 1);
        List<DeviceKey> deviceKeys = new ArrayList<>();
        deviceKey1.setKeyStatus(EnumCollection.DeviceKeyStatus.FROZEN.ordinal());
        deviceKey2.setIsAuthKey(true);
        deviceKey3.setIsAuthKey(true);
        deviceKey5.setIsAuthKey(true);
        deviceKey7.setIsAuthKey(true);
        deviceKeys.add(deviceKey1);
        deviceKeys.add(deviceKey2);
        deviceKeys.add(deviceKey3);
        deviceKeys.add(deviceKey4);
        deviceKeys.add(deviceKey5);
        deviceKeys.add(deviceKey6);
        deviceKeys.add(deviceKey7);
        mAllDeviceKey = deviceKeys;
        for (DeviceKey key : mAllDeviceKey) {
            key.initName(getApplication().getResources().getStringArray(R.array.deviceTypeName));
            key.setLockId(Integer.valueOf(mLockKey.getId()));
        }
        //从蓝牙获取信息后存入数据库
        mExecutorService.execute(() -> DeviceKeyDaoImpl.get().insertDeviceKeys(deviceKeys));
        //再从蓝牙获取授权信息
        getDeviceKeyAuth();
    }

    private void getDeviceKeyAuth() {
        //TODO 初始化假数据
        DeviceKeyAuth deviceKeyAuth = new DeviceKeyAuth(0, 1, 10, "1,2,6", 1547111512289L, 1548111512289L, 5);
        DeviceKeyAuth deviceKeyAuth1 = new DeviceKeyAuth(1, 2, 10, "1,2,6", 1547111512289L, 1548111512289L, 10);
        DeviceKeyAuth deviceKeyAuth2 = new DeviceKeyAuth(2, 4, 10, "1,2,6", 1547111512289L, 1548111512289L, 5);
        DeviceKeyAuth deviceKeyAuth4 = new DeviceKeyAuth(4, 6, 10, "1,2,6", 1547111512289L, 1547111512299L, 10);
        List<DeviceKeyAuth> deviceKeyAuths = new ArrayList<>();
        deviceKeyAuths.add(deviceKeyAuth);
        deviceKeyAuths.add(deviceKeyAuth1);
        deviceKeyAuths.add(deviceKeyAuth2);
        deviceKeyAuths.add(deviceKeyAuth4);
        //从蓝牙获取信息后处理
        mAllDeviceKeyAuth = deviceKeyAuths;
//        mExecutorService.execute(() -> DeviceKeyAuthDaoImpl.get().insertDeviceKeyAuths(deviceKeyAuths));
        //再从蓝牙获取授权次数信息
        getDeviceKeyAuthCounts();
    }

    private void getDeviceKeyAuthCounts() {
        //TODO 初始化假数据
        AuthCountInfo authCountInfo = new AuthCountInfo(0, 10, 5);
        AuthCountInfo authCountInfo1 = new AuthCountInfo(0, 10, 2);
        AuthCountInfo authCountInfo2 = new AuthCountInfo(0, 10, 4);
        AuthCountInfo authCountInfo3 = new AuthCountInfo(0, 10, 7);
        List<AuthCountInfo> list = new ArrayList<>();
        list.add(authCountInfo);
        list.add(authCountInfo1);
        list.add(authCountInfo2);
        list.add(authCountInfo3);

        //再从蓝牙获取授权次数信息
        for (DeviceKeyAuth auth : mAllDeviceKeyAuth) {
            for (AuthCountInfo info : list) {
                if (auth.getAuthId() == info.getAuthId()) {
                    auth.setOpenLockCnt(info.getAuthCount());
                    auth.setOpenLockCnt(info.getOpenLockCount());
                }
            }
        }

        for (DeviceKeyAuth auth : mAllDeviceKeyAuth) {
            DeviceKey key = getKeyByKeyId(auth.getKeyID());
            if (key != null) {
                key.setDeviceKeyAuthData(auth);
            }
        }
        //从蓝牙获取信息后存入数据库
        mExecutorService.execute(() -> {
            DeviceKeyDaoImpl.get().insertDeviceKeys(mAllDeviceKey);
        });
    }

    private DeviceKey getKeyByKeyId(int id) {
        for (DeviceKey key : mAllDeviceKey) {
            if (key.getKeyID() == id) {
                return key;
            }
        }
        return null;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        mExecutorService.shutdown();
        DeviceKeyDaoImpl.get().getAll().removeObserver(observer1);
    }
}
