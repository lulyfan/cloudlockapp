package com.ut.module_lock.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.example.operation.CommonApi;
import com.ut.base.ErrorHandler;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.database.daoImpl.DeviceKeyDaoImpl;
import com.ut.database.entity.DeviceKey;
import com.ut.module_lock.R;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
        Disposable disposable = CommonApi.updateKeyInfo(deviceKey)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(voidResult -> {
                    if (voidResult.isSuccess()) {
                        getShowTip().postValue(getApplication().getString(R.string.operate_success));
                        DeviceKeyDaoImpl.get().insertDeviceKeys(deviceKey);
                        setDeviceNameResult.postValue(true);
                        getApplication().sendBroadcast(new Intent(RouterUtil.BrocastReceiverAction.ACTION_RELOAD_WEB_DEVICEKEY));
                    } else {
                        getShowTip().postValue(getApplication().getString(R.string.operate_success));
                    }
                }, new ErrorHandler());
        mCompositeDisposable.add(disposable);
    }
}
