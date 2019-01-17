package com.ut.module_lock.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.entity.base.Result;
import com.example.operation.MyRetrofit;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.LockKey;
import com.ut.module_lock.R;
import com.ut.unilink.UnilinkManager;
import com.ut.unilink.cloudLock.CallBack2;
import com.ut.unilink.cloudLock.ConnectListener;
import com.ut.unilink.cloudLock.ScanDevice;
import com.ut.unilink.cloudLock.ScanListener;

import java.util.List;

import io.reactivex.functions.Consumer;

public class TimeAdjustVM extends BaseViewModel {

    public MutableLiveData<Long> lockTime = new MutableLiveData<>();
    public MutableLiveData<String> tip = new MutableLiveData<>();
    public MutableLiveData<Integer> state = new MutableLiveData<>();
    public String mac;
    private int operate;
    private boolean isFindDevice;
    private long time;  //用于写入锁时间

    private static final int READ_TIME = 0;
    private static final int WRITE_TIME = 1;

    public static final int STATE_DEFAULT = -1;
    public static final int STATE_FAILED = -2;
    public static final int STATE_SCAN = 0;
    public static final int STATE_CONNECT = 1;
    public static final int STATE_READ_TIME = 2;
    public static final int STATE_WRITE_TIME = 3;

    public TimeAdjustVM(@NonNull Application application) {
        super(application);
        state.setValue(STATE_DEFAULT);
    }

    public void readLockTime() {
        boolean isConnect = UnilinkManager.getInstance(getApplication()).isConnect(mac);
        if (!isConnect) {
            scan(READ_TIME);
        } else {
            sendReadTimeCmd();
        }
    }

    public void wirteLockTime() {
        boolean isConnect = UnilinkManager.getInstance(getApplication()).isConnect(mac);
        if (!isConnect) {
            scan(WRITE_TIME);
        } else {
            sendWriteTimeCmd();
        }
    }

    private void scan(int operate) {
        this.operate = operate;
        isFindDevice = false;

        int scanResult = UnilinkManager.getInstance(getApplication()).scan(new ScanListener() {
            @Override
            public void onScan(ScanDevice scanDevice, List<ScanDevice> scanDevices) {
                if (scanDevice.getAddress().equals(mac)) {
                    isFindDevice = true;
                    connect(scanDevice);
                }
            }

            @Override
            public void onFinish() {
                if (!isFindDevice) {
                    tip.postValue(getApplication().getString(R.string.wakeupDevice));
                    state.postValue(STATE_FAILED);
                }
            }
        }, 10);

        switch (scanResult) {
            case UnilinkManager.BLE_NOT_SUPPORT:
                tip.postValue(getApplication().getString(R.string.BLE_NOT_SUPPORT));
                break;

            case UnilinkManager.BLE_NOT_OPEN:
                tip.postValue(getApplication().getString(R.string.BLE_NOT_OPEN));
                break;

            case UnilinkManager.NO_LOCATION_PERMISSION:
                tip.postValue(getApplication().getString(R.string.NO_LOCATION_PERMISSION));
                break;

            case UnilinkManager.SCAN_SUCCESS:
                state.postValue(STATE_SCAN);
                break;

            default:
        }

        if (scanResult != UnilinkManager.SCAN_SUCCESS) {
            state.postValue(STATE_FAILED);
        }
    }

    private void connect(ScanDevice scanDevice) {
        state.postValue(STATE_CONNECT);

        UnilinkManager.getInstance(getApplication()).connect(scanDevice, new ConnectListener() {
            @Override
            public void onConnect() {
                UnilinkManager.getInstance(getApplication()).setConnectListener(null);
                if (operate == READ_TIME) {
                    sendReadTimeCmd();
                } else if (operate == WRITE_TIME) {
                    sendWriteTimeCmd();
                }
            }

            @Override
            public void onDisconnect(int code, String message) {
                tip.postValue(getApplication().getString(R.string.err_connect));
                state.postValue(STATE_FAILED);
            }
        });
    }

    private void sendReadTimeCmd() {
        state.postValue(STATE_READ_TIME);
        LiveData<LockKey> lockKeyLiveData = CloudLockDatabaseHolder.get().getLockKeyDao().getByMac(mac);
        lockKeyLiveData.observeForever(lockKey ->
                UnilinkManager.getInstance(getApplication()).readTime(mac, lockKey.getEncryptType(), lockKey.getEncryptKey(),
                        new CallBack2<Long>() {
                            @Override
                            public void onSuccess(Long data) {
                                lockTime.postValue(data);
                                state.postValue(STATE_DEFAULT);
                            }

                            @Override
                            public void onFailed(int errCode, String errMsg) {
                                tip.postValue(getApplication().getString(R.string.err_readLockTime) + errMsg);
                                state.postValue(STATE_FAILED);
                            }
                        }));

    }

    private void sendWriteTimeCmd() {
        state.postValue(STATE_WRITE_TIME);
        LiveData<LockKey> lockKeyLiveData = CloudLockDatabaseHolder.get().getLockKeyDao().getByMac(mac);
        lockKeyLiveData.observeForever(lockKey ->
                UnilinkManager.getInstance(getApplication()).writeTime(mac, lockKey.getEncryptType(), lockKey.getEncryptKey(), time,
                        new CallBack2<Void>() {
                            @Override
                            public void onSuccess(Void data) {
                                tip.postValue(getApplication().getString(R.string.operateSuccess));
                                state.postValue(STATE_DEFAULT);
                                readLockTime();
                            }

                            @Override
                            public void onFailed(int errCode, String errMsg) {
                                tip.postValue(getApplication().getString(R.string.err_writeLockTime) + errMsg);
                                state.postValue(STATE_FAILED);
                            }
                        }));

    }

    public void adjustTime() {
        MyRetrofit.get().getCommonApiService().checkTime()
                .doOnNext(stringResult -> {
                    if (stringResult == null) {
                        throw new NullPointerException(getApplication().getString(R.string.serviceErr));
                    }

                    if (!stringResult.isSuccess()) {
                        throw new Exception(stringResult.msg);
                    }
                })
                .subscribe(longResult -> {
                    time = longResult.data;
                    wirteLockTime();
                }, throwable -> tip.postValue(throwable.getMessage()));

    }
}
