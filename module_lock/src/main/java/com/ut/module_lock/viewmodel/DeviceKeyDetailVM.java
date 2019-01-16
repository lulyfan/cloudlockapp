package com.ut.module_lock.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;

import com.ut.database.daoImpl.DeviceKeyDaoImpl;
import com.ut.database.entity.DeviceKey;
import com.ut.database.entity.DeviceKeyAuth;
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
public class DeviceKeyDetailVM extends BaseViewModel {
    private ScheduledExecutorService mExecutorService = Executors.newSingleThreadScheduledExecutor();
    private MutableLiveData<Boolean> freezeResult = new MutableLiveData();

    public DeviceKeyDetailVM(@NonNull Application application) {
        super(application);
    }

    public LiveData<Boolean> getFreezeResult() {
        return freezeResult;
    }

    public void freezeOrUnfreeze(boolean isFreeze) {
        if (isFreeze) {
            //TODO 解除冻结
        } else {

        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mExecutorService.shutdown();
    }
}
