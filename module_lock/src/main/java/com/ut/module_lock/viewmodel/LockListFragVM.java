package com.ut.module_lock.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.example.operation.CommonApi;
import com.ut.base.Utils.UTLog;
import com.ut.database.daoImpl.LockGroupDaoImpl;
import com.ut.database.daoImpl.LockKeyDaoImpl;
import com.ut.database.entity.EnumCollection;
import com.ut.database.entity.LockGroup;
import com.ut.database.entity.LockKey;
import com.ut.module_lock.R;
import com.ut.unilink.UnilinkManager;
import com.ut.unilink.cloudLock.CallBack2;
import com.ut.unilink.cloudLock.ConnectListener;
import com.ut.unilink.cloudLock.ScanDevice;
import com.ut.unilink.cloudLock.ScanListener;
import com.ut.unilink.util.Base64;
import com.ut.unilink.util.Log;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * author : zhouyubin
 * time   : 2018/12/17
 * desc   :
 * version: 1.0
 */
public class LockListFragVM extends AndroidViewModel {
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private MutableLiveData<Boolean> refreshStatus = new MutableLiveData<>();
    private MutableLiveData<List<LockKey>> mLockKeys = new MutableLiveData<>();
    private volatile boolean mIsReset = false;
    private LiveData<List<LockKey>> lock1 = null;//全部分组的锁
    private LiveData<List<LockKey>> lock2 = null;//当前分组的锁
    private long currentGroupId = -1;

    private boolean isFindDevice;
    private LockKey mLockKey;   //本次要开的锁
    public boolean isStopScan;
    public boolean isScaning;
    private Handler handler = new Handler(Looper.getMainLooper());

    public LockListFragVM(@NonNull Application application) {
        super(application);
        getLockKeyFromDb();//先从数据库获取数据
    }

    public MutableLiveData<Boolean> getRefreshStatus() {
        return refreshStatus;
    }

    public LiveData<List<LockKey>> getLockList() {
        return mLockKeys;
    }

    private void getLockKeyFromDb() {//监听数据库变化
        lock1 = LockKeyDaoImpl.get().getAllLockKey();
        lock1.observeForever(lockKeys -> {
            assert lockKeys != null;
            if (currentGroupId != -1) return;
//            if (mIsReset && lockKeys.size() < 1) return;
            mLockKeys.setValue(lockKeys);
        });
    }


    public LiveData<List<LockGroup>> getLockGroupList() {
        return LockGroupDaoImpl.get().getAllLockGroup();
    }

    public void toGetLockAllList(boolean isReset) {
        Disposable disposable = CommonApi.pageUserLock(1, -1)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(lockKeyResults -> {
                    List<LockKey> list = lockKeyResults.getData();
                    LockKey[] lockKeys = new LockKey[list.size()];
                    //TODO 先清除数据,后面再做优化
                    if (isReset) {
                        mIsReset = true;
                        LockKeyDaoImpl.get().deleteAll();
                    }
                    LockKeyDaoImpl.get().insertAll(list.toArray(lockKeys));
                    if (isReset)
                        refreshStatus.postValue(true);
                }, throwable -> {
                    //TODO 获取锁列表失败处理
                    refreshStatus.postValue(false);
                });
        mCompositeDisposable.add(disposable);
    }


    public void toGetAllGroupList(boolean isReset) {
        Disposable disposable = CommonApi.getGroup()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(lockGroupResult -> {
                    List<LockGroup> list = lockGroupResult.getData();
                    if (list == null) return;
                    LockGroup[] lockGroups = new LockGroup[list.size()];
                    if (isReset) {//刷新时清除所有组数据
                        LockKeyDaoImpl.get().deleteAll();
                    }
                    LockGroupDaoImpl.get().insertAll(list.toArray(lockGroups));
                    if (isReset)
                        refreshStatus.postValue(true);
                }, throwable -> {
                    throwable.printStackTrace();
                    //TODO
                    refreshStatus.postValue(false);
                });
        mCompositeDisposable.add(disposable);
    }

    /**
     * 获取分组的锁
     *
     * @param lockGroup
     * @return
     */
    public void getGroupLockList(LockGroup lockGroup) {
        currentGroupId = lockGroup.getId();
        if (lockGroup.getId() == -1) {//当组id为-1时获取全部锁
            getLockKeyFromDb();
        } else {
            lock2 = LockKeyDaoImpl.get().getLockByGroupId(lockGroup.getId());
            lock2.observeForever(lockKeys -> {
                if (currentGroupId == -1 || lockKeys == null) return;
//                if (mIsReset && lockKeys.size() < 1) return;
                mLockKeys.setValue(lockKeys);
            });
        }
    }


    @Override

    protected void onCleared() {
        super.onCleared();
        mCompositeDisposable.dispose();
//        lock1.removeObservers(getApplication());
//        lock2.removeObserver(getApplication());
    }

    //自动查询锁设备并开锁
    public void autoOpenLock() {
        isStopScan = false;
        scan();
    }

    private void scan() {

        if (isStopScan) {
            return;
        }

        if (isScaning) {
            return;
        }

        isFindDevice = false;
        mLockKey = null;

        int scanResult = UnilinkManager.getInstance(getApplication()).scan(new ScanListener() {
            @Override
            public void onScan(ScanDevice scanDevice) {
                List<LockKey> lockKeys = mLockKeys.getValue();
                if (lockKeys == null || lockKeys.size() <= 0) {
                    return;
                }

                for (LockKey lockKey : lockKeys) {
                    if (scanDevice.getAddress().equalsIgnoreCase(lockKey.getMac()) && lockKey.getCanOpen() == 1) {
                        isFindDevice = true;
                        mLockKey = lockKey;
                        UnilinkManager.getInstance(getApplication()).stopScan();
                        isScaning = false;
                        connect(scanDevice);
                    }
                }
            }

            @Override
            public void onScanTimeout() {
                handleScanEnd();
            }

            @Override
            public void onFinish(List<ScanDevice> scanDevices) {
                handleScanEnd();
            }
        }, 10);

        if (scanResult == UnilinkManager.SCAN_SUCCESS) {
            isScaning = true;
            Log.i("autoOpenLock", "开始扫描");
        }
    }

    private void delayScan() {
        handler.postDelayed(() -> scan(), 3000);
    }

    private void handleScanEnd() {
        isScaning = false;
        Log.i("autoOpenLock", "结束扫描");
        if (!isFindDevice) {
            delayScan();
        }

    }

    private void connect(ScanDevice scanDevice) {
        Log.i("autoOpenLock", "连接设备");
        UnilinkManager.getInstance(getApplication()).connect(scanDevice, new ConnectListener() {
            @Override
            public void onConnect() {
                Log.i("autoOpenLock", "连接成功------");
                openLock();
            }

            @Override
            public void onDisconnect(int code, String message) {
                Log.i("autoOpenLock", "连接失败------" + message);
                delayScan();
            }
        });
    }

    private void openLock() {
        if (mLockKey == null) {
            return;
        }

        UnilinkManager.getInstance(getApplication()).setConnectListener(null);
        int lockType = mLockKey.getType();
        if (lockType == EnumCollection.LockType.SMARTLOCK.getType()) {
            UnilinkManager.getInstance(getApplication()).openGateLock(mLockKey.getMac(), mLockKey.getEncryptType(),
                    mLockKey.getEncryptKey(), Base64.decode(mLockKey.getBlueKey()), new CallBack2<Void>() {
                        @Override
                        public void onSuccess(Void data) {
                            Log.i("autoOpenLock", "开锁成功");
                            UnilinkManager.getInstance(getApplication()).close(mLockKey.getMac());
                            delayScan();
                        }

                        @Override
                        public void onFailed(int errCode, String errMsg) {
                            Log.i("autoOpenLock", "开锁失败:" + errMsg);
                            UnilinkManager.getInstance(getApplication()).close(mLockKey.getMac());
                            delayScan();
                        }
                    });
        } else {
            UnilinkManager.getInstance(getApplication()).openCloudLock(mLockKey.getMac(), mLockKey.getEncryptType(),
                    mLockKey.getEncryptKey(), Base64.decode(mLockKey.getBlueKey()), new CallBack2<Void>() {
                        @Override
                        public void onSuccess(Void data) {
                            Log.i("autoOpenLock", "开锁成功");
                            UnilinkManager.getInstance(getApplication()).close(mLockKey.getMac());
                            delayScan();
                        }

                        @Override
                        public void onFailed(int errCode, String errMsg) {
                            Log.i("autoOpenLock", "开锁失败:" + errMsg);
                            UnilinkManager.getInstance(getApplication()).close(mLockKey.getMac());
                            delayScan();
                        }
                    });

        }
    }

    public void stopAutoOpenLock() {
        isStopScan = true;
        UnilinkManager.getInstance(getApplication()).stopScan();
    }


}
