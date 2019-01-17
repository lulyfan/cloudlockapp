package com.ut.module_lock.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.TextView;

import com.example.operation.CommonApi;
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
import com.ut.unilink.cloudLock.Unilink;
import com.ut.unilink.cloudLock.protocol.data.LockState;

import java.util.List;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

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

    private String mMac = null;

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

    public LiveData<Boolean> getUnlockSuccessStatus() {
        return unlockSuccess;
    }

    public void setLockKey(LockKey lockKey) {
        this.mLockKey = lockKey;
        if (mLockKey != null) {
            mMac = mLockKey.getMac();
        }
    }

    public LiveData<LockKey> getLockKey() {
        return LockKeyDaoImpl.get().getLockByMac(mLockKey.getMac());
    }


    public int openLock() {
        if (mLockKey == null) {
            return -100;
        }

        isConnectSuccessed = false;
        if (mLockKey.getKeyStatus() == EnumCollection.KeyStatus.HAS_INVALID.ordinal()
                || mLockKey.getKeyStatus() == EnumCollection.KeyStatus.HAS_FREEZE.ordinal()
                || mLockKey.getKeyStatus() == EnumCollection.KeyStatus.HAS_OVERDUE.ordinal()) {
            return -3;
        } else if (UnilinkManager.getInstance(getApplication()).isConnect(mLockKey.getMac())) {
            toCheckPermissionOrOpenLock(getCloucLockFromLockKey());
            return 0;
        } else {
            return UnilinkManager.getInstance(getApplication()).scan(new ScanListener() {
                @Override
                public void onScan(ScanDevice scanDevice) {
                    if (!isToConnect && scanDevice.getAddress().equals(mLockKey.getMac())) {
                        toConnect(scanDevice, mLockKey);
                    }
                }

                @Override
                public void onFinish(List<ScanDevice> scanDevices) {

                }

                @Override
                public void onScanTimeout() {
                    showTip.postValue(getApplication().getString(R.string.lock_tip_ble_not_finded));
                }
            }, 10);
        }
    }


    private void toConnect(ScanDevice scanDevice, LockKey lockKey) {
        UnilinkManager.getInstance(getApplication()).stopScan();
        UnilinkManager.getInstance(getApplication()).connect(scanDevice, lockKey.getEncryptType(), lockKey.getEncryptKey(),
                new ConnectListener() {
                    @Override
                    public void onConnect() {
                        CloudLock cloudLock = getCloucLockFromLockKey();
                        io.reactivex.schedulers.Schedulers.io().scheduleDirect(() -> {
                            toGetElect(cloudLock);
                        }, 100, TimeUnit.MILLISECONDS);

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
                }, mLockStateListener);
    }

    private void toCheckPermissionOrOpenLock(CloudLock cloudLock) {
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

    private void toGetElect(CloudLock cloudLock) {
        UnilinkManager.getInstance(getApplication()).getElect(cloudLock, new CallBack() {
            @Override
            public void onSuccess(CloudLock cloudLock) {
                mLockKey.setElectric(cloudLock.getElect());
                toCheckPermissionOrOpenLock(cloudLock);
                rx.Observable.just(mLockKey)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(lockKey -> {
                            LockKeyDaoImpl.get().insert(lockKey);
                        });
            }

            @Override
            public void onFailed(int i, String s) {
                toCheckPermissionOrOpenLock(cloudLock);
            }
        });
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
        Disposable result = CommonApi.isAuth(mLockKey.getMac())
                .subscribe(jsonElementResult -> {
                            if (jsonElementResult.code == 200) {
                                io.reactivex.schedulers.Schedulers.io().scheduleDirect(() -> {
                                    toActOpenLock(getCloucLockFromLockKey());
                                }, 100, TimeUnit.MILLISECONDS);
                            } else {
                                showTip.postValue(jsonElementResult.msg);
                            }
                        },
                        throwable -> {
                            UTLog.i("鉴权时网络错误");
                            showTip.postValue(getApplication().getString(R.string.tip_network_error));
                        });
        mCompositeDisposable.add(result);
    }

    private void toActOpenLock(CloudLock cloudLock) {
        if (mLockKey.getType() == EnumCollection.LockType.SMARTLOCK.getType()) {
            UnilinkManager.getInstance(getApplication()).openGateLock(mLockKey.getMac(),
                    mLockKey.getEncryptType(), mLockKey.getEncryptKey(), cloudLock.getOpenLockPassword(), new CallBack2<Void>() {
                        @Override
                        public void onSuccess(Void data) {
                            unlockSuccess.postValue(true);
                            toAddLog(1);
                        }

                        @Override
                        public void onFailed(int errCode, String errMsg) {
                            UTLog.i("onOpen Fail:" + errCode + " s:" + errMsg);
                            showTip.postValue(getApplication().getString(R.string.lock_tip_ble_unlock_failed));
                            toAddLog(2);
                        }
                    });
        } else {
            UnilinkManager.getInstance(getApplication()).openCloudLock(mLockKey.getMac(),
                    mLockKey.getEncryptType(), mLockKey.getEncryptKey(), cloudLock.getOpenLockPassword(), new CallBack2<Void>() {
                        @Override
                        public void onSuccess(Void data) {
                            unlockSuccess.postValue(true);
                            toAddLog(1);
                        }

                        @Override
                        public void onFailed(int errCode, String errMsg) {
                            UTLog.i("onOpen Fail:" + errCode + " s:" + errMsg);
                            showTip.postValue(getApplication().getString(R.string.lock_tip_ble_unlock_failed));
                            toAddLog(2);
                        }
                    });
        }

//        UnilinkManager.getInstance(getApplication()).openLock(cloudLock, new CallBack() {
//            @Override
//            public void onSuccess(CloudLock cloudLock) {
//                unlockSuccess.postValue(true);
//                toAddLog(1);
//            }
//
//            @Override
//            public void onFailed(int i, String s) {
//                UTLog.i("onOpen Fail:" + i + " s:" + s);
//                showTip.postValue(getApplication().getString(R.string.lock_tip_ble_unlock_failed));
//                toAddLog(2);
//            }
//        });
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
        mCompositeDisposable.dispose();
        UnilinkManager.getInstance(getApplication()).disconnect(mMac);
    }
}
