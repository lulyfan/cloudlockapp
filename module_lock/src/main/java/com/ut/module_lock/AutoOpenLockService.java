package com.ut.module_lock;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.ut.database.entity.EnumCollection;
import com.ut.database.entity.LockKey;
import com.ut.unilink.UnilinkManager;
import com.ut.unilink.cloudLock.CallBack2;
import com.ut.unilink.cloudLock.ConnectListener;
import com.ut.unilink.cloudLock.ScanDevice;
import com.ut.unilink.cloudLock.ScanListener;
import com.ut.unilink.util.Base64;
import com.ut.unilink.util.Log;

import java.util.List;

public class AutoOpenLockService extends Service {

    private boolean isFindDevice;
    private LockKey mLockKey;   //本次要开的锁
    public boolean isStopScan;
    public boolean isScaning;
    private Handler handler = new Handler(Looper.getMainLooper());
    private List<LockKey> mLockKeys;

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

    //自动查询锁设备并开锁
    public void startAutoOpenLock() {
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