package com.ut.base;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.example.entity.base.Result;
import com.example.operation.CommonApi;
import com.example.operation.MyRetrofit;
import com.google.gson.JsonElement;
import com.ut.base.Utils.UTLog;
import com.ut.base.Utils.UploadOfflineRecordUtil;
import com.ut.database.entity.EnumCollection;
import com.ut.database.entity.LockKey;
import com.ut.database.entity.OfflineRecord;
import com.ut.unilink.UnilinkManager;
import com.ut.unilink.cloudLock.CallBack2;
import com.ut.unilink.cloudLock.ConnectListener;
import com.ut.unilink.cloudLock.ScanDevice;
import com.ut.unilink.cloudLock.ScanListener;
import com.ut.unilink.util.Base64;
import com.ut.unilink.util.Log;

import java.util.Date;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class AutoOpenLockService extends Service {

    private boolean isFindDevice;
    private LockKey mLockKey;   //本次要开的锁
    public boolean isStopScan;
    public boolean isScaning;
    private Handler handler = new Handler(Looper.getMainLooper());
    private List<LockKey> mLockKeys;
    private int connectCount;
    private int connectFailedCount;
    private int openLockCount;
    private int openLockFailedCount;

    public AutoOpenLockService() {

    }

    private IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {

        public AutoOpenLockService getService() {
            return AutoOpenLockService.this;
        }
    }

    public void setLockKeys(List<LockKey> lockKeys) {
        mLockKeys = lockKeys;
    }

    private boolean isNeedAutoOpenLock() {
        List<LockKey> lockKeys = mLockKeys;
        if (lockKeys == null || lockKeys.size() <= 0) {
            return false;
        }

        for (LockKey lockKey : lockKeys) {
            if (lockKey.getCanOpen() == 1
                    && EnumCollection.KeyStatus.isKeyValid(lockKey.getKeyStatus())) {
                return true;
            }
        }
        return false;
    }

    //自动查询锁设备并开锁
    public void startAutoOpenLock() {
        if (!isNeedAutoOpenLock()) {
            return;
        }

        if (!UnilinkManager.getInstance(this).checkState()) {
            return;
        }

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
                List<LockKey> lockKeys = mLockKeys;
                if (lockKeys == null || lockKeys.size() <= 0) {
                    return;
                }

                for (LockKey lockKey : lockKeys) {
                    if (scanDevice.getAddress().equalsIgnoreCase(lockKey.getMac()) && lockKey.getCanOpen() == 1
                            && EnumCollection.KeyStatus.isKeyValid(lockKey.getKeyStatus())) {
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
            Log.i("autoOpenLock", "-------------------------连接次数:" + connectCount + "\t连接失败次数:" + connectFailedCount +
                    "\t\t开锁次数:" + openLockCount + "\t开锁失败次数:" + openLockFailedCount);
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
        connectCount++;
        UnilinkManager.getInstance(getApplication()).connect(scanDevice, new ConnectListener() {
            @Override
            public void onConnect() {
                Log.i("autoOpenLock", "连接成功------");
                openLock();
            }

            @Override
            public void onDisconnect(int code, String message) {
                Log.i("autoOpenLock", "连接失败------" + message);
                connectFailedCount++;
                delayScan();
            }
        });
    }

    private void openLock() {
        if (mLockKey == null) {
            return;
        }

        openLockCount++;
        UnilinkManager.getInstance(getApplication()).setConnectListener(null);
        int lockType = mLockKey.getType();
        if (lockType == EnumCollection.LockType.SMARTLOCK.getType()) {
            UnilinkManager.getInstance(getApplication()).openGateLock(mLockKey.getMac(), mLockKey.getEncryptType(),
                    mLockKey.getEncryptKey(), Base64.decode(mLockKey.getBlueKey()), new CallBack2<Void>() {
                        @Override
                        public void onSuccess(Void data) {
                            Log.i("autoOpenLock", "开锁成功");
                            addOpenLockLog(1);
                            UnilinkManager.getInstance(getApplication()).close(mLockKey.getMac());
                            delayScan();
                        }

                        @Override
                        public void onFailed(int errCode, String errMsg) {
                            Log.i("autoOpenLock", "开锁失败:" + errMsg);
                            openLockFailedCount++;
                            addOpenLockLog(2);
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
                            addOpenLockLog(1);
                            UnilinkManager.getInstance(getApplication()).close(mLockKey.getMac());
                            delayScan();
                        }

                        @Override
                        public void onFailed(int errCode, String errMsg) {
                            Log.i("autoOpenLock", "开锁失败:" + errMsg);
                            addOpenLockLog(2);
                            UnilinkManager.getInstance(getApplication()).close(mLockKey.getMac());
                            delayScan();
                        }
                    });

        }
    }

    public void stopAutoOpenLock() {
        isStopScan = true;
        connectCount = 0;
        connectFailedCount = 0;
        openLockCount = 0;
        openLockFailedCount = 0;
        UnilinkManager.getInstance(getApplication()).stopScan();
    }

    @SuppressLint("CheckResult")
    private void addOpenLockLog(int type) {

        long lockId = Long.parseLong(mLockKey.getId());
        long keyId = mLockKey.getKeyId();
        int openLockType = EnumCollection.OpenLockType.BLEAUTO.ordinal();
        int electric = mLockKey.getElectric();

        CommonApi.addLog(lockId, keyId, type, openLockType, electric)
                .doOnNext(voidResult -> {
                    if (voidResult == null) {
                        throw new NullPointerException(getApplicationContext().getString(R.string.serviceErr));
                    }

                    if (!voidResult.isSuccess()) {
                        throw new Exception(voidResult.msg);
                    }
                })
                .subscribe(jsonElementResult -> {
                    UTLog.i(jsonElementResult.toString());
                }, throwable -> {
                    throwable.printStackTrace();

                    OfflineRecord offlineRecord = new OfflineRecord();
                    offlineRecord.setLockId(lockId);
                    offlineRecord.setKeyId(keyId);
                    offlineRecord.setType(type);
                    offlineRecord.setOpenLockType(openLockType);
                    offlineRecord.setElectric(electric);
                    offlineRecord.setCreateTime(new Date().getTime());
                    UploadOfflineRecordUtil.uploadBatch(offlineRecord);
                });
    }
}
