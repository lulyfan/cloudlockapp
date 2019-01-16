package com.ut.module_lock.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.ut.database.entity.DeviceKey;

/**
 * author : zhouyubin
 * time   : 2019/01/12
 * desc   :
 * version: 1.0
 */
public class EditNameVM extends BaseViewModel {
    public EditNameVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> setDeviceNameResult = new MutableLiveData<>();

    public LiveData<Boolean> getSetDeviceNameResult() {
        return setDeviceNameResult;
    }

    public void setDeviceName(DeviceKey deviceKey) {
        //向后台更新名字
        setDeviceNameResult.setValue(true);
    }
}
