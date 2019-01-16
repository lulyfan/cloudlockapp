package com.ut.module_lock.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.ut.database.entity.DeviceKey;
import com.ut.module_lock.R;

/**
 * author : zhouyubin
 * time   : 2019/01/14
 * desc   :
 * version: 1.0
 */
public class DeviceKeyRuleVM extends BaseViewModel {
    public MutableLiveData<DeviceKey> mDeviceKey = new MutableLiveData<>();

    public LiveData<Boolean> getSaveResult() {
        return saveResult;
    }

    public MutableLiveData<Boolean> saveResult = new MutableLiveData<>();

    public DeviceKeyRuleVM(@NonNull Application application) {
        super(application);
    }

    public void setDeviceKey(DeviceKey deviceKey) {
        this.mDeviceKey.setValue(deviceKey);
    }

    public DeviceKey getDeviceKey() {
        return mDeviceKey.getValue();
    }

    public String openCntTimeStringByDeviceKey(int cnt) {
        if (cnt == 255) {
            return getApplication().getString(R.string.device_key_time_unlimit);
        }
        return String.valueOf(cnt);
    }

    public void saveDeviceKey() {
        DeviceKey deviceKey = mDeviceKey.getValue();
        //TODO 调用蓝牙写入成功后上传后台
    }

}
