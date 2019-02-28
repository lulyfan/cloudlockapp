package com.ut.module_lock.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.operation.CommonApi;
import com.ut.base.ErrorHandler;
import com.ut.base.Utils.UTLog;
import com.ut.database.daoImpl.LockKeyDaoImpl;
import com.ut.database.entity.EnumCollection;
import com.ut.database.entity.LockKey;
import com.ut.module_lock.R;
import com.ut.unilink.UnilinkManager;
import com.ut.unilink.cloudLock.CallBack;
import com.ut.unilink.cloudLock.CallBack2;
import com.ut.unilink.cloudLock.CloudLock;
import com.ut.unilink.cloudLock.ConnectListener;
import com.ut.unilink.cloudLock.LockStateListener;
import com.ut.unilink.cloudLock.ScanDevice;
import com.ut.unilink.cloudLock.ScanListener;
import com.ut.unilink.util.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.disposables.Disposable;

/**
 * author : zhouyubin
 * time   : 2018/12/20
 * desc   :
 * version: 1.0
 */
public class LockDetailVM extends BaseViewModel {
    private MutableLiveData<Integer> mShowTipDialog = new MutableLiveData<>();//1:显示开锁成功提示框，2：显示锁被重置提示框
    public static final int SHOWTIPDIALOG_TYPE_UNLOCKSUCCESS = 1;
    public static final int SHOWTIPDIALOG_TYPE_LOCKRESET = 2;
    private MutableLiveData<Boolean> connectStatus = new MutableLiveData<>();
    private MutableLiveData<Boolean> reAutoOpen = new MutableLiveData<>();//重新自动开锁
    private MutableLiveData<Integer> electricity = new MutableLiveData<>();//电量变化更新
    private LockKey mLockKey = null;
    public static final String TAG = LockDetailVM.class.getSimpleName();
    private LiveData<LockKey> mLockKeyLiveData = null;
    private int mOpenLockType = EnumCollection.OpenLockType.BLEMANUAL.ordinal();

    private AtomicInteger mOpenStatus = new AtomicInteger(EnumCollection.OpenLockState.INITIAL);

    public LockDetailVM(@NonNull Application application) {
        super(application);
        connectStatus.setValue(false);
    }

    public void setIsAutoOpen(boolean isAutoOpen) {
        mOpenLockType = isAutoOpen ? EnumCollection.OpenLockType.BLEAUTO.ordinal()
                : EnumCollection.OpenLockType.BLEMANUAL.ordinal();
    }

    public LiveData<Boolean> getConnectStatus() {
        return connectStatus;
    }

    public LiveData<Integer> getUnlockSuccessStatus() {
        return mShowTipDialog;
    }

    public LiveData<Integer> getElectricity() {
        return electricity;
    }

    public MutableLiveData<Boolean> getReAutoOpen() {
        return reAutoOpen;
    }

    public void setLockKey(LockKey lockKey) {
        this.mLockKey = lockKey;
    }

    public LiveData<LockKey> getLockKey(String mac) {
        if (mLockKeyLiveData == null) {
            mLockKeyLiveData = LockKeyDaoImpl.get().getLockByMac(mac);
        }
        return mLockKeyLiveData;
    }

    private boolean isLockKeyInvalid() {
        return mLockKey.getKeyStatus() == EnumCollection.KeyStatus.HAS_INVALID.ordinal()
                || mLockKey.getKeyStatus() == EnumCollection.KeyStatus.HAS_FREEZE.ordinal()
                || mLockKey.getKeyStatus() == EnumCollection.KeyStatus.HAS_OVERDUE.ordinal();
    }

    public int openLock() {
        if (mLockKey == null) {
            return -100;
        }

        if (isLockKeyInvalid()) {//无效
            return -3;
        } else if (UnilinkManager.getInstance(getApplication()).isConnect(mLockKey.getMac())) {//已连接
            connectStatus.postValue(true);
            UTLog.i(TAG, "锁已连接");
            toCheckPermissionOrOpenLock(getCloucLockFromLockKey());
            UnilinkManager.getInstance(getApplication()).setConnectListener(mConnectListener);
            return 0;
        } else {
            if (mOpenStatus.get() != EnumCollection.OpenLockState.INITIAL) return 0;
            UTLog.i(TAG, "开始搜索");
            int result = UnilinkManager.getInstance(getApplication()).scan(new ScanListener() {
                @Override
                public void onScan(ScanDevice scanDevice) {
                    Log.i(TAG, "scan device 5:" + scanDevice.getAddress() +
                            " openStatus:" + mOpenStatus.get());
                    if (scanDevice.getAddress().equalsIgnoreCase(mLockKey.getMac())) {
                        if (mOpenStatus.compareAndSet(EnumCollection.OpenLockState.SCANNING
                                , EnumCollection.OpenLockState.SCANNED)) {
                            UnilinkManager.getInstance(getApplication()).stopScan();
                            if (!scanDevice.isActive()) {//当锁没有激活的时候，给出提示
                                showDialog.postValue(false);
                                mShowTipDialog.postValue(SHOWTIPDIALOG_TYPE_LOCKRESET);
                                mOpenStatus.set(EnumCollection.OpenLockState.INITIAL);
                            } else {
                                toConnect(scanDevice, mLockKey);
                            }
                        }
                    }
                }

                @Override
                public void onFinish(List<ScanDevice> scanDevices) {
                    Log.i(TAG, "scan device onFinish 7:");
                    if (mOpenStatus.get() == EnumCollection.OpenLockState.SCANNING) {
                        onScanTimeout();
                    }
                }

                @Override
                public void onScanTimeout() {
                    Log.i(TAG, "scan device onScanTimeout 8:");
                    showTip.postValue(getApplication().getString(R.string.lock_tip_ble_not_finded));
                    mOpenStatus.set(EnumCollection.OpenLockState.INITIAL);
                    showDialog.postValue(false);
                    reAutoOpen.postValue(true);
                }
            }, 10);
            if (result == 0) {
                mOpenStatus.set(EnumCollection.OpenLockState.SCANNING);
            }
            return result;
        }
    }

    private void toConnect(ScanDevice scanDevice, LockKey lockKey) {
        UTLog.i(TAG, "开始连接");
        if (mOpenStatus.compareAndSet(EnumCollection.OpenLockState.SCANNED
                , EnumCollection.OpenLockState.CONNECTING)) {
            UnilinkManager.getInstance(getApplication()).setConnectListener(null);
            UnilinkManager.getInstance(getApplication()).connect(scanDevice, lockKey.getEncryptType(), lockKey.getEncryptKey(),
                    mConnectListener, mLockStateListener);
        }
    }

    ConnectListener mConnectListener = new ConnectListener() {
        @Override
        public void onConnect() {
            if (mOpenStatus.compareAndSet(EnumCollection.OpenLockState.CONNECTING
                    , EnumCollection.OpenLockState.CONNECTED)) {
                io.reactivex.schedulers.Schedulers.io().scheduleDirect(() -> {
                    toGetElect(getCloucLockFromLockKey());
                }, 100, TimeUnit.MILLISECONDS);
                connectStatus.postValue(true);
            }

        }

        @Override
        public void onDisconnect(int i, String s) {
            UTLog.i(TAG, "onDisconnect:" + i + " s:" + s);
            connectStatus.postValue(false);
            if (mOpenStatus.get() < EnumCollection.OpenLockState.CONNECTED) {
                showTip.postValue(getApplication().getString(R.string.lock_tip_ble_unlock_failed));
            }
            showDialog.postValue(false);
            mOpenStatus.set(EnumCollection.OpenLockState.INITIAL);
            reAutoOpen.postValue(true);
        }
    };

    private void toGetElect(CloudLock cloudLock) {//获取电量，不管结果成不成功都去开锁
        UnilinkManager.getInstance(getApplication()).getElect(cloudLock, new CallBack() {
            @Override
            public void onSuccess(CloudLock cloudLock) {
                toCheckPermissionOrOpenLock(cloudLock);
                if (mLockKey.getElectric() != cloudLock.getElect()) {//电量有变化时提示界面更新
                    electricity.postValue(cloudLock.getElect());
                }
            }

            @Override
            public void onFailed(int i, String s) {
                UTLog.i(TAG, "to get elect failed:" + i + " s:" + s);
                toCheckPermissionOrOpenLock(cloudLock);
            }
        });
    }

    private void toCheckPermissionOrOpenLock(CloudLock cloudLock) {
        UTLog.i(TAG, "开始检查权限或开锁");
        if (mLockKey.getUserType() == EnumCollection.UserType.NORMAL.ordinal()) {
            toCheckPermission();
        } else {
            io.reactivex.schedulers.Schedulers.io().scheduleDirect(() -> {
                toActOpenLock(cloudLock);
            }, 100, TimeUnit.MILLISECONDS);
        }
    }

    private void toCheckPermission() {
        UTLog.i(TAG, "检查权限");
        Disposable result = CommonApi.isAuth(mLockKey.getMac())
                .subscribe(jsonElementResult -> {
                            if (jsonElementResult.code == 200) {
                                toActOpenLock(getCloucLockFromLockKey());
                            } else {
                                showTip.postValue(jsonElementResult.msg);
                                mOpenStatus.set(EnumCollection.OpenLockState.INITIAL);
                                showDialog.postValue(false);
                                reAutoOpen.postValue(true);
                            }
                        },
                        new ErrorHandler() {
                            @Override
                            public void accept(Throwable throwable) {
                                super.accept(throwable);
                                mOpenStatus.set(EnumCollection.OpenLockState.INITIAL);
                                showDialog.postValue(false);
                                reAutoOpen.postValue(true);
                            }
                        });
        mCompositeDisposable.add(result);
    }

    private void toActOpenLock(CloudLock cloudLock) {
        UTLog.i(TAG, "开始开锁");
        if (mOpenStatus.compareAndSet(EnumCollection.OpenLockState.CONNECTED
                , EnumCollection.OpenLockState.OPENING)) {
            if (mLockKey.getType() == EnumCollection.LockType.SMARTLOCK.getType()) {
                UnilinkManager.getInstance(getApplication()).openGateLock(mLockKey.getMac(),
                        mLockKey.getEncryptType(), mLockKey.getEncryptKey(),
                        cloudLock.getOpenLockPassword(), mOpenCallback);
            } else {
                UnilinkManager.getInstance(getApplication()).openCloudLock(mLockKey.getMac(),
                        mLockKey.getEncryptType(), mLockKey.getEncryptKey(),
                        cloudLock.getOpenLockPassword(), mOpenCallback);
            }
        }

    }

    private void toAddLog(int type, int openLockType) {
        Disposable disposable = CommonApi.addLog(Long.parseLong(mLockKey.getId()), mLockKey.getKeyId(), type, openLockType, mLockKey.getElectric())
                .subscribe(jsonElementResult -> {
                    UTLog.i(jsonElementResult.toString());
                }, throwable -> {
                    throwable.printStackTrace();
                    //TODO 将未成功提交的记录保存在本地，后面继续提交
                });
        mCompositeDisposable.add(disposable);
    }

    private CallBack2<Void> mOpenCallback = new CallBack2<Void>() {
        @Override
        public void onSuccess(Void data) {
            toAddLog(1, mOpenLockType);
            mShowTipDialog.postValue(SHOWTIPDIALOG_TYPE_UNLOCKSUCCESS);
            showDialog.postValue(false);
            //todo 暂时先提示断开
//            connectStatus.postValue(false);
//            mOpenStatus.compareAndSet(EnumCollection.OpenLockState.OPENING
//                    , EnumCollection.OpenLockState.CONNECTED);
            if (mLockKey != null) {
                mExecutorService.schedule(() -> {
                    UnilinkManager.getInstance(getApplication()).disconnect(mLockKey.getMac());
                }, 500, TimeUnit.MILLISECONDS);
            }
        }

        @Override
        public void onFailed(int errCode, String errMsg) {
            UTLog.i(TAG, "onOpen Fail:" + errCode + " s:" + errMsg);
            toAddLog(2, mOpenLockType);
            showTip.postValue(getApplication().getString(R.string.lock_tip_ble_unlock_failed));
            showDialog.postValue(false);
            mOpenStatus.compareAndSet(EnumCollection.OpenLockState.OPENING
                    , EnumCollection.OpenLockState.CONNECTED);
        }
    };

    @NonNull
    private CloudLock getCloucLockFromLockKey() {
        CloudLock cloudLock = new CloudLock(mLockKey.getMac());
        cloudLock.setOpenLockPassword(mLockKey.getBlueKey());
        cloudLock.setEncryptType(mLockKey.getEncryptType());
        cloudLock.setEntryptKey(mLockKey.getEncryptKey());
        return cloudLock;
    }

    public void reSetStatus() {
        this.mOpenStatus.set(EnumCollection.OpenLockState.INITIAL);
    }

    private LockStateListener mLockStateListener = lockState -> {
//        mLockKey.setElectric(lockState.getElect());
//        rx.Observable.just(mLockKey)
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .subscribe(lockKey -> {
//                    LockKeyDaoImpl.get().insert(lockKey);
//                });
    };

    @Override
    protected void onCleared() {
        super.onCleared();
        UnilinkManager.getInstance(getApplication()).setConnectListener(null);
        if (mLockKey != null) {
            UnilinkManager.getInstance(getApplication()).disconnect(mLockKey.getMac());
        }
    }
}
