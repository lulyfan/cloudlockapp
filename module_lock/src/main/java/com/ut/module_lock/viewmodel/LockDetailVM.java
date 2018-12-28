package com.ut.module_lock.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.operation.CommonApi;
import com.ut.base.Utils.UTLog;
import com.ut.database.daoImpl.LockKeyDaoImpl;
import com.ut.database.entity.EnumCollection;
import com.ut.database.entity.LockKey;
import com.ut.module_lock.R;
import com.ut.unilink.UnilinkManager;
import com.ut.unilink.cloudLock.CallBack;
import com.ut.unilink.cloudLock.CloudLock;
import com.ut.unilink.cloudLock.ConnectListener;
import com.ut.unilink.cloudLock.ScanDevice;
import com.ut.unilink.cloudLock.ScanListener;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * author : zhouyubin
 * time   : 2018/12/20
 * desc   :
 * version: 1.0
 */
public class LockDetailVM extends AndroidViewModel {
    private MutableLiveData<Boolean> unlockSuccess = new MutableLiveData<>();
    private MutableLiveData<Boolean> connectStatus = new MutableLiveData<>();
    private MutableLiveData<String> showTip = new MutableLiveData<>();
    private LockKey mLockKey = null;
    private volatile boolean isToConnect = false;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private boolean isConnectSuccessed = false;

    public LockDetailVM(@NonNull Application application) {
        super(application);
        connectStatus.setValue(false);
    }

    public LiveData<Boolean> getConnectStatus() {
        return connectStatus;
    }

    public LiveData<String> getShowTip() {
        return showTip;
    }

    public LiveData<Boolean> getUnlockSuccessStatus(){
        return unlockSuccess;
    }

    public void setLockKey(LockKey lockKey) {
        this.mLockKey = lockKey;
    }

    public LiveData<LockKey> getLockKey(){
        return LockKeyDaoImpl.get().getLockByMac(mLockKey.getMac());
    }

    public int openLock() {
        isConnectSuccessed = false;
        return UnilinkManager.getInstance(getApplication()).scan(new ScanListener() {
            @Override
            public void onScan(ScanDevice scanDevice, List<ScanDevice> list) {
                if (!isToConnect && scanDevice.getAddress().equals(mLockKey.getMac())) {
                    toConnect(scanDevice);
                }
            }

            @Override
            public void onFinish() {
                showTip.postValue(getApplication().getString(R.string.lock_tip_ble_not_finded));
            }
        }, 20);
    }

    private void toConnect(ScanDevice scanDevice) {
        UnilinkManager.getInstance(getApplication()).stopScan();
        UnilinkManager.getInstance(getApplication()).connect(scanDevice, new ConnectListener() {
            @Override
            public void onConnect() {
                if (mLockKey.getUserType() == EnumCollection.UserType.NORMAL.ordinal()) {
                    toCheckPermission();
                } else {
                    toActOpenLock();
                }
                isConnectSuccessed = true;
                connectStatus.postValue(true);
            }

            @Override
            public void onDisconnect(int i, String s) {
                UTLog.i("onDisconnect:" + i + " s:" + s);
                connectStatus.postValue(false);
                if (!isConnectSuccessed) {
                    showTip.postValue(getApplication().getString(R.string.lock_tip_ble_unlock_failed));
                }
            }
        });
    }

    private void toCheckPermission() {
        Disposable result = CommonApi.isAuth(mLockKey.getMac())
                .subscribe(jsonElementResult -> {
                            if (jsonElementResult.code == 200) {
                                toActOpenLock();
                            } else {
                                showTip.postValue(jsonElementResult.msg);
                            }
                        },
                        throwable -> {
                            UTLog.i("onDisconnect");
                            showTip.postValue(getApplication().getString(R.string.lock_tip_ble_unlock_failed));
                        });
        mCompositeDisposable.add(result);
    }

    private void toActOpenLock() {
        CloudLock cloudLock = new CloudLock(mLockKey.getMac());
        cloudLock.setOpenLockPassword(mLockKey.getBlueKey());
        cloudLock.setEncryptType(mLockKey.getEncryptType());
        cloudLock.setEntryptKey(mLockKey.getEncryptKey());
        UnilinkManager.getInstance(getApplication()).openLock(cloudLock, new CallBack() {
            @Override
            public void onSuccess(CloudLock cloudLock) {
//                showTip.postValue(getApplication().getString(R.string.lock_tip_ble_unlock_success));
                unlockSuccess.postValue(true);
                toAddLog(1);
            }

            @Override
            public void onFailed(int i, String s) {
                UTLog.i("onFailed");
                showTip.postValue(getApplication().getString(R.string.lock_tip_ble_unlock_failed));
                toAddLog(2);
            }
        });
    }

    private void toAddLog(int type) {
        Disposable disposable = CommonApi.addLog(Long.parseLong(mLockKey.getId()), mLockKey.getKeyId(), type)
                .subscribe(jsonElementResult -> {
                    UTLog.i(jsonElementResult.toString());
                }, throwable -> {
                    throwable.printStackTrace();
                    //TODO 将未成功提交的记录保存在本地，后面继续提交
                });
        mCompositeDisposable.add(disposable);
    }

    @Override

    protected void onCleared() {
        super.onCleared();
        mCompositeDisposable.dispose();
    }
}
