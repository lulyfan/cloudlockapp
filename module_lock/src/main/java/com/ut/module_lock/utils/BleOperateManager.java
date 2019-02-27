package com.ut.module_lock.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.Nullable;

import com.ut.base.AppManager;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.base.Utils.RomUtils;
import com.ut.base.Utils.UTLog;
import com.ut.base.dialog.CustomerAlertDialog;
import com.ut.module_lock.R;
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
import java.util.concurrent.atomic.AtomicInteger;

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
    public static final int ERROR_LOCKRESET = -103;//锁被重置或未激活
    private OperateCallback mOperateCallback = null;

    private boolean canScan = true;

    private OperateDeviceKeyCallback mOperateDeviceKeyCallback = null;

    private OperateDeviceRuleCallback mOperateDeviceRuleCallback = null;

    private int lockType = -1;

    private CustomerAlertDialog mPermissionDialog = null;

    private AtomicInteger mOperateStatus = new AtomicInteger(OperateStatus.STATUS_SCAN_INSTIAL);

    private class OperateStatus {
        public static final int STATUS_SCAN_INSTIAL = 0;
        public static final int STATUS_SCANING = 1;
        public static final int STATUS_SCANED = 2;
    }

    public void setOperateDeviceKeyDetailCallback(OperateDeviceKeyDetailCallback operateDeviceKeyDetailCallback) {
        mOperateDeviceKeyDetailCallback = operateDeviceKeyDetailCallback;
    }

    public void setOperateDeviceRuleCallback(OperateDeviceRuleCallback operateDeviceRuleCallback) {
        mOperateDeviceRuleCallback = operateDeviceRuleCallback;
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

    public void setConnectListener() {
        UnilinkManager.getInstance(mContext).setConnectListener(mConnectListener);
        isConnectSuccessed = true;
    }

    public void disconnect(String mac) {
        UnilinkManager.getInstance(mContext).disconnect(mac);
    }

    public void scanDevice(int type, Activity activity) {
        UTLog.i("to scan 0");
        this.lockType = type;
        if (RomUtils.isFlymeRom()//当为魅族手机时需要提醒用户打开定位服务
                && !checkGpsAndOpenLock())
            return;
        if (mOperateStatus.get() != OperateStatus.STATUS_SCAN_INSTIAL) return;
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
        if (scanResult == UnilinkManager.SCAN_SUCCESS) {
            mOperateStatus.set(OperateStatus.STATUS_SCANING);
        }
    }

    private int toScanDevice(int type) {
        UTLog.i("to scan 1");
        if (type == -1) return ERROR_SCANTIMEOUT;
        return UnilinkManager.getInstance(mContext).scan(new ScanListener() {
            @Override
            public void onScan(ScanDevice scanDevice) {
                if (mOperateCallback != null
                        && mOperateCallback.checkScanResult(scanDevice)) {
                    if (mOperateStatus.compareAndSet(OperateStatus.STATUS_SCANING, OperateStatus.STATUS_SCANED)) {
                        UnilinkManager.getInstance(mContext).stopScan();
                        if (mOperateCallback != null) {
                            if (!scanDevice.isActive()) {
                                mOperateCallback.onScanFaile(ERROR_LOCKRESET);
                            } else {
                                mOperateCallback.onScanSuccess(scanDevice);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFinish(List<ScanDevice> scanDevices) {
                UTLog.i("onScanFinish");
                if (mOperateStatus.get() != OperateStatus.STATUS_SCANED
                        && mOperateCallback != null) {
                    UTLog.i("onScanTimeout 1");
                    mOperateCallback.onScanFaile(ERROR_SCANTIMEOUT);
                }
            }

            @Override
            public void onScanTimeout() {
                UTLog.i("onScanTimeout");
                if (mOperateCallback != null) {
                    UTLog.i("onScanTimeout 1");
                    mOperateCallback.onScanFaile(ERROR_SCANTIMEOUT);
                }
                mOperateStatus.set(OperateStatus.STATUS_SCAN_INSTIAL);
            }
        }, 10);
    }

    public void connect(ScanDevice scanDevice, int enctyptType, String enctyptKey) {
        isConnectSuccessed = false;
        UnilinkManager.getInstance(mContext).setConnectListener(null);
        UnilinkManager.getInstance(mContext).connect(scanDevice, enctyptType, enctyptKey,
                mConnectListener, mLockStateListener);
    }

    ConnectListener mConnectListener = new ConnectListener() {
        @Override
        public void onConnect() {
            isConnectSuccessed = true;
            if (mOperateCallback != null) {
                mOperateCallback.onConnectSuccess();
            }
            mOperateStatus.set(OperateStatus.STATUS_SCAN_INSTIAL);
        }

        @Override
        public void onDisconnect(int i, String s) {
            UTLog.i("onDisconnect:" + i + " s:" + s);
            mOperateStatus.set(OperateStatus.STATUS_SCAN_INSTIAL);
            if (!isConnectSuccessed) {
                if (mOperateCallback != null) {
                    isConnectSuccessed = false;
                    mOperateCallback.onConnectFailed(i, s);
                }
            } else {
                if (mOperateCallback != null) {
                    mOperateCallback.onDisconnect();
                }
            }
        }
    };

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

    public void writeDeviceKey(String mac, int encryptType,
                               String encryptKey, List<GateLockKey> gateLockKeys) {
        UnilinkManager.getInstance(mContext).writeKeyInfos(mac,
                encryptType, encryptKey, gateLockKeys, new CallBack2<Void>() {
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

    public void addDeviceKeyAuth(String mac, int encryptType,
                                 String encryptKey, AuthInfo authInfo) {
        UnilinkManager.getInstance(mContext).addAuth(mac, encryptType,
                encryptKey, authInfo, new CallBack2<Integer>() {
                    @Override
                    public void onSuccess(Integer data) {
                        if (mOperateDeviceRuleCallback != null) {
                            mOperateDeviceRuleCallback.onAddDevicekeyAuthSuccess(data);
                        }
                    }

                    @Override
                    public void onFailed(int errCode, String errMsg) {
                        if (mOperateDeviceRuleCallback != null) {
                            mOperateDeviceRuleCallback.onAddDeviceKeyAuthFailed(errCode, errMsg);
                        }
                    }
                });
    }

    public void updateDeviceKeyAuth(String mac, int
            encryptType, String encryptKey, AuthInfo authInfo) {
        UnilinkManager.getInstance(mContext).updateAuth(mac, encryptType, encryptKey, authInfo, new CallBack2<Void>() {
            @Override
            public void onSuccess(Void data) {
                if (mOperateDeviceRuleCallback != null) {
                    mOperateDeviceRuleCallback.onUpdateDeviceKeyAuthSuccess();
                }
            }

            @Override
            public void onFailed(int errCode, String errMsg) {
                if (mOperateDeviceRuleCallback != null) {
                    mOperateDeviceRuleCallback.onUpdateDeviceKeyAuthFailed(errCode, errMsg);
                }
            }
        });
    }

    public void deleteDeviceKeyAuth(String mac, int encryptType,
                                    String encryptKey, int authId) {
        UnilinkManager.getInstance(mContext).deleteAuth(mac, encryptType, encryptKey, authId, new CallBack2<Void>() {
            @Override
            public void onSuccess(Void data) {
                if (mOperateDeviceRuleCallback != null) {
                    mOperateDeviceRuleCallback.onDeleteDeviceKeyAuthSuccess();
                }
            }

            @Override
            public void onFailed(int errCode, String errMsg) {
                if (mOperateDeviceRuleCallback != null) {
                    mOperateDeviceRuleCallback.onDeleteDeviceKeyAuthFailed(errCode, errMsg);
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


    public void onActivityResult(Activity activity, int requestCode
            , int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK &&
                requestCode == BLEENABLECODE) {
            scanDevice(lockType, activity);
            UTLog.i("to scan");
        }
    }

    public void onActivityOnpause() {
        if (mPermissionDialog != null && mPermissionDialog.isShowing()) {
            mPermissionDialog.dismiss();
        }
    }

    public void onRequestPermissionsResult(Activity activity, int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == BLEREAUESTCODE && grantResults.length > 0) {
            for (int i : grantResults) {
                if (i != PackageManager.PERMISSION_GRANTED) {//定位未允许的时候去
                    mPermissionDialog = new CustomerAlertDialog(activity, false)
                            .setMsg(activity.getString(R.string.lock_location_need_tips))
                            .setCancelLister(v -> {
                            })
                            .setConfirmListener(v -> {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                                activity.startActivity(intent);
                            });
                    mPermissionDialog.show();
                    return;
                }
            }
            scanDevice(lockType, activity);
        }
    }

    private boolean checkGpsAndOpenLock() {
        if (!SystemUtils.isGPSOpen(AppManager.getAppManager().currentActivity())) {
            new CustomerAlertDialog(AppManager.getAppManager().currentActivity(), false)
                    .setMsg(AppManager.getAppManager().currentActivity().getString(R.string.lock_gps_open_tips))
                    .setCancelLister(v -> {
                    })
                    .setConfirmListener(v -> {
                        // 转到手机设置界面，用户设置GPS
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        AppManager.getAppManager().currentActivity().startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                    })
                    .show();
            return false;
        }
        return true;
    }

    public interface OperateCallback {
        void onStartScan();

        //检查是否是需要的结果
        boolean checkScanResult(ScanDevice scanDevice);

        void onScanSuccess(ScanDevice scanDevice);

        void onScanFaile(int errorCode);

        void onConnectSuccess();

        void onConnectFailed(int errorcode, String errorMsg);

        void onDisconnect();

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

    public interface OperateDeviceRuleCallback {
        void onAddDevicekeyAuthSuccess(int authId);

        void onAddDeviceKeyAuthFailed(int errCode, String errMsg);

        void onUpdateDeviceKeyAuthSuccess();

        void onUpdateDeviceKeyAuthFailed(int errCode, String errMsg);

        void onDeleteDeviceKeyAuthSuccess();

        void onDeleteDeviceKeyAuthFailed(int errCode, String errMsg);
    }
}