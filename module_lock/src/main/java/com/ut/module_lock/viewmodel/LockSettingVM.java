package com.ut.module_lock.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.operation.MyRetrofit;
import com.ut.base.ErrorHandler;
import com.ut.base.Utils.UTLog;
import com.ut.commoncomponent.CLToast;
import com.ut.database.entity.EnumCollection;
import com.ut.database.entity.Lock;
import com.ut.database.entity.LockKey;
import com.ut.module_lock.R;
import com.ut.unilink.UnilinkManager;
import com.ut.unilink.cloudLock.CallBack;
import com.ut.unilink.cloudLock.CloudLock;
import com.ut.unilink.cloudLock.ConnectListener;
import com.ut.unilink.cloudLock.ScanDevice;
import com.ut.unilink.cloudLock.ScanListener;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * author : zhouyubin
 * time   : 2018/12/25
 * desc   :
 * version: 1.0
 */
public class LockSettingVM extends AndroidViewModel {
    private MutableLiveData<String> showTip = new MutableLiveData<>();
    private boolean isConnected = false;
    private MutableLiveData<Boolean> isUnlockSuccess = new MutableLiveData<>();
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public LockSettingVM(@NonNull Application application) {
        super(application);
    }

    public LiveData<Boolean> isUnlockSuccess() {
        return isUnlockSuccess;
    }

    public LiveData<String> getShowTip() {
        return showTip;
    }


    public int unbindLock(LockKey lockKey) {
        if (UnilinkManager.getInstance(getApplication()).isConnect(lockKey.getMac())) {
            toUnbind(lockKey);
        } else {
            return UnilinkManager.getInstance(getApplication()).scan(new ScanListener() {
                @Override
                public void onScan(ScanDevice scanDevice, List<ScanDevice> list) {
                    if (lockKey.getMac().equals(scanDevice.getAddress())) {
                        toConnect(lockKey, scanDevice);
                    }
                }

                @Override
                public void onFinish() {
                    showTip.postValue(getApplication().getString(R.string.lock_tip_ble_not_finded));
                }
            }, 10);
        }
        return 0;
    }

    private void toConnect(LockKey lockKey, ScanDevice scanDevice) {
        UnilinkManager.getInstance(getApplication()).connect(scanDevice, new ConnectListener() {
            @Override
            public void onConnect() {
                toUnbind(lockKey);
                isConnected = true;
            }

            @Override
            public void onDisconnect(int i, String s) {
                if (!isConnected) {
                    showTip.postValue(getApplication().getString(R.string.lock_tip_ble_unbindlock_failed));
                }
            }
        });
    }

    private void toUnbind(LockKey lockKey) {
        CloudLock cloudLock = new CloudLock(lockKey.getMac());
        cloudLock.setAdminPassword(lockKey.getAdminPwd());
        cloudLock.setEncryptType(lockKey.getEncryptType());
        cloudLock.setEntryptKey(lockKey.getEncryptKey());
        UnilinkManager.getInstance(getApplication())
                .resetLock(cloudLock, new CallBack() {
                    @Override
                    public void onSuccess(CloudLock cloudLock) {
                        deleteAdminLock(lockKey);
                    }

                    @Override
                    public void onFailed(int i, String s) {
                        showTip.postValue(getApplication().getString(R.string.lock_tip_ble_unbindlock_failed));
                    }
                });
    }

    private void deleteAdminLock(LockKey lockKey) {
        Disposable disposable = MyRetrofit.get().getCommonApiService().deleteAdminLock(lockKey.getMac())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        isUnlockSuccess.postValue(true);
                    }
                    showTip.postValue(result.msg);
                }, new ErrorHandler());
        mCompositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mCompositeDisposable.clear();
    }
}
