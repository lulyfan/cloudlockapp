package com.ut.module_lock.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.ut.base.Utils.UTLog;
import com.ut.unilink.UnilinkManager;
import com.ut.unilink.cloudLock.CallBack2;
import com.ut.unilink.cloudLock.ConnectListener;
import com.ut.unilink.cloudLock.LockStateListener;
import com.ut.unilink.cloudLock.ScanDevice;
import com.ut.unilink.cloudLock.ScanListener;
import com.ut.unilink.cloudLock.protocol.data.AuthCountInfo;
import com.ut.unilink.cloudLock.protocol.data.AuthInfo;
import com.ut.unilink.cloudLock.protocol.data.GateLockKey;
import com.ut.unilink.cloudLock.protocol.data.GateLockOperateRecord;
import com.ut.unilink.cloudLock.protocol.data.LockState;

import java.util.List;

/**
 * author : zhouyubin
 * time   : 2019/01/16
 * desc   :
 * version: 1.0
 */
public class BleOperateManager {
    private static BleOperateManager instance = null;
    private boolean isConnectSuccessed = false;
    private static final int BLEREAUESTCODE = 101;
    private static final int BLEENABLECODE = 102;

    public static final int ERROR_SCANTIMEOUT = -101;
    public static final int ERROR_SCANNOTSUPPORT = -102;
    private OperateCallback mOperateCallback = null;

    private boolean canScan = true;

    private OperateDeviceKeyCallback mOperateDeviceKeyCallback = null;
    private int lockType = -1;

    public void setOperateDeviceKeyDetailCallback(OperateDeviceKeyDetailCallback operateDeviceKeyDetailCallback) {
        mOperateDeviceKeyDetailCallback = operateDeviceKeyDetailCallback;
    }

    private OperateDeviceKeyDetailCallback mOperateDeviceKeyDetailCallback = null;


    public void setOperateCallback(OperateCallback operateCallback) {
        mOperateCallback = operateCallback;
    }

    public void setOperateDeviceKeyCallback(OperateDeviceKeyCallback operateDeviceKeyCallback) {
        mOperateDeviceKeyCallback = operateDeviceKeyCallback;
    }

    private Context mContext = null;

    public BleOperateManager(Context context) {
        mContext = context;
    }

    public boolean isConnected(String mac) {
        return UnilinkManager.getInstance(mContext).isConnect(mac);
    }

    public void disconnect(String mac) {
        UnilinkManager.getInstance(mContext).disconnect(mac);
    }

    public void scanDevice(int type, Activity activity) {
        UTLog.i("to scan 0");
        this.lockType = type;
        int scanResult = toScanDevice(type);
        switch (scanResult) {
            case UnilinkManager.BLE_NOT_SUPPORT:
                if (mOperateCallback != null) {
                    mOperateCallback.onScanFaile(ERROR_SCANNOTSUPPORT);
                }
                break;
            case UnilinkManager.NO_LOCATION_PERMISSION:
                UnilinkManager.getInstance(mContext).requestPermission(activity, BLEREAUESTCODE);
                break;
            case UnilinkManager.BLE_NOT_OPEN:
                UnilinkManager.getInstance(mContext).enableBluetooth(activity, BLEENABLECODE);
                break;
            case UnilinkManager.SCAN_SUCCESS:
                UTLog.i("to scan 2");
                if (mOperateCallback != null) {
                    mOperateCallback.onStartScan();
                }
                break;
        }
    }

    private int toScanDevice(int type) {
        UTLog.i("to scan 1");
        if (type == -1) return ERROR_SCANTIMEOUT;
        canScan = true;
        return UnilinkManager.getInstance(mContext).scan(new ScanListener() {
            @Override
            public void onScan(ScanDevice scanDevice, List<ScanDevice> list) {
                if (!canScan) return;
                if (mOperateCallback != null
                        && mOperateCallback.checkScanResult(scanDevice)) {
                    canScan = false;
                    UnilinkManager.getInstance(mContext).stopScan();
                    if (mOperateCallback != null) {
                        mOperateCallback.onScanSuccess(scanDevice);
                    }
                }
            }

            @Override
            public void onFinish() {
                if (mOperateCallback != null) {
                    mOperateCallback.onScanFaile(ERROR_SCANTIMEOUT);
                }
            }
        }, 10);
    }


    public void connect(ScanDevice scanDevice, int enctyptType, String enctyptKey) {
        isConnectSuccessed = false;
        UnilinkManager.getInstance(mContext).connect(scanDevice, enctyptType, enctyptKey,
                new ConnectListener() {
                    @Override
                    public void onConnect() {
                        isConnectSuccessed = true;
                        if (mOperateCallback != null) {
                            mOperateCallback.onConnectSuccess();
                        }
                    }

                    @Override
                    public void onDisconnect(int i, String s) {
                        UTLog.i("onDisconnect:" + i + " s:" + s);
                        if (!isConnectSuccessed) {
                            if (mOperateCallback != null) {
                                mOperateCallback.onConnectFailed(i, s);
                            }
                        }
                    }
                }, mLockStateListener);
    }

    public void updateTime(String mac, int enctyptType, String enctyptKey, long time) {
        UnilinkManager.getInstance(mContext).writeTime(mac, enctyptType, enctyptKey, time, new CallBack2<Void>() {
            @Override
            public void onSuccess(Void data) {
                if (mOperateDeviceKeyCallback != null) {
                    mOperateDeviceKeyCallback.onWriteTimeSuccess();
                }
            }

            @Override
            public void onFailed(int errCode, String errMsg) {
                if (mOperateDeviceKeyCallback != null) {
                    mOperateDeviceKeyCallback.onWriteTimeFailed(errCode, errMsg);
                }
            }
        });
    }


    public void readKeyInfo(String mac, int encryptType, String encryptKey) {
        UnilinkManager.getInstance(mContext).readKeyInfos(mac, encryptType, encryptKey, new CallBack2<List<GateLockKey>>() {
            @Override
            public void onSuccess(List<GateLockKey> data) {
                if (mOperateDeviceKeyCallback != null) {
                    mOperateDeviceKeyCallback.onReadKeyInfoSuccess(data);
                }
            }

            @Override
            public void onFailed(int errCode, String errMsg) {
                if (mOperateDeviceKeyCallback != null) {
                    mOperateDeviceKeyCallback.onReadKeyInfoFailed(errCode, errMsg);
                }
            }
        });
    }

    public void queryAllAuth(String mac, int encryptType, String encryptKey) {
        UnilinkManager.getInstance(mContext).queryAllAuth(mac, encryptType, encryptKey, new CallBack2<List<AuthInfo>>() {
            @Override
            public void onSuccess(List<AuthInfo> data) {
                if (mOperateDeviceKeyCallback != null) {
                    mOperateDeviceKeyCallback.onQueryAllAuthSuccess(data);
                }
            }

            @Override
            public void onFailed(int errCode, String errMsg) {
                if (mOperateDeviceKeyCallback != null) {
                    mOperateDeviceKeyCallback.onQueryAllAuthFailed(errCode, errMsg);
                }
            }
        });
    }

    public void readAuthCountInfo(String mac, int encryptType, String encryptKey) {
        UnilinkManager.getInstance(mContext).readAuthCountInfo(mac, encryptType, encryptKey, new CallBack2<List<AuthCountInfo>>() {
            @Override
            public void onSuccess(List<AuthCountInfo> data) {
                if (mOperateDeviceKeyCallback != null) {
                    mOperateDeviceKeyCallback.onReadAuthCountSuccess(data);
                }
            }

            @Override
            public void onFailed(int errCode, String errMsg) {
                if (mOperateDeviceKeyCallback != null) {
                    mOperateDeviceKeyCallback.onReadAuthCountFailed(errCode, errMsg);
                }
            }
        });
    }

    public void readOpenRecord(String mac, int encryptType, String encryptKey, int readSerialNum) {
        UnilinkManager.getInstance(mContext).readGateLockOpenLockRecord(mac, encryptType, encryptKey, readSerialNum, new CallBack2<List<GateLockOperateRecord>>() {
            @Override
            public void onSuccess(List<GateLockOperateRecord> data) {
                if (mOperateDeviceKeyCallback != null) {
                    mOperateDeviceKeyCallback.onReadRecordSuccess(data);
                }
            }

            @Override
            public void onFailed(int errCode, String errMsg) {
                if (mOperateDeviceKeyCallback != null) {
                    mOperateDeviceKeyCallback.onReadRecordFailed(errCode, errMsg);
                }
            }
        });
    }

    public void deleteDeviceKey(String mac, int encryptType, String encryptKey, int keyId) {
        UnilinkManager.getInstance(mContext).deleteKey(mac, encryptType, encryptKey, keyId, new CallBack2<Void>() {
            @Override
            public void onSuccess(Void data) {
                if (mOperateDeviceKeyDetailCallback != null) {
                    mOperateDeviceKeyDetailCallback.onDeleteSuccess();
                }
            }

            @Override
            public void onFailed(int errCode, String errMsg) {
                if (mOperateDeviceKeyDetailCallback != null) {
                    mOperateDeviceKeyDetailCallback.onDeleteFailed(errCode, errMsg);
                }
            }
        });
    }

    public void writeDeviceKey(String mac, int encryptType, String encryptKey, List<GateLockKey> gateLockKeys) {
        UnilinkManager.getInstance(mContext).writeKeyInfos(mac, encryptType, encryptKey, gateLockKeys, new CallBack2<Void>() {
            @Override
            public void onSuccess(Void data) {
                if (mOperateDeviceKeyDetailCallback != null) {
                    mOperateDeviceKeyDetailCallback.onWriteDeviceKeySuccess();
                }
            }

            @Override
            public void onFailed(int errCode, String errMsg) {
                if (mOperateDeviceKeyDetailCallback != null) {
                    mOperateDeviceKeyDetailCallback.onWriteDeviceKeyFailed(errCode, errMsg);
                }
            }
        });
    }

    private LockStateListener mLockStateListener = new LockStateListener() {
        @Override
        public void onState(LockState lockState) {
            if (mOperateCallback != null) {
                mOperateCallback.onElectric(lockState.getElect());
            }
        }
    };


    public void onActivityResult(Activity activity, int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == BLEREAUESTCODE || requestCode == BLEENABLECODE) {
                scanDevice(lockType, activity);
                UTLog.i("to scan");
            }
        }
    }

    public void onRequestPermissionsResult(Activity activity, int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == BLEREAUESTCODE) {
            scanDevice(lockType, activity);
            UTLog.i("to scan");
        }
    }


    public interface OperateCallback {
        void onStartScan();

        //检查是否是需要的结果
        boolean checkScanResult(ScanDevice scanDevice);

        void onScanSuccess(ScanDevice scanDevice);

        void onScanFaile(int errorCode);

        void onConnectSuccess();

        void onConnectFailed(int errorcode, String errorMsg);

        void onElectric(int elect);

    }

    public interface OperateDeviceKeyCallback {
        void onWriteTimeSuccess();

        void onWriteTimeFailed(int code, String msg);

        void onReadKeyInfoSuccess(List<GateLockKey> data);

        void onReadKeyInfoFailed(int code, String msg);

        void onQueryAllAuthSuccess(List<AuthInfo> data);

        void onQueryAllAuthFailed(int code, String msg);

        void onReadAuthCountSuccess(List<AuthCountInfo> data);

        void onReadAuthCountFailed(int code, String msg);

        void onReadRecordSuccess(List<GateLockOperateRecord> data);

        void onReadRecordFailed(int errorcode, String errorMsg);
    }

    public interface OperateDeviceKeyDetailCallback {
        void onDeleteSuccess();

        void onDeleteFailed(int errorcode, String errorMsg);

        void onWriteDeviceKeySuccess();

        void onWriteDeviceKeyFailed(int errCode, String errMsg);
    }
}