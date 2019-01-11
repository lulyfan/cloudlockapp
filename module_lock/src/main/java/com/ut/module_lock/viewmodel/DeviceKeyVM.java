package com.ut.module_lock.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;

import com.ut.database.daoImpl.DeviceKeyAuthDaoImpl;
import com.ut.database.daoImpl.DeviceKeyDaoImpl;
import com.ut.database.entity.DeviceKey;
import com.ut.database.entity.DeviceKeyAuth;
import com.ut.module_lock.R;
import com.ut.module_lock.entity.AuthCountInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.ut.database.entity.EnumCollection.*;

/**
 * author : zhouyubin
 * time   : 2019/01/08
 * desc   :
 * version: 1.0
 */
public class DeviceKeyVM extends BaseViewModel {
    private ScheduledExecutorService mExecutorService = Executors.newSingleThreadScheduledExecutor();
    private MutableLiveData<List<DeviceKey>> mFingerPrintKey = new MutableLiveData<>();
    private MutableLiveData<List<DeviceKey>> mPWDKey = new MutableLiveData<>();
    private MutableLiveData<List<DeviceKey>> mICKey = new MutableLiveData<>();
    private MutableLiveData<List<DeviceKey>> mELECKey = new MutableLiveData<>();
    private List<DeviceKey> mAllDeviceKey = new ArrayList<>();
    private List<DeviceKeyAuth> mAllDeviceKeyAuth = new ArrayList<>();
    private Observer<List<DeviceKey>> observer1 = null;
    private Observer<List<DeviceKeyAuth>> observer2 = null;

    public DeviceKeyVM(@NonNull Application application) {
        super(application);
        initDeviceKeys();
        initDeviceKeyAuthDatas();
        getDevieKey();
    }

    public MutableLiveData<List<DeviceKey>> getDeviceKeys(int deviceKeyType) {
        if (deviceKeyType == DeviceKeyType.ICCARD.ordinal()) {
            return mICKey;
        } else if (deviceKeyType == DeviceKeyType.PASSWORD.ordinal()) {
            return mPWDKey;
        } else if (deviceKeyType == DeviceKeyType.ELECTRONICKEY.ordinal()) {
            return mELECKey;
        }
        return mFingerPrintKey;
    }

    public void getDevieKey() {
        //TODO 初始化假数据
        DeviceKey deviceKey1 = new DeviceKey(0, "", 0, 0, 0);
        DeviceKey deviceKey2 = new DeviceKey(1, "", 0, 1, 1);
        DeviceKey deviceKey3 = new DeviceKey(2, "", 1, 1, 0);
        DeviceKey deviceKey4 = new DeviceKey(3, "", 2, 0, 0);
        DeviceKey deviceKey5 = new DeviceKey(4, "", 2, 1, 1);
        DeviceKey deviceKey6 = new DeviceKey(5, "", 4, 0, 0);
        DeviceKey deviceKey7 = new DeviceKey(6, "", 4, 1, 1);
        List<DeviceKey> deviceKeys = new ArrayList<>();
        deviceKeys.add(deviceKey1);
        deviceKeys.add(deviceKey2);
        deviceKeys.add(deviceKey3);
        deviceKeys.add(deviceKey4);
        deviceKeys.add(deviceKey5);
        deviceKeys.add(deviceKey6);
        deviceKeys.add(deviceKey7);
        mExecutorService.execute(() -> DeviceKeyDaoImpl.get().insertDeviceKeys(deviceKeys));
        getDeviceKeyAuth();
    }

    private void getDeviceKeyAuth() {
        //TODO 初始化假数据
        DeviceKeyAuth deviceKeyAuth = new DeviceKeyAuth(0, 1, 10, "1,2,7", 1547111512289L, 1548111512289L, 5);
        DeviceKeyAuth deviceKeyAuth1 = new DeviceKeyAuth(1, 2, 10, "1,2,7", 1547111512289L, 1548111512289L, 10);
        DeviceKeyAuth deviceKeyAuth2 = new DeviceKeyAuth(2, 4, 10, "1,2,7", 1547111512289L, 1548111512289L, 5);
        DeviceKeyAuth deviceKeyAuth4 = new DeviceKeyAuth(4, 6, 10, "1,2,7", 1547111512289L, 1548111512289L, 10);
        List<DeviceKeyAuth> deviceKeyAuths = new ArrayList<>();
        deviceKeyAuths.add(deviceKeyAuth);
        deviceKeyAuths.add(deviceKeyAuth1);
        deviceKeyAuths.add(deviceKeyAuth2);
        deviceKeyAuths.add(deviceKeyAuth4);
        mAllDeviceKeyAuth = deviceKeyAuths;
//        mExecutorService.execute(() -> DeviceKeyAuthDaoImpl.get().insertDeviceKeyAuths(deviceKeyAuths));
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
        for (DeviceKeyAuth auth : mAllDeviceKeyAuth) {
            for (AuthCountInfo info : list) {
                if (auth.getAuthId() == info.getAuthId()) {
                    auth.setOpenLockCnt(info.getAuthCount());
                    auth.setOpenLockCnt(info.getOpenLockCount());
                }
            }
        }
        mExecutorService.schedule(() -> DeviceKeyAuthDaoImpl.get().insertDeviceKeyAuths(mAllDeviceKeyAuth), 100, TimeUnit.MILLISECONDS);
    }

    private void initDeviceKeys() {
        observer1 = deviceKeys -> {
            mAllDeviceKey = deviceKeys;
            notifyDataSetChanged(deviceKeys);
        };
        DeviceKeyDaoImpl.get().getAll().observeForever(observer1);
    }

    private void notifyDataSetChanged(List<DeviceKey> deviceKeys) {
        notifyDataSetChanged(deviceKeys, DeviceKeyType.FINGERPRINT.ordinal());
        notifyDataSetChanged(deviceKeys, DeviceKeyType.PASSWORD.ordinal());
        notifyDataSetChanged(deviceKeys, DeviceKeyType.ICCARD.ordinal());
        notifyDataSetChanged(deviceKeys, DeviceKeyType.ELECTRONICKEY.ordinal());
    }

    private void initDeviceKeyAuthDatas() {
        observer2 = deviceAuthdatas -> {
            mAllDeviceKeyAuth = deviceAuthdatas;
            for (DeviceKeyAuth auth : deviceAuthdatas) {
                DeviceKey key = getKeyByKeyId(auth.getKeyID());
                if (key != null)
                    key.setDeviceKeyAuthData(auth);
            }
            notifyDataSetChanged(mAllDeviceKey);
        };
        DeviceKeyAuthDaoImpl.get().getAll().observeForever(observer2);
    }

    private DeviceKey getKeyByKeyId(int id) {
        for (DeviceKey key : mAllDeviceKey) {
            if (key.getKeyID() == id) {
                return key;
            }
        }
        return null;
    }

    private void notifyDataSetChanged(List<DeviceKey> list, int type) {
        List<DeviceKey> temp = new ArrayList<>();
        for (DeviceKey key : list) {
            if (key.getKeyType() == type) {
                key.initName(getApplication().getResources().getStringArray(R.array.deviceTypeName));
                temp.add(key);
            }
        }
        getDeviceKeys(type).setValue(temp);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        mExecutorService.shutdown();
        DeviceKeyDaoImpl.get().getAll().removeObserver(observer1);
        DeviceKeyAuthDaoImpl.get().getAll().removeObserver(observer2);
    }
}
