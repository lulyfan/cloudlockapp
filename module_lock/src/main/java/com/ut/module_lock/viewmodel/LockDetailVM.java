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
import com.ut.unilink.cloudLock.CallBack2;
import com.ut.unilink.cloudLock.CloudLock;
import com.ut.unilink.cloudLock.ConnectListener;
import com.ut.unilink.cloudLock.LockStateListener;
import com.ut.unilink.cloudLock.ScanDevice;
import com.ut.unilink.cloudLock.ScanListener;
import com.ut.unilink.cloudLock.protocol.data.LockState;
import com.ut.unilink.util.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import rx.schedulers.Schedulers;

/**
 * author : zhouyubin
 * time   : 2018/12/20
 * desc   :
 * version: 1.0
 */
public class LockDetailVM extends BaseViewModel {
    private MutableLiveData<Boolean> unlockSuccess = new MutableLiveData<>();
    private MutableLiveData<Boolean> connectStatus = new MutableLiveData<>();
    private MutableLiveData<Boolean> reAutoOpen = new MutableLiveData<>();//重新自动开锁
    private LockKey mLockKey = null;
    private static final String TAG = LockDetailVM.class.getSimpleName();

    private int openStatus = EnumCollection.OpenLockState.INITIAL;

    public LockDetailVM(@NonNull Application application) {
        super(application);
        connectStatus.setValue(false);
    }

    public LiveData<Boolean> getConnectStatus() {
        return connectStatus;
    }

    public LiveData<Boolean> getUnlockSuccessStatus() {
        return unlockSuccess;
    }

    public MutableLiveData<Boolean> getReAutoOpen() {
        return reAutoOpen;
    }

    public void setLockKey(LockKey lockKey) {
        this.mLockKey = lockKey;
    }

    public LiveData<LockKey> getLockKey() {
        return LockKeyDaoImpl.get().getLockByMac(mLockKey.getMac());
    }


    public int openLock() {
        if (mLockKey == null) {
            return -100;
        }

        if (isLockKeyInvalid()) {//无效
            return -3;
        } else if (UnilinkManager.getInstance(getApplication()).isConnect(mLockKey.getMac())) {//已连接
            connectStatus.postValue(true);
            toCheckPermissionOrOpenLock(getCloucLockFromLockKey());
            UnilinkManager.getInstance(getApplication()).setConnectListener(getConnectListener());
            return 0;
        } else {
            if (openStatus != EnumCollection.OpenLockState.INITIAL) return 0;
            UTLog.i(TAG, "开始搜索");
            int result = UnilinkManager.getInstance(getApplication()).scan(new ScanListener() {
                @Override
                public void onScan(ScanDevice scanDevice) {
                    Log.i("scan", "scan device 5:" + scanDevice.getAddress() +
                            " openStatus:" + openStatus);
                    if (openStatus == EnumCollection.OpenLockState.SCANNING
                            && scanDevice.getAddress().equalsIgnoreCase(mLockKey.getMac())) {
                        Log.i("scan", "scan device 6:" + scanDevice.getAddress());
                        toConnect(scanDevice, mLockKey);
                    }
                }

                @Override
                public void onFinish(List<ScanDevice> scanDevices) {
                    Log.i("scan", "scan device 7:");
                    if (openStatus == EnumCollection.OpenLockState.SCANNING) {
                        showTip.postValue(getApplication().getString(R.string.lock_tip_ble_not_finded));
                        openStatus = EnumCollection.OpenLockState.INITIAL;
                        reAutoOpen.postValue(true);
                        showDialog.postValue(false);
                        UTLog.i("getShowDialog");
                    }
                }

                @Override
                public void onScanTimeout() {
                    Log.i("scan", "scan device 8:");
                    showTip.postValue(getApplication().getString(R.string.lock_tip_ble_not_finded));
                    openStatus = EnumCollection.OpenLockState.INITIAL;
                    reAutoOpen.postValue(true);
                    showDialog.postValue(false);
                    UTLog.i("getShowDialog");
                }
            }, 10);
            if (result == 0) {
                openStatus = EnumCollection.OpenLockState.SCANNING;
            }
            return result;
        }
    }

    private boolean isLockKeyInvalid() {
        return mLockKey.getKeyStatus() == EnumCollection.KeyStatus.HAS_INVALID.ordinal()
                || mLockKey.getKeyStatus() == EnumCollection.KeyStatus.HAS_FREEZE.ordinal()
                || mLockKey.getKeyStatus() == EnumCollection.KeyStatus.HAS_OVERDUE.ordinal();
    }


    private void toConnect(ScanDevice scanDevice, LockKey lockKey) {
        UTLog.i(TAG, "开始连接");
        openStatus = EnumCollection.OpenLockState.CONNECTING;
        UnilinkManager.getInstance(getApplication()).stopScan();
        UnilinkManager.getInstance(getApplication()).connect(scanDevice, lockKey.getEncryptType(), lockKey.getEncryptKey(),
                getConnectListener(), mLockStateListener);
    }

    @NonNull
    private ConnectListener getConnectListener() {
        return new ConnectListener() {
            @Override
            public void onConnect() {
                openStatus = EnumCollection.OpenLockState.CHECKANDOPENING;
                io.reactivex.schedulers.Schedulers.io().scheduleDirect(() -> {
                    toCheckPermissionOrOpenLock(getCloucLockFromLockKey());
                }, 100, TimeUnit.MILLISECONDS);
                connectStatus.postValue(true);
            }

            @Override
            public void onDisconnect(int i, String s) {
                UTLog.i(TAG, "onDisconnect:" + i + " s:" + s);
                connectStatus.postValue(false);
                if (openStatus < EnumCollection.OpenLockState.CHECKANDOPENING) {
                    showTip.postValue(getApplication().getString(R.string.lock_tip_ble_unlock_failed));
                }
                showDialog.postValue(false);
                UTLog.i("getShowDialog");
                openStatus = EnumCollection.OpenLockState.INITIAL;
                reAutoOpen.postValue(true);
            }
        };
    }

    private void toCheckPermissionOrOpenLock(CloudLock cloudLock) {
        UTLog.i(TAG, "开始检查权限或开锁");
        openStatus = EnumCollection.OpenLockState.CHECKANDOPENING;
        if (mLockKey.getUserType() == EnumCollection.UserType.NORMAL.ordinal()) {
            toCheckPermission();
        } else {
            io.reactivex.schedulers.Schedulers.io().scheduleDirect(() -> {
                toActOpenLock(cloudLock);
            }, 100, TimeUnit.MILLISECONDS);
        }
    }

    @NonNull
    private CloudLock getCloucLockFromLockKey() {
        CloudLock cloudLock = new CloudLock(mLockKey.getMac());
        cloudLock.setOpenLockPassword(mLockKey.getBlueKey());
        cloudLock.setEncryptType(mLockKey.getEncryptType());
        cloudLock.setEntryptKey(mLockKey.getEncryptKey());
        return cloudLock;
    }

    private LockStateListener mLockStateListener = new LockStateListener() {
        @Override
        public void onState(LockState lockState) {
            mLockKey.setElectric(lockState.getElect());
            rx.Observable.just(mLockKey)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(lockKey -> {
                        LockKeyDaoImpl.get().insert(lockKey);
                    });
        }
    };

    private void toCheckPermission() {
        UTLog.i(TAG, "检查权限");
        Disposable result = CommonApi.isAuth(mLockKey.getMac())
                .subscribe(jsonElementResult -> {
                            if (jsonElementResult.code == 200) {
                                io.reactivex.schedulers.Schedulers.io().scheduleDirect(() -> {
                                    toActOpenLock(getCloucLockFromLockKey());
                                }, 100, TimeUnit.MILLISECONDS);
                            } else {
                                showTip.postValue(jsonElementResult.msg);
                                openStatus = EnumCollection.OpenLockState.INITIAL;
                                showDialog.postValue(false);
                                UTLog.i("getShowDialog");
                                reAutoOpen.postValue(true);
                            }
                        },
                        throwable -> {
                            UTLog.i("鉴权时网络错误");
                            showTip.postValue(getApplication().getString(R.string.tip_network_error));
                            openStatus = EnumCollection.OpenLockState.INITIAL;
                            showDialog.postValue(false);
                            UTLog.i("getShowDialog");
                            reAutoOpen.postValue(true);
                        });
        mCompositeDisposable.add(result);
    }

    private void toActOpenLock(CloudLock cloudLock) {
        UTLog.i(TAG, "开始开锁");
        openStatus = EnumCollection.OpenLockState.OPENING;
        if (mLockKey.getType() == EnumCollection.LockType.SMARTLOCK.getType()) {
            UnilinkManager.getInstance(getApplication()).openGateLock(mLockKey.getMac(),
                    mLockKey.getEncryptType(), mLockKey.getEncryptKey(), cloudLock.getOpenLockPassword(), new CallBack2<Void>() {
                        @Override
                        public void onSuccess(Void data) {
                            unlockSuccess.postValue(true);
                            toAddLog(1);
                            showDialog.postValue(false);
                            UTLog.i(TAG, "getShowDialog");
//                            openStatus = EnumCollection.OpenLockState.INITIAL;
                        }

                        @Override
                        public void onFailed(int errCode, String errMsg) {
                            UTLog.i(TAG, "onOpen Fail:" + errCode + " s:" + errMsg);
                            showTip.postValue(getApplication().getString(R.string.lock_tip_ble_unlock_failed));
                            toAddLog(2);
//                            openStatus = EnumCollection.OpenLockState.INITIAL;
                            showDialog.postValue(false);
                        }
                    });
        } else {
            UnilinkManager.getInstance(getApplication()).openCloudLock(mLockKey.getMac(),
                    mLockKey.getEncryptType(), mLockKey.getEncryptKey(), cloudLock.getOpenLockPassword(), new CallBack2<Void>() {
                        @Override
                        public void onSuccess(Void data) {
                            unlockSuccess.postValue(true);
                            toAddLog(1);
                            openStatus = EnumCollection.OpenLockState.INITIAL;
                            showDialog.postValue(false);
                            UTLog.i("getShowDialog");
                        }

                        @Override
                        public void onFailed(int errCode, String errMsg) {
                            UTLog.i("onOpen Fail:" + errCode + " s:" + errMsg);
                            showTip.postValue(getApplication().getString(R.string.lock_tip_ble_unlock_failed));
                            toAddLog(2);
                            openStatus = EnumCollection.OpenLockState.INITIAL;
                            showDialog.postValue(false);
                            UTLog.i("getShowDialog");
                        }
                    });
        }

    }

    private void toAddLog(int type) {
        Disposable disposable = CommonApi.addLog(Long.parseLong(mLockKey.getId()), mLockKey.getKeyId(), type, mLockKey.getElectric())
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
        UnilinkManager.getInstance(getApplication()).setConnectListener(null);
        if (mLockKey != null)
            UnilinkManager.getInstance(getApplication()).disconnect(mLockKey.getMac());
    }
}
